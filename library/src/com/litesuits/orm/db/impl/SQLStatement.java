package com.litesuits.orm.db.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.litesuits.android.log.Log;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.Querier;
import com.litesuits.orm.db.assit.Querier.CursorParser;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.litesuits.orm.db.assit.Transaction;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapInfo;
import com.litesuits.orm.db.model.MapInfo.MapTable;
import com.litesuits.orm.db.model.Property;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * sql语句构造与执行
 *
 * @author mty
 * @date 2013-6-14下午7:48:34
 */
public class SQLStatement implements Serializable {
    private static final long   serialVersionUID = -3790876762607683712L;
    private static final String TAG              = SQLStatement.class.getSimpleName();
    public static final  short  NONE             = -1;
    /**
     * sql语句
     */
    public String          sql;
    /**
     * sql语句中占位符对应的参数
     */
    public Object[]        bindArgs;
    /**
     * sql语句执行者，私有(private)。
     */
    public SQLiteStatement mStatement;

    public SQLStatement() {}

    public SQLStatement(String sql, Object[] args) {
        this.sql = sql;
        this.bindArgs = args;
    }

    /**
     * 给sql语句的占位符(?)按序绑定值
     *
     * @param i The 1-based index to the parameter to bind null to
     * @param o
     * @throws Exception
     */
    public void bind(int i, Object o) throws Exception {
        switch (DataUtil.getType(o)) {
            case DataUtil.FIELD_TYPE_NULL:
                mStatement.bindNull(i);
                break;
            case DataUtil.FIELD_TYPE_STRING:
                mStatement.bindString(i, String.valueOf(o));
                break;
            case DataUtil.FIELD_TYPE_LONG:
                mStatement.bindLong(i, ((Number) o).longValue());
                break;
            case DataUtil.FIELD_TYPE_REAL:
                mStatement.bindDouble(i, ((Number) o).doubleValue());
                break;
            case DataUtil.FIELD_TYPE_DATE:
                mStatement.bindLong(i, ((Date) o).getTime());
                break;
            case DataUtil.FIELD_TYPE_BLOB:
                mStatement.bindBlob(i, (byte[]) o);
                break;
            case DataUtil.FIELD_TYPE_SERIALIZABLE:
                mStatement.bindBlob(i, DataUtil.objectToByte(o));
                break;
            default:
                break;
        }
    }

    /**
     * 执行插入单个数据，返回rawid
     *
     * @param db
     * @param entity
     * @return
     * @throws Exception
     */
    public long execInsertWithMapping(SQLiteDatabase db, Object entity, TableManager tableManager) throws Exception {
        printSQL();
        mStatement = db.compileStatement(sql);
        Object keyObj = null;
        if (!Checker.isEmpty(bindArgs)) {
            keyObj = bindArgs[0];
            for (int i = 0; i < bindArgs.length; i++) {
                bind(i + 1, bindArgs[i]);
            }
        }
        long rowID = mStatement.executeInsert();
        clearArgs();
        if (Log.isPrint) Log.i(TAG, "SQL Execute Insert --> " + rowID);
        if (entity != null) {
            FieldUtil.setKeyValueIfneed(entity, TableManager.getTable(entity).key, keyObj, rowID);
            mapRelationToDb(entity, true, true, db, tableManager);
        }

        return rowID;
    }

    /**
     * 目前可用于给对象持久化映射关系时，因为不传入实体所以不可以为之注入ID。
     *
     * @param db
     * @return
     * @throws Exception
     */
    public long execInsert(SQLiteDatabase db) throws Exception {
        return execInsertWithMapping(db, null, null);
    }

    /**
     * 执行批量插入
     *
     * @param db
     * @return
     */
    public int execInsertCollection(SQLiteDatabase db, Collection<?> list, TableManager tableManager) {
        printSQL();
        db.beginTransaction();
        if (Log.isPrint) Log.d(TAG, "----> BeginTransaction[insert col]");
        try {
            mStatement = db.compileStatement(sql);
            Iterator<?> it = list.iterator();
            boolean tableCheck = true;
            EntityTable table = null;
            while (it.hasNext()) {
                mStatement.clearBindings();
                Object obj = it.next();

                if (table == null) {
                    table = TableManager.getTable(obj);
                    tableManager.checkOrCreateTable(db, obj);
                }

                int j = 1;
                Object keyObj = null;
                if (table.key != null) {
                    keyObj = FieldUtil.getAssignedKeyObject(table.key, obj);
                    bind(j++, keyObj);
                }
                if (!Checker.isEmpty(table.pmap)) {
                    // 第一个是主键。其他属性从2开始。
                    for (Property p : table.pmap.values()) {
                        bind(j++, FieldUtil.get(p.field, obj));
                    }
                }
                long rowID = mStatement.executeInsert();
                FieldUtil.setKeyValueIfneed(obj, table.key, keyObj, rowID);

                mapRelationToDb(obj, true, tableCheck, db, tableManager);
                tableCheck = false;
            }
            if (Log.isPrint) Log.i(TAG, "Exec insert " + list.size() + " rows , SQL: " + sql);
            db.setTransactionSuccessful();
            if (Log.isPrint) Log.d(TAG, "----> BeginTransaction[insert col] Successful");
            return list.size();
        } catch (Exception e) {
            if (Log.isPrint) Log.e(TAG, "----> BeginTransaction[insert col] Failling");
            e.printStackTrace();
        } finally {
            clearArgs();
            db.endTransaction();
        }
        return NONE;
    }

