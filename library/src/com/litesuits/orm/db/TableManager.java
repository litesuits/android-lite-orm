package com.litesuits.orm.db;

import android.database.sqlite.SQLiteDatabase;
import com.litesuits.android.log.Log;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.litesuits.orm.db.assit.Transaction;
import com.litesuits.orm.db.assit.Transaction.Worker;
import com.litesuits.orm.db.impl.SQLStatement;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapProperty;
import com.litesuits.orm.db.model.Property;
import com.litesuits.orm.db.model.SQLiteTable;
import com.litesuits.orm.db.utils.FieldUtil;
import com.litesuits.orm.db.utils.TableUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 表管理
 *
 * @author MaTianyu
 * @date 2013-6-16上午12:27:32
 */
public final class TableManager {
    private static final String TAG = TableManager.class.getSimpleName();
    private static TableManager                 instance;
    /**
     * 这里放的是数据库表信息（表名、字段、建表语句...）
     */
    private        ArrayList<SQLiteTable>       mSqlTableList;
    /**
     * 这里放的是实体表信息（主键、属性、关系映射...）
     * key : Class Name
     * value: EntityTable
     */
    private        HashMap<String, EntityTable> mEntityTableMap;

    private TableManager() {
        mEntityTableMap = new HashMap<String, EntityTable>();
    }

    public synchronized static TableManager getInstance() {
        if (instance == null) {
            instance = new TableManager();
        }
        return instance;
    }

    /**
     * 清空数据
     */
    public synchronized void clear() {
        if (instance != null) {
            instance.mSqlTableList = null;
            instance.mEntityTableMap = null;
            instance = null;
        }
    }

    /**
     * 获取缓存实体表信息
     */
    private EntityTable getEntityTable(String name) {
        return mEntityTableMap.get(name);
    }

    /**
     * 缓存的实体表信息
     *
     * @return 返回前一个和此Key相同的Value，没有则返回null。
     */
    private EntityTable putEntityTable(String tableName, EntityTable entity) {
        return mEntityTableMap.put(tableName, entity);
    }

    public EntityTable getMappingTable(String tableName, String column1, String column2) {
        EntityTable table = getEntityTable(tableName);
        if (table == null) {
            table = new EntityTable();
            table.name = tableName;
            table.pmap = new LinkedHashMap<String, Property>();
            table.pmap.put(column1, null);
            table.pmap.put(column2, null);
            putEntityTable(tableName, table);
        }
        return table;
    }
    /**
     * 获取缓存实体表信息
     */
    public EntityTable getEntityTable(Class<?> claxx, boolean needPK) {
        EntityTable table = getEntityTable(claxx.getName());
        if (table == null) {
            table = new EntityTable();
            putEntityTable(claxx.getName(), table);
            table.claxx = claxx;
            table.name = TableUtil.getTableName(claxx);
            table.pmap = new LinkedHashMap<String, Property>();
            List<Field> fields = FieldUtil.getAllDeclaredFields(claxx);
            //Field[] fields = claxx.getDeclaredFields();
            for (Field f : fields) {
                if (FieldUtil.isInvalid(f)) {
                    continue;
                }
                Property p = new Property();
                p.field = f;
                // 获取列名,每个属性都有，没有注解默认取属性名
                Column col = f.getAnnotation(Column.class);
                if (col != null) {
                    p.column = col.value();
                } else {
                    p.column = f.getName();
                }

                // 主键判断
                PrimaryKey key = f.getAnnotation(PrimaryKey.class);
                if (key != null) {
                    // 主键不加入属性Map
                    table.key = new com.litesuits.orm.db.model.PrimaryKey(p, key.value());
                    // 主键为系统分配，必须为Long型
                    if (table.key.isAssignedBySystem()) {
                        if (table.key.field.getType() != Long.class && table.key.field.getType() != long.class) {
                            throw new RuntimeException(
                                    PrimaryKey.AssignType.AUTO_INCREMENT
                                            + "要求主键属性必须是Long型( the primary key should be Long or long...)\n 提示：把你的主键设置为Long或long型"
                            );
                        }
                    }
                } else {
                    //ORM handle
                    Mapping mapping = f.getAnnotation(Mapping.class);
                    if (mapping != null) {
                        table.addMapping(new MapProperty(p, mapping.value()));
                    } else {
                        table.pmap.put(p.column, p);
                    }
                }
            }
        }
        if (needPK && table.key == null) {
            throw new RuntimeException(
                    "你必须设置主键(you must set the primary key...)\n 提示：在对象的属性上加PrimaryKey注解来设置主键。");
        }
        return table;
    }


    ///**
    // * 缓存的实体表信息
    // * @return 返回前一个和此Key相同的Value，没有则返回null。
    // */
    //public EntityTable putEntityTable(Class<?> claxx, EntityTable entity) {
    //	return putEntityTable(claxx.getName(), entity);
    //}

