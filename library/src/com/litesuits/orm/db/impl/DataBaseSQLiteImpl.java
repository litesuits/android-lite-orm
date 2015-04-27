package com.litesuits.orm.db.impl;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import com.litesuits.android.log.Log;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.*;
import com.litesuits.orm.db.assit.Transaction.Worker;
import com.litesuits.orm.db.model.*;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.FieldUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 数据SQLite操作实现
 * 可查阅 <a href="http://www.sqlite.org/lang.html">SQLite操作指南</a>
 *
 * @author mty
 * @date 2013-6-2下午2:32:56
 */
public class DataBaseSQLiteImpl extends SQLiteClosable implements DataBase {

    public static final String TAG = DataBaseSQLiteImpl.class.getSimpleName();

    private SQLiteHelper mHelper;

    private DataBaseConfig mConfig;

    private TableManager mTableManager;

    protected DataBaseSQLiteImpl(DataBaseConfig config) {
        if (config.dbName == null) {
            config.dbName = DataBaseConfig.DEFAULT_DB_NAME;
        }
        if (config.dbVersion <= 0) {
            config.dbVersion = DataBaseConfig.DEFAULT_DB_VERSION;
        }
        mConfig = config;
        mHelper = new SQLiteHelper(mConfig.context.getApplicationContext(), mConfig.dbName, null, mConfig.dbVersion, config.onUpdateListener);
        mConfig.context = null;
        mTableManager = new TableManager(mConfig.dbName);
    }

    public synchronized static DataBaseSQLiteImpl newInstance(DataBaseConfig config) {
        return new DataBaseSQLiteImpl(config);
    }

    @Override
    public SQLStatement createSQLStatement(String sql, Object[] bindArgs) {
        return new SQLStatement(sql, bindArgs);
    }