    /**
     * 执行更新单个数据，返回受影响的行数
     *
     * @param db
     * @param entity
     * @return
     * @throws Exception
     */
    public int execUpdateWithMapping(SQLiteDatabase db, Object entity, TableManager tableManager) throws Exception {
        printSQL();
        mStatement = db.compileStatement(sql);
        if (!Checker.isEmpty(bindArgs)) {
            for (int i = 0; i < bindArgs.length; i++) {
                bind(i + 1, bindArgs[i]);
            }
        }
        int rows = mStatement.executeUpdateDelete();
        clearArgs();
        if (Log.isPrint) Log.i(TAG, "SQL Execute update --> " + rows);
        if (entity != null) {
            mapRelationToDb(entity, true, true, db, tableManager);
        }
        return rows;
    }

    /**
     * 执行批量更新
     *
     * @param db
     * @return
     */
    public int execUpdateCollection(SQLiteDatabase db, Collection<?> list, ColumnsValue cvs, TableManager tableManager) {
        printSQL();
        db.beginTransaction();
        if (Log.isPrint) Log.d(TAG, "----> BeginTransaction[update col]");
        try {
            mStatement = db.compileStatement(sql);
            Iterator<?> it = list.iterator();
            boolean tableCheck = true;
            EntityTable table = null;
            boolean hasCol = cvs != null && cvs.checkColumns();
            boolean hasVal = hasCol && cvs.hasValues();
            while (it.hasNext()) {
                mStatement.clearBindings();
                Object obj = it.next();
                if (table == null) {
                    table = TableManager.getTable(obj);
                    tableManager.checkOrCreateTable(db, obj);
                }
                int j = 1;
                // 此种情况下，bindArgs非空表明开发者设置了默认值
                if (hasCol) {
                    for (int i = 0; i < cvs.columns.length; i++) {
                        Object v = null;
                        if (hasVal) v = cvs.values[i];
                        if (v == null) {
                            Field field = table.pmap.get(cvs.columns[i]).field;
                            v = FieldUtil.get(field, obj);
                        }
                        bind(j++, v);
                    }
                } else if (!Checker.isEmpty(table.pmap)) {
                    // 第一个是主键。其他属性从2开始。
                    for (Property p : table.pmap.values()) {
                        bind(j++, FieldUtil.get(p.field, obj));
                    }
                }
                if (table.key != null) {
                    bind(j, FieldUtil.getAssignedKeyObject(table.key, obj));
                }
                mStatement.executeUpdateDelete();

                mapRelationToDb(obj, true, tableCheck, db, tableManager);
                tableCheck = false;
            }
            if (Log.isPrint) Log.i(TAG, "Exec update " + list.size() + " rows , SQL: " + sql);
            db.setTransactionSuccessful();
            if (Log.isPrint) Log.d(TAG, "----> BeginTransaction[update col] Successful");
            return list.size();
        } catch (Exception e) {
            if (Log.isPrint) Log.e(TAG, "----> BeginTransaction[update col] Failling");
            e.printStackTrace();
        } finally {
            clearArgs();
            db.endTransaction();
        }
        return NONE;
    }

    /**
     * 删除语句执行，返回受影响的行数
     *
     * @param db
     * @return
     * @throws Exception
     */
    public int execDelete(SQLiteDatabase db) throws Exception {
        return execDeleteWithMapping(db, null, null);
    }

    /**
     * 执行删操作.(excute delete ...)，返回受影响的行数
     * 并将关系映射删除
     *
     * @param db
     * @throws Exception
     */
    public int execDeleteWithMapping(final SQLiteDatabase db, Object entity, TableManager tableManager) throws Exception {
        printSQL();
        mStatement = db.compileStatement(sql);
        if (bindArgs != null) {
            for (int i = 0; i < bindArgs.length; i++) {
                bind(i + 1, bindArgs[i]);
            }
        }
        int nums = mStatement.executeUpdateDelete();
        if (Log.isPrint) Log.i(TAG, "SQL Execute Delete --> " + nums);
        clearArgs();
        if (entity != null) {
            // 删除关系映射
            mapRelationToDb(entity, false, false, db, tableManager);
        }
        return nums;
    }

