package com.litesuits.orm.db.assit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.util.Log;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.Querier.CursorParser;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapInfo;
import com.litesuits.orm.db.model.MapInfo.MapTable;
import com.litesuits.orm.db.model.Property;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;
import com.litesuits.orm.log.OrmLog;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * sql语句构造与执行
 *
 * @author mty
 * @date 2013-6-14下午7:48:34
 */
public class SQLStatement implements Serializable {
    private static final long serialVersionUID = -3790876762607683712L;
    private static final String TAG = SQLStatement.class.getSimpleName();
    public static final short NONE = -1;
    public static final short NORMAL = 0;
    public static final int IN_TOP_LIMIT = 999;
    /**
     * sql语句
     */
    public String sql;
    /**
     * sql语句中占位符对应的参数
     */
    public Object[] bindArgs;
    /**
     * sql语句执行者，私有(private)。
     */
    private SQLiteStatement mStatement;

    public SQLStatement() {}

    public SQLStatement(String sql, Object[] args) {
        this.sql = sql;
        this.bindArgs = args;
    }

    /**
     * 给sql语句的占位符(?)按序绑定值
     *
     * @param i The 1-based index to the parameter to bind null to
     */
    protected void bind(int i, Object o) throws IOException {
        if (o == null) {
            mStatement.bindNull(i);
        } else if (o instanceof CharSequence || o instanceof Boolean || o instanceof Character) {
            mStatement.bindString(i, String.valueOf(o));
        } else if (o instanceof Float || o instanceof Double) {
            mStatement.bindDouble(i, ((Number) o).doubleValue());
        } else if (o instanceof Number) {
            mStatement.bindLong(i, ((Number) o).longValue());
        } else if (o instanceof Date) {
            mStatement.bindLong(i, ((Date) o).getTime());
        } else if (o instanceof byte[]) {
            mStatement.bindBlob(i, (byte[]) o);
        } else if (o instanceof Serializable) {
            mStatement.bindBlob(i, DataUtil.objectToByte(o));
        } else {
            mStatement.bindNull(i);
        }
    }

    /**
     * 插入数据，未传入实体所以不可以为之注入ID。
     */
    public long execInsert(SQLiteDatabase db) throws IOException, IllegalAccessException {
        return execInsertWithMapping(db, null, null);
    }


    /**
     * 插入数据，并为实体对象为之注入ID（如果需要）。
     */
    public long execInsert(SQLiteDatabase db, Object entity) throws IOException, IllegalAccessException {
        return execInsertWithMapping(db, entity, null);
    }

    /**
     * 插入数据，为其注入ID（如果需要），关系表也一并处理。
     */
    public long execInsertWithMapping(SQLiteDatabase db, Object entity, TableManager tableManager)
            throws IllegalAccessException, IOException {
        printSQL();
        mStatement = db.compileStatement(sql);
        Object keyObj = null;
        if (!Checker.isEmpty(bindArgs)) {
            keyObj = bindArgs[0];
            for (int i = 0; i < bindArgs.length; i++) {
                bind(i + 1, bindArgs[i]);
            }
        }
        //OrmLog.i(TAG, "SQL Execute bind over ");
        long rowID = NONE;
        try {
            rowID = mStatement.executeInsert();
        } finally {
            realease();
        }
        //OrmLog.i(TAG, "SQL Execute insert over ");
        if (OrmLog.isPrint) {
            OrmLog.i(TAG, "SQL Execute Insert RowID --> " + rowID + "    sql: " + sql);
        }
        if (entity != null) {
            FieldUtil.setKeyValueIfneed(entity, TableManager.getTable(entity).key, keyObj, rowID);
        }
        if (tableManager != null) {
            mapRelationToDb(entity, true, true, db, tableManager);
        }
        return rowID;
    }


    /**
     * 执行批量插入
     */
    public int execInsertCollection(SQLiteDatabase db, Collection<?> list) {
        return execInsertCollectionWithMapping(db, list, null);
    }