    /**
     * 检测表是否建立，没有则建一张新表。
     *
     * @param db
     * @param entity
     */
    public EntityTable checkOrCreateTable(SQLiteDatabase db, Object entity) {
        // 关键点1：初始化全部数据库表
        initAllTablesFromSQLite(db);
        EntityTable table = TableUtil.getTable(entity);
        // table lock synchronized
        synchronized (table) {
            // 关键点2:判断表是否存在，是否需要新加列。
            if (!checkExistAndColumns(db, table)) {
                // 关键点3：新建表并加入表队列
                if (createTable(db, table)) {
                    putSqlTableIntoList(table);
                }
            }
        }
        return table;
    }

    /**
     * 检测映射表是否建立，没有则建一张新表。
     *
     * @param db
     * @param tableName
     * @param column1
     * @param column2
     * @return
     */
    public void checkOrCreateMappingTable(SQLiteDatabase db, String tableName, String column1,
                                          String column2) {
        // 关键点1：初始化全部数据库表
        initAllTablesFromSQLite(db);
        EntityTable table = TableUtil.getMappingTable(tableName, column1, column2);
        synchronized (table) {
            // 关键点2:判断表是否存在，是否需要新加列。
            if (!checkExistAndColumns(db, table)) {
                // 关键点3：新建表并加入表队列
                if (createTable(db, table)) {
                    putSqlTableIntoList(table);
                }
            }
        }
    }

    /**
     * 检查表是否存在，存在的话检查是否需要改动，添加列字段。
     * 注：sqlite仅仅支持表改名、表添加列两种alter方式。表中修改、刪除列是不被直接支持的。
     * 不能新加主键：The column may not have a PRIMARY KEY or UNIQUE constraint.
     * <p> http://www.sqlite.org/lang_altertable.html
     */
    private boolean checkExistAndColumns(SQLiteDatabase db, EntityTable entityTable) {
        if (!Checker.isEmpty(mSqlTableList)) {
            for (SQLiteTable sqlTable : mSqlTableList) {
                if (entityTable.name.equals(sqlTable.name)) {
                    if (Log.isPrint) Log.d(TAG, "Table [" + entityTable.name + "] Exist");
                    if (!sqlTable.isTableChecked) {
                        // 表仅进行一次检查，检验是否有新字段加入。
                        sqlTable.isTableChecked = true;
                        if (Log.isPrint) Log.d(TAG, "Table [" + entityTable.name + "] check column now.");
                        ArrayList<String> newColumns = null;
                        if (entityTable.pmap != null) {
                            for (String col : entityTable.pmap.keySet()) {
                                if (!sqlTable.columns.contains(col)) {
                                    if (newColumns == null) newColumns = new ArrayList<String>();
                                    newColumns.add(col);
                                }
                            }
                        }
                        if (!Checker.isEmpty(newColumns)) {
                            sqlTable.columns.addAll(newColumns);
                            int sum = insertNewColunms(db, entityTable.name, newColumns);
                            if (Log.isPrint) Log.i(TAG, "Table [" + entityTable.name + "] add " + sum + " new column");
                        }
                    }
                    return true;
                }
            }
        }
        if (Log.isPrint) Log.d(TAG, "Table [" + entityTable.name + "] Not Exist");
        return false;
    }

    /**
     * 将Sql Table放入存储集合
     *
     * @param table
     */
    private void putSqlTableIntoList(EntityTable table) {
        if (Log.isPrint) Log.i(TAG, "Table [" + table.name + "] Create Success");
        SQLiteTable sqlTable = new SQLiteTable();
        sqlTable.name = table.name;
        sqlTable.columns = new ArrayList<String>();
        if (table.key != null) sqlTable.columns.add(table.key.column);
        if (table.pmap != null) {
            for (String col : table.pmap.keySet()) {
                sqlTable.columns.add(col);
            }
        }
        if (mSqlTableList != null) mSqlTableList.add(sqlTable);
    }

    /**
     * global lock synchronized
     *
     * @param db
     */
    private void initAllTablesFromSQLite(SQLiteDatabase db) {
        synchronized (instance) {
            if (Checker.isEmpty(mSqlTableList)) mSqlTableList = TableUtil.getAllTablesFromSQLite(db);
        }
    }

    /**
     * 插入新列
     *
     * @param tableName
     * @param columns
     * @return
     */
    private int insertNewColunms(SQLiteDatabase db, final String tableName, final List<String> columns) {
        Integer size = null;
        if (!Checker.isEmpty(columns)) {
            size = Transaction.execute(db, new Worker<Integer>() {
                @Override
                public Integer doTransaction(SQLiteDatabase db) {
                    for (String c : columns) {
                        SQLStatement stmt = SQLBuilder.buildAddColumnSql(tableName, c);
                        stmt.execute(db);
                    }
                    return columns.size();
                }
            });
        }
        return size == null ? 0 : size;
    }

    /**
     * 建立新表
     */
    private boolean createTable(SQLiteDatabase db, EntityTable table) {
        return SQLBuilder.buildCreateTable(table).execute(db);
    }

}