    /**
     * 执行删操作.(excute delete ...)，返回受影响的行数
     * 并将关系映射删除
     *
     * @param db
     * @throws Exception
     */
    public int execDeleteCollection(final SQLiteDatabase db, final Collection<?> collection, final TableManager tableManager) throws Exception {
        printSQL();
        // 删除全部数据
        mStatement = db.compileStatement(sql);
        if (bindArgs != null) {
            for (int i = 0; i < bindArgs.length; i++) {
                bind(i + 1, bindArgs[i]);
            }
        }
        int nums = mStatement.executeUpdateDelete();
        if (Log.isPrint) Log.i(TAG, "SQL Execute Delete --> " + nums);
        clearArgs();
        // 删除关系映射
        MapInfo mapTable = SQLBuilder.buildMappingSql(collection.iterator().next(), true);
        if (mapTable != null && !mapTable.isEmpty()) {
            Boolean suc = Transaction.execute(db, new Transaction.Worker<Boolean>() {
                @Override
                public Boolean doTransaction(SQLiteDatabase db) throws Exception {
                    for (Object o : collection) {
                        // 删除关系映射
                        mapRelationToDb(o, false, false, db, tableManager);
                    }
                    return true;
                }
            });
            if (Log.isPrint) Log.i(TAG, "Exec delete collection mapping: " + ((suc != null && suc) ? "成功" : "失败"));
        } else {
            Log.i(TAG, "此对象组不包含关系映射");
        }
        return nums;
    }

    /**
     * 执行create,drop table 等
     *
     * @param db
     */
    public boolean execute(SQLiteDatabase db) {
        printSQL();
        mStatement = db.compileStatement(sql);
        try {
            if (bindArgs != null) {
                for (int i = 0; i < bindArgs.length; i++) {
                    bind(i + 1, bindArgs[i]);
                }
            }
            mStatement.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clearArgs();
        }
        return false;
    }

    /**
     * Execute a statement that returns a 1 by 1 table with a numeric value.
     * For example, SELECT COUNT(*) FROM table;
     *
     * @param db
     * @return
     */
    public long queryForLong(SQLiteDatabase db) {
        printSQL();
        long count = 0;
        try {
            mStatement = db.compileStatement(sql);
            if (bindArgs != null) {
                for (int i = 0; i < bindArgs.length; i++) {
                    bind(i + 1, bindArgs[i]);
                }
            }
            count = mStatement.simpleQueryForLong();
            if (Log.isPrint) Log.i(TAG, "SQL Execute queryForLong --> " + count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clearArgs();
        }
        return count;
    }

    /**
     * 执行查询
     * 根据类信息读取数据库，取出全部本类的对象。
     *
     * @param claxx
     * @return
     */
    public <T> ArrayList<T> query(SQLiteDatabase db, final Class<T> claxx) {
        printSQL();
        final ArrayList<T> list = new ArrayList<T>();
        try {
            final EntityTable table = TableManager.getTable(claxx, false);
            Querier.doQuery(db, this, new CursorParser() {
                @Override
                public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                    T t = ClassUtil.newInstance(claxx);
                    DataUtil.injectDataToObject(c, t, table);
                    list.add(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public String toString() {
        return "SQLStatement [sql=" + sql + ", bindArgs=" + Arrays.toString(bindArgs) + ", mStatement=" + mStatement
                + "]";
    }
    /*------------------------------ 私有方法 ------------------------------*/

    /**
     * 重新映射关系到数据库
     *
     * @param entity
     * @param insertNew 仅在执行删除该实体时，此值为false
     * @param db
     */
    private void mapRelationToDb(Object entity, final boolean insertNew, final boolean tableCheck, SQLiteDatabase db, final TableManager tableManager) {
        // 插入关系映射
        final MapInfo mapTable = SQLBuilder.buildMappingSql(entity, insertNew);
        if (mapTable != null && !mapTable.isEmpty()) {
            Transaction.execute(db, new Transaction.Worker<Boolean>() {
                @Override
                public Boolean doTransaction(SQLiteDatabase db) throws Exception {
                    if (insertNew && tableCheck) {
                        for (MapTable table : mapTable.tableList) {
                            tableManager.checkOrCreateMappingTable(db, table.name, table.column1,
                                    table.column2);
                        }
                    }
                    if (mapTable.delOldRelationSQL != null) {
                        for (SQLStatement st : mapTable.delOldRelationSQL) {
                            long rowId = st.execDelete(db);
                            if (Log.isPrint) Log.v(TAG, "Exec delete mapping success, nums: " + rowId);
                        }
                    }
                    if (insertNew && mapTable.mapNewRelationSQL != null) {
                        for (SQLStatement st : mapTable
                                .mapNewRelationSQL) {
                            long rowId = st.execInsert(db);
                            if (Log.isPrint) Log.v(TAG, "Exec save mapping success, nums: " + rowId);
                        }
                    }
                    return true;
                }
            });
        }
    }

    private void printSQL() {
        if (Log.isPrint) Log.d(TAG, "SQL Execute: [" + sql + "] ARGS--> " + Arrays.toString(bindArgs));
    }

    private void clearArgs() {
        if (mStatement != null) mStatement.close();
        sql = null;
        bindArgs = null;
        mStatement = null;
    }


}