    public int execInsertCollectionWithMapping(SQLiteDatabase db, Collection<?> list, TableManager tableManager) {
        printSQL();
        db.beginTransaction();
        if (OrmLog.isPrint) {
            OrmLog.i(TAG, "----> BeginTransaction[insert col]");
        }
        EntityTable table = null;
        try {
            mStatement = db.compileStatement(sql);
            Iterator<?> it = list.iterator();
            boolean mapTableCheck = true;
            while (it.hasNext()) {
                mStatement.clearBindings();
                Object obj = it.next();

                if (table == null) {
                    table = TableManager.getTable(obj);
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
                if (tableManager != null) {
                    mapRelationToDb(obj, true, mapTableCheck, db, tableManager);
                    mapTableCheck = false;
                }
            }
            if (OrmLog.isPrint) {
                OrmLog.i(TAG, "Exec insert [" + list.size() + "] rows , SQL: " + sql);
            }
            db.setTransactionSuccessful();
            if (OrmLog.isPrint) {
                OrmLog.i(TAG, "----> BeginTransaction[insert col] Successful");
            }
            return list.size();
        } catch (Exception e) {
            if (OrmLog.isPrint) {
                OrmLog.e(TAG, "----> BeginTransaction[insert col] Failling");
            }
            e.printStackTrace();
        } finally {
            realease();
            db.endTransaction();
        }
        return NONE;
    }

    /**
     * 执行更新单个数据，返回受影响的行数
     */
    public int execUpdate(SQLiteDatabase db) throws IOException {
        return execUpdateWithMapping(db, null, null);
    }

    /**
     * 执行更新单个数据，返回受影响的行数
     */
    public int execUpdateWithMapping(SQLiteDatabase db, Object entity, TableManager tableManager) throws IOException {
        printSQL();
        mStatement = db.compileStatement(sql);
        if (!Checker.isEmpty(bindArgs)) {
            for (int i = 0; i < bindArgs.length; i++) {
                bind(i + 1, bindArgs[i]);
            }
        }
        int rows = NONE;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mStatement.execute();
            rows = NORMAL;
        } else {
            rows = mStatement.executeUpdateDelete();
        }
        realease();
        if (OrmLog.isPrint) {
            OrmLog.i(TAG, "SQL Execute update, changed rows --> " + rows);
        }
        if (tableManager != null && entity != null) {
            mapRelationToDb(entity, true, true, db, tableManager);
        }
        return rows;
    }

    /**
     * 执行批量更新
     */
    public int execUpdateCollection(SQLiteDatabase db, Collection<?> list, ColumnsValue cvs) {
        return execUpdateCollectionWithMapping(db, list, cvs, null);
    }

    /**
     * 执行批量更新
     */
    public int execUpdateCollectionWithMapping(SQLiteDatabase db, Collection<?> list,
                                               ColumnsValue cvs, TableManager tableManager) {
        printSQL();
        db.beginTransaction();
        if (OrmLog.isPrint) {
            OrmLog.d(TAG, "----> BeginTransaction[update col]");
        }
        try {
            mStatement = db.compileStatement(sql);
            Iterator<?> it = list.iterator();
            boolean mapTableCheck = true;
            EntityTable table = null;
            while (it.hasNext()) {
                mStatement.clearBindings();
                Object obj = it.next();
                if (table == null) {
                    table = TableManager.getTable(obj);
                }
                bindArgs = SQLBuilder.buildUpdateSqlArgsOnly(obj, cvs);
                if (!Checker.isEmpty(bindArgs)) {
                    for (int i = 0; i < bindArgs.length; i++) {
                        bind(i + 1, bindArgs[i]);
                    }
                }
                mStatement.execute();
                if (tableManager != null) {
                    mapRelationToDb(obj, true, mapTableCheck, db, tableManager);
                    mapTableCheck = false;
                }
            }
            if (OrmLog.isPrint) {
                OrmLog.i(TAG, "Exec update [" + list.size() + "] rows , SQL: " + sql);
            }
            db.setTransactionSuccessful();
            if (OrmLog.isPrint) {
                OrmLog.d(TAG, "----> BeginTransaction[update col] Successful");
            }
            return list.size();
        } catch (Exception e) {
            if (OrmLog.isPrint) {
                OrmLog.e(TAG, "----> BeginTransaction[update col] Failling");
            }
            e.printStackTrace();
        } finally {
            realease();
            db.endTransaction();
        }
        return NONE;
    }

    /**
     * 删除语句执行，返回受影响的行数
     */
    public int execDelete(SQLiteDatabase db) throws IOException {
        return execDeleteWithMapping(db, null, null);
    }

