package com.litesuits.orm.db.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.litesuits.android.log.Log;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.Querier;
import com.litesuits.orm.db.assit.Querier.CursorParser;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.litesuits.orm.db.impl.SQLStatement;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.SQLiteColumn;
import com.litesuits.orm.db.model.SQLiteTable;

import java.util.ArrayList;

/**
 * 表工具
 *
 * @author MaTianyu  2013-6-8
 */
public class TableUtil {
    public static final String TAG = TableUtil.class.getSimpleName();

    /**
     * 根据类自动生成表名字
     */
    public static String getTableName(Class<?> claxx) {
        Table anno = claxx.getAnnotation(Table.class);
        if (anno != null) {
            return anno.value();
        } else {
            return claxx.getName().replaceAll("\\.", "_");
        }
    }

    /**
     * 根据实体生成表信息,一定需要PrimaryKey
     */
    public static EntityTable getTable(Object entity) {
        return getTable(entity.getClass(), true);
    }

    /**
     * 根据类生成表信息,一定需要PrimaryKey
     */
    public static EntityTable getTable(Class<?> claxx) {
        return getTable(claxx, true);
    }

    /**
     * 根据类生成表信息
     *
     * @param claxx
     * @param needPK
     * @return
     */
    public static EntityTable getTable(Class<?> claxx, boolean needPK) {
        return TableManager.getInstance().getEntityTable(claxx, needPK);
    }

    /**
     * 获取关系映射表
     *
     * @param tableName
     * @param column1
     * @param column2
     * @return
     */
    public static EntityTable getMappingTable(String tableName, String column1, String column2) {
        return TableManager.getInstance().getMappingTable(tableName,column1,column2);
    }

    /**
     * 初始化全部表及其列名,初始化失败，则无法进行下去。
     *
     * @throws Exception
     */
    public static ArrayList<SQLiteTable> getAllTablesFromSQLite(SQLiteDatabase db) {
        SQLStatement st = SQLBuilder.buildTableObtainAll();
        final EntityTable table = TableUtil.getTable(SQLiteTable.class, false);
        final ArrayList<SQLiteTable> list = new ArrayList<SQLiteTable>();
        if (Log.isPrint) Log.i(TAG, "Initialize SQL table start--------------------->");
        Querier.doQuery(db, st, new CursorParser() {
            @Override
            public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                SQLiteTable sqlTable = new SQLiteTable();
                DataUtil.injectDataToObject(c, sqlTable, table);
                ArrayList<String> colS = getAllColumnsFromSQLite(db, sqlTable.name);
                if (Checker.isEmpty(colS)) {
                    // 如果读数据库失败了，那么解析建表语句
                    colS = transformSqlToColumns(sqlTable.sql);
                }
                sqlTable.columns = colS;
                if (Log.isPrint) Log.d(TAG, "Find One SQL Table: " + sqlTable);
                list.add(sqlTable);
            }
        });
        if (Log.isPrint) Log.i(TAG, "Initialize SQL table end  ---------------------> " + list.size());
        return list;
    }

    /**
     * 数据库分析
     * 通过读数据库得到一张表的全部列名
     *
     * @param db
     * @param tableName
     * @return
     */
    public static ArrayList<String> getAllColumnsFromSQLite(SQLiteDatabase db, final String tableName) {
        SQLStatement st = SQLBuilder.buildColumnsObtainAll(tableName);
        final EntityTable table = TableUtil.getTable(SQLiteColumn.class, false);
        final ArrayList<String> list = new ArrayList<String>();
        Querier.doQuery(db, st, new CursorParser() {
            @Override
            public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                SQLiteColumn col = new SQLiteColumn();
                DataUtil.injectDataToObject(c, col, table);
                list.add(col.name);
            }
        });
        return list;
    }

    /**
     * 语义分析
     * 依据表的sql“CREATE TABLE”建表语句得到一张表的全部列名。
     *
     * @param sql
     * @return
     */
    public static ArrayList<String> transformSqlToColumns(String sql) {
        if (sql != null) {
            int start = sql.indexOf("(");
            int end = sql.lastIndexOf(")");
            if (start > 0 && end > 0) {
                sql = sql.substring(start + 1, end);
                String cloumns[] = sql.split(",");
                ArrayList<String> colList = new ArrayList<String>();
                for (String col : cloumns) {
                    col = col.trim();
                    int endS = col.indexOf(" ");
                    if (endS > 0) {
                        col = col.substring(0, endS);
                    }
                    colList.add(col);
                }
                Log.w(TAG, "降级：语义分析表结构（" + colList.toString() + " , Origin SQL is: " + sql);
                return colList;
            }
        }
        return null;
    }

    public static String getMapTableName(Class c1, Class c2) {
        return getMapTableName(getTableName(c1), getTableName(c2));
    }

    public static String getMapTableName(EntityTable t1, EntityTable t2) {
        return getMapTableName(t1.name, t2.name);
    }

    public static String getMapTableName(String tableName1, String tableName2) {
        if (tableName1.compareTo(tableName2) < 0) return tableName1 + "_" + tableName2;
        else return tableName2 + "_" + tableName1;
    }
}