    @Override
    public boolean execute(SQLiteDatabase db, SQLStatement statement) {
        acquireReference();
        try {
            if (statement != null) {
                return statement.execute(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return false;

    }

    @Override
    public boolean dropTable(Object entity) {
        acquireReference();
        try {
            return SQLBuilder.buildDropTable(TableManager.getTable(entity)).execute(mHelper.getWritableDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return false;
    }

    @Override
    public boolean dropTable(String tableName) {
        acquireReference();
        try {
            return SQLBuilder.buildDropTable(tableName).execute(mHelper.getWritableDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return false;
    }

    @Override
    public DataBase single() {
        return null;
    }

    @Override
    public DataBase cascade() {
        return null;
    }

    @Override
    public long save(Object entity) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            mTableManager.checkOrCreateTable(db, entity);
            return SQLBuilder.buildReplaceSql(entity).execInsertWithMapping(db, entity, mTableManager);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public int save(Collection<?> collection) {
        acquireReference();
        try {
            if (!Checker.isEmpty(collection)) {
                SQLiteDatabase db = mHelper.getWritableDatabase();
                Object entity = collection.iterator().next();
                SQLStatement stmt = SQLBuilder.buildReplaceAllSql(entity);
                mTableManager.checkOrCreateTable(db, entity);
                return stmt.execInsertCollection(db, collection, mTableManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public long insert(Object entity) {
        return insert(entity, null);
    }

    @Override
    public long insert(Object entity, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            mTableManager.checkOrCreateTable(db, entity);
            return SQLBuilder.buildInsertSql(entity, conflictAlgorithm).execInsertWithMapping(db, entity, mTableManager);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public int insert(Collection<?> collection) {
        return insert(collection, null);
    }

    @Override
    public int insert(Collection<?> collection, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            if (!Checker.isEmpty(collection)) {
                SQLiteDatabase db = mHelper.getWritableDatabase();
                Object entity = collection.iterator().next();
                SQLStatement stmt = SQLBuilder.buildInsertAllSql(entity, conflictAlgorithm);
                mTableManager.checkOrCreateTable(db, entity);
                return stmt.execInsertCollection(db, collection, mTableManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public int update(Object entity) {
        return update(entity, null, null);
    }

    @Override
    public int update(Object entity, ConflictAlgorithm conflictAlgorithm) {
        return update(entity, null, conflictAlgorithm);
    }

    @Override
    public int update(Object entity, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            return SQLBuilder.buildUpdateSql(entity, cvs, conflictAlgorithm).execUpdateWithMapping(db, entity, mTableManager);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public int update(Collection<?> collection) {
        return update(collection, null, null);
    }

    @Override
    public int update(Collection<?> collection, ConflictAlgorithm conflictAlgorithm) {
        return update(collection, null, conflictAlgorithm);
    }

    @Override
    public int update(Collection<?> collection, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            if (!Checker.isEmpty(collection)) {
                SQLiteDatabase db = mHelper.getWritableDatabase();
                Object entity = collection.iterator().next();
                SQLStatement stmt = SQLBuilder.buildUpdateAllSql(entity, cvs, conflictAlgorithm);
                return stmt.execUpdateCollection(db, collection, cvs, mTableManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * 主动删除其mapping数据
     */
    @Override
    public int delete(Object entity) {
        acquireReference();
        try {
            return SQLBuilder.buildDeleteSql(entity).execDeleteWithMapping(mHelper.getWritableDatabase(), entity, mTableManager);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public int delete(Class<?> claxx) {
        return deleteAll(claxx);
    }

    @Override
    public int delete(final Collection<?> collection) {
        acquireReference();
        try {
            if (!Checker.isEmpty(collection)) {
                EntityTable table = TableManager.getTable(collection.iterator().next());
                if (table.key != null) {
                    SQLStatement stmt = SQLBuilder.buildDeleteSql(collection);
                    return stmt.execDeleteCollection(mHelper.getWritableDatabase(), collection, mTableManager);
                } else {
                    Integer size = Transaction.execute(mHelper.getWritableDatabase(), new Worker<Integer>() {
                        @Override
                        public Integer doTransaction(SQLiteDatabase db) throws Exception {
                            for (Object entity : collection) {
                                SQLBuilder.buildDeleteSql(entity).execDeleteWithMapping(db, entity, mTableManager);
                            }
                            if (Log.isPrint) {
                                Log.i(TAG, "Exec delete(no primarykey) ：" + collection.size());
                            }
                            return collection.size();
                        }
                    });
                    return size == null ? 0 : size;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public int delete(Class<?> claxx, WhereBuilder where) {
        acquireReference();
        try {
            EntityTable table = TableManager.getTable(claxx);
            if (table.key != null && !Checker.isEmpty(table.mappingList)) {
                List<?> list = query(QueryBuilder.create(claxx).columns(new String[]{table.key.column}).where(where));
                delete(list);
            } else {
                return where.createStatementDelete(claxx).execDelete(mHelper.getWritableDatabase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public int deleteAll(Class<?> claxx) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            SQLStatement stmt = SQLBuilder.buildDeleteAllSql(claxx);
            int num = stmt.execDelete(db);
            // 删除关系映射
            final MapInfo mapTable = SQLBuilder.buildDelAllMappingSql(claxx);
            if (mapTable != null && !mapTable.isEmpty()) {
                Transaction.execute(db, new Transaction.Worker<Boolean>() {
                    @Override
                    public Boolean doTransaction(SQLiteDatabase db) throws Exception {
                        if (mapTable.delOldRelationSQL != null) {
                            for (SQLStatement st : mapTable.delOldRelationSQL) {
                                long rowId = st.execDelete(db);
                                if (Log.isPrint) {
                                    Log.i(TAG, "Exec delete mapping success, nums: " + rowId);
                                }
                            }
                        }
                        return true;
                    }
                });
            }
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    /**
     * 删除从[start,end]的数据
     * 此方法暂不会删除关联映射表里的关系数据
     */
    @Override
    public int delete(Class<?> claxx, long start, long end, String orderAscColumn) {
        acquireReference();
        try {
            if (start < 0 || end < start) { throw new RuntimeException("start must >=0 and smaller than end"); }
            if (start != 0) {
                start -= 1;
            }
            end = end == Integer.MAX_VALUE ? -1 : end - start;
            SQLStatement stmt = SQLBuilder.buildDeleteSql(claxx, start, end, orderAscColumn);
            return stmt.execDelete(mHelper.getWritableDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public long queryCount(Class<?> claxx) {
        return queryCount(new QueryBuilder(claxx));
    }

    @Override
    public long queryCount(QueryBuilder qb) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            SQLStatement stmt = qb.createStatementForCount();
            return stmt.queryForLong(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public <T> ArrayList<T> query(QueryBuilder qb) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //mTableManager.checkOrCreateTable(db, qb.getQueryClass());
        return qb.createStatement().query(db, qb.getQueryClass());
    }

    @Override
    public <T> T queryById(long id, Class<T> claxx) {
        return queryById(String.valueOf(id), claxx);
    }

    @Override
    public <T> T queryById(String id, Class<T> claxx) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            EntityTable table = TableManager.getTable(claxx);
            SQLStatement stmt = new QueryBuilder(claxx).where(table.key.column + "=?", new String[]{id}).createStatement();
            ArrayList<T> list = stmt.query(db, claxx);
            if (!Checker.isEmpty(list)) {
                return list.get(0);
            }
        } finally {
            releaseReference();
        }
        return null;
    }

    @Override
    public <T> ArrayList<T> query(Class<T> claxx) {
        acquireReference();
        try {
            SQLStatement stmt = new QueryBuilder(claxx).createStatement();
            return stmt.query(mHelper.getReadableDatabase(), claxx);
        } finally {
            releaseReference();
        }
    }

    @Override
    public ArrayList<Relation> queryRelation(Class class1, Class class2, List<String> key1List, List<String> key2List) {
        acquireReference();
        try {
            SQLStatement stmt = SQLBuilder.buildQueryRelationSql(class1, class2, key1List, key2List);
            final EntityTable table1 = TableManager.getTable(class1);
            final EntityTable table2 = TableManager.getTable(class2);
            final ArrayList<Relation> list = new ArrayList<Relation>();
            Querier.doQuery(mHelper.getReadableDatabase(), stmt, new Querier.CursorParser() {
                @Override
                public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                    Relation relation = new Relation();
                    relation.key1 = c.getString(c.getColumnIndex(table1.name));
                    relation.key2 = c.getString(c.getColumnIndex(table2.name));
                    list.add(relation);
                }
            });
            return list;
        } finally {
            releaseReference();
        }
    }

    @Override
    public <E, T> boolean mapping(Collection<E> col1, Collection<T> col2) {
        if (Checker.isEmpty(col1) || Checker.isEmpty(col2)) {
            return false;
        }
        acquireReference();
        try {
            return keepMapping(col1, col2) | keepMapping(col2, col1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return false;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        return mHelper.getReadableDatabase();
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        return mHelper.getWritableDatabase();
    }

    @Override
    public TableManager getTableManager() {
        return mTableManager;
    }

    @Override
    public SQLiteHelper getSQLiteHelper() {
        return mHelper;
    }

    @Override
    public DataBaseConfig getDataBaseConfig() {
        return mConfig;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SQLiteDatabase createDatabase() {
        return openOrCreateDatabase(mConfig.dbName, null, null);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SQLiteDatabase openOrCreateDatabase(String path, SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        acquireReference();
        try {
            File dbf = new File(path);
            File dbp = dbf.getParentFile();
            if (!dbp.exists()) {
                dbp.mkdirs();
            }
            if (!dbf.exists()) {
                dbf.createNewFile();
            }
            return SQLiteDatabase.openOrCreateDatabase(path, factory, errorHandler);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return null;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean deleteDatabase(File file) {
        acquireReference();
        try {
            return SQLiteDatabase.deleteDatabase(file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return false;
    }

    @Override
    public synchronized void close() {
        releaseReference();
    }

    /**
     * refCountIsZero 降到0时自动触发释放各种资源
     */
    @Override
    protected void onAllReferencesReleased() {
        mConfig = null;
        mHelper.close();
        mTableManager.clear();
    }


    /* --------------------------------  私有方法 -------------------------------- */

    private <E, T> boolean keepMapping(Collection<E> col1, Collection<T> col2) throws IllegalAccessException, InstantiationException {
        Class claxx1 = col1.iterator().next().getClass();
        Class claxx2 = col2.iterator().next().getClass();
        EntityTable table1 = TableManager.getTable(claxx1);
        EntityTable table2 = TableManager.getTable(claxx2);
        if (table1.mappingList != null) {
            for (MapProperty mp : table1.mappingList) {
                Class itemClass;
                Class fieldClass = mp.field.getType();
                if (mp.isToMany()) {
                    // N对多关系
                    if (ClassUtil.isCollection(fieldClass)) {
                        itemClass = FieldUtil.getGenericType(mp.field);
                    } else {
                        throw new RuntimeException("OneToMany and ManyToMany Relation, You must use collection object");
                    }
                } else {
                    itemClass = fieldClass;
                }
                if (itemClass == claxx2) {
                    ArrayList<String> key1List = new ArrayList<String>();
                    HashMap<String, Object> map1 = new HashMap<String, Object>();
                    // 构建第1个对象的key集合以及value映射
                    for (Object o1 : col1) {
                        if (o1 != null) {
                            Object key1 = FieldUtil.get(table1.key.field, o1);
                            if (key1 != null) {
                                key1List.add(key1.toString());
                                map1.put(key1.toString(), o1);
                            }
                        }
                    }
                    ArrayList<Relation> mapList = queryRelation(claxx1, claxx2, key1List, null);
                    if (!Checker.isEmpty(mapList)) {
                        HashMap<String, Object> map2 = new HashMap<String, Object>();
                        // 构建第2个对象的value映射
                        for (Object o2 : col2) {
                            if (o2 != null) {
                                Object key2 = FieldUtil.get(table2.key.field, o2);
                                if (key2 != null) {
                                    map2.put(key2.toString(), o2);
                                }
                            }
                        }
                        for (Relation m : mapList) {
                            Object obj1 = map1.get(m.key1);
                            Object obj2 = map2.get(m.key2);
                            if (obj1 != null && obj2 != null) {
                                if (mp.isToMany()) {
                                    // N对多关系
                                    if (ClassUtil.isCollection(fieldClass)) {
                                        Collection col = (Collection) FieldUtil.get(mp.field, obj1);
                                        if (col == null) {
                                            col = (Collection) fieldClass.newInstance();
                                            FieldUtil.set(mp.field, obj1, col);
                                        }
                                        col.add(obj2);
                                    } else {
                                        throw new RuntimeException("OneToMany and ManyToMany Relation, You must use collection object");
                                    }
                                } else {
                                    FieldUtil.set(mp.field, obj1, obj2);
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