    /**
     * 执行删操作.(excute delete ...)，返回受影响的行数
     * 并将关系映射删除
     */
    public int execDeleteWithMapping(final SQLiteDatabase db, Object entity, TableManager tableManager)
            throws IOException {
        printSQL();
        mStatement = db.compileStatement(sql);
        if (bindArgs != null) {
            for (int i = 0; i < bindArgs.length; i++) {
                bind(i + 1, bindArgs[i]);
            }
        }
        int nums = NONE;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mStatement.execute();
            nums = NORMAL;
        } else {
            nums = mStatement.executeUpdateDelete();
        }
        if (OrmLog.isPrint) {
            OrmLog.v(TAG, "SQL execute delete, changed rows--> " + nums);
        }
        realease();
        if (tableManager != null && entity != null) {
            // 删除关系映射
            mapRelationToDb(entity, false, false, db, tableManager);
        }
        return nums;
    }

    /**
     * 执行删操作.(excute delete ...)，返回受影响的行数
     * 并将关系映射删除
     */
    public int execDeleteCollection(final SQLiteDatabase db, final Collection<?> collection) throws IOException {
        return execDeleteCollectionWithMapping(db, collection, null);
    }

    /**
     * 执行删操作.(excute delete ...)，返回受影响的行数
     * 并将关系映射删除
     */
    public int execDeleteCollectionWithMapping(final SQLiteDatabase db, final Collection<?> collection,
                                               final TableManager tableManager) throws IOException {
        printSQL();
        // 删除全部数据
        mStatement = db.compileStatement(sql);
        if (bindArgs != null) {
            for (int i = 0; i < bindArgs.length; i++) {
                bind(i + 1, bindArgs[i]);
            }
        }
        int nums;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mStatement.execute();
            nums = collection.size();
        } else {
            nums = mStatement.executeUpdateDelete();
        }
        if (OrmLog.isPrint) {
            OrmLog.v(TAG, "SQL execute delete, changed rows --> " + nums);
        }
        realease();
        if (tableManager != null) {
            // 删除关系映射
            Boolean suc = Transaction.execute(db, new Transaction.Worker<Boolean>() {
                @Override
                public Boolean doTransaction(SQLiteDatabase db) throws Exception {
                    boolean mapTableCheck = true;
                    for (Object o : collection) {
                        // 删除关系映射
                        mapRelationToDb(o, false, mapTableCheck, db, tableManager);
                        mapTableCheck = false;
                    }
                    return true;
                }
            });
            if (OrmLog.isPrint) {
                OrmLog.i(TAG, "Exec delete collection mapping: " + ((suc != null && suc) ? "成功" : "失败"));
            }
        }
        return nums;
    }

    /**
     * 执行create,drop table 等
     */
    public boolean execute(SQLiteDatabase db) {
        printSQL();
        try {
            mStatement = db.compileStatement(sql);
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
            realease();
        }
        return false;
    }

    /**
     * Execute a statement that returns a 1 by 1 table with a numeric value.
     * For example, SELECT COUNT(*) FROM table;
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
            if (OrmLog.isPrint) {
                OrmLog.i(TAG, "SQL execute query for count --> " + count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realease();
        }
        return count;
    }

    /**
     * 执行查询
     * 根据类信息读取数据库，取出全部本类的对象。
     */
    public <T> ArrayList<T> query(SQLiteDatabase db, final Class<T> claxx) {
        printSQL();
        final ArrayList<T> list = new ArrayList<T>();
        try {
            final EntityTable table = TableManager.getTable(claxx, false);
            Querier.doQuery(db, this, new CursorParser() {
                @Override
                public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                    //long start = System.nanoTime();
                    T t = ClassUtil.newInstance(claxx);
                    //Log.i(TAG, "parse new after  " + ((System.nanoTime() - start)/1000));
                    //start = System.nanoTime();
                    DataUtil.injectDataToObject(c, t, table);
                    //Log.i(TAG, "parse inject after  " +  ((System.nanoTime() - start)/1000));
                    list.add(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 执行查询
     * 根据类信息读取数据库，取出本类的对象。
     */
    public <T> T queryOneEntity(SQLiteDatabase db, final Class<T> claxx) {
        printSQL();
        final EntityTable table = TableManager.getTable(claxx, false);
        T t = Querier.doQuery(db, this, new CursorParser<T>() {
            T t;

            @Override
            public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                t = ClassUtil.newInstance(claxx);
                DataUtil.injectDataToObject(c, t, table);
                stopParse();
            }

            @Override
            public T returnResult() {
                return t;
            }
        });
        return t;
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
     * @param insertNew 仅在执行删除该实体时，此值为false
     */
    private void mapRelationToDb(Object entity, final boolean insertNew,
                                 final boolean tableCheck, SQLiteDatabase db,
                                 final TableManager tableManager) {
        // 插入关系映射
        final MapInfo mapTable = SQLBuilder.buildMappingInfo(entity, insertNew, tableManager);
        if (mapTable != null && !mapTable.isEmpty()) {
            Transaction.execute(db, new Transaction.Worker<Boolean>() {
                @Override
                public Boolean doTransaction(SQLiteDatabase db) throws Exception {
                    if (insertNew && tableCheck) {
                        for (MapTable table : mapTable.tableList) {
                            tableManager.checkOrCreateMappingTable(db, table.name, table.column1, table.column2);
                        }
                    }
                    if (mapTable.delOldRelationSQL != null) {
                        for (SQLStatement st : mapTable.delOldRelationSQL) {
                            long rowId = st.execDelete(db);
                            if (OrmLog.isPrint) {
                                OrmLog.v(TAG, "Exec delete mapping success, nums: " + rowId);
                            }
                        }
                    }
                    if (insertNew && mapTable.mapNewRelationSQL != null) {
                        for (SQLStatement st : mapTable.mapNewRelationSQL) {
                            long rowId = st.execInsert(db);
                            if (OrmLog.isPrint) {
                                OrmLog.v(TAG, "Exec save mapping success, nums: " + rowId);
                            }
                        }
                    }
                    return true;
                }
            });
        }
    }

    private void printSQL() {
        if (OrmLog.isPrint) {
            OrmLog.d(TAG, "SQL Execute: [" + sql + "] ARGS--> " + Arrays.toString(bindArgs));
        }
    }

    private void realease() {
        if (mStatement != null) {
            mStatement.close();
        }
        //sql = null;
        bindArgs = null;
        mStatement = null;
    }


}
