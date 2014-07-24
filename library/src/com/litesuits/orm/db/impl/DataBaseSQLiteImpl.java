package com.litesuits.orm.db.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.litesuits.android.log.Log;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.*;
import com.litesuits.orm.db.assit.Transaction.Worker;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapInfo;
import com.litesuits.orm.db.model.MapProperty;
import com.litesuits.orm.db.model.Relation;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.FieldUtil;
import com.litesuits.orm.db.utils.TableUtil;

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
public final class DataBaseSQLiteImpl extends SQLiteClosable implements DataBase {

    public static final String TAG = DataBaseSQLiteImpl.class.getSimpleName();

    private SQLiteHelper mHelper;

    private DataBaseConfig mConfig;

    private TableManager mTableManager;

    private static DataBaseSQLiteImpl instance;

    private DataBaseSQLiteImpl(DataBaseConfig config) {
        mConfig = config;
        mHelper = new SQLiteHelper(mConfig.context.getApplicationContext(), mConfig.dbName
                , null, mConfig.dbVersion, config.onUpdateListener);
        //		mRDb = mHelper.getReadableDatabase();
        //		mWDb = mHelper.getWritableDatabase();
        mConfig.context = null;
        mTableManager = TableManager.getInstance();
    }

    public synchronized static DataBaseSQLiteImpl newInstance(DataBaseConfig config) {
        if (instance == null) {
            instance = new DataBaseSQLiteImpl(config);
        }
        return instance;
    }

    @Override
    public long save(Object entity) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            mTableManager.checkOrCreateTable(db, entity);
            return SQLBuilder.buildSaveSql(entity).execInsertWithMapping(db, entity);
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
                SQLStatement stmt = SQLBuilder.buildSaveAllSql(entity);
                mTableManager.checkOrCreateTable(db, entity);
                return stmt.execInsertCollection(db, collection);
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
            return delete(entity, mHelper.getWritableDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    private int delete(Object entity, SQLiteDatabase db) throws Exception {
        return SQLBuilder.buildDeleteSql(entity).execDeleteWithMapping(db, entity);
    }

    @Override
    public int delete(final Collection<?> collection) {
        acquireReference();
        try {
            if (!Checker.isEmpty(collection)) {
                EntityTable table = TableUtil.getTable(collection.iterator().next());
                if (table.key != null) {
                    SQLStatement stmt = SQLBuilder.buildDeleteSql(collection);
                    return stmt.execDeleteCollection(mHelper.getWritableDatabase(), collection);
                } else {
                    Integer size = Transaction.execute(mHelper.getWritableDatabase(), new Worker<Integer>() {
                        @Override
                        public Integer doTransaction(SQLiteDatabase db) throws Exception {
                            for (Object entity : collection) {
                                delete(entity, db);
                            }
                            if (Log.isPrint) Log.i(TAG, "Exec delete(no primarykey) ：" + collection.size());
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
                                if (Log.isPrint) Log.i(TAG, "Exec delete mapping success, nums: " + rowId);
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
     * 删除从（start,end]的数据
     * 此方法暂不会删除关联映射表里的关系数据
     *
     * @param claxx
     * @param start
     * @param end
     * @return
     */
    @Override
    public int delete(Class<?> claxx, int start, int end) {
        acquireReference();
        try {
            if (start < 0 || end < start) { throw new RuntimeException("start must >=0 and smaller than end"); }
            SQLStatement stmt = SQLBuilder.buildDeleteSql(claxx, start, end);
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
        try{
        SQLStatement stmt = new QueryBuilder(claxx).createStatementForCount();
        return stmt.queryForLong(mHelper.getReadableDatabase(), claxx);
        }catch (Exception e){
            e.printStackTrace();
        }
        return SQLStatement.NONE;
    }

    @Override
    public <T> ArrayList<T> query(Class<T> claxx, Collection<?> collection, QueryBuilder qb) {
        return null;
    }

    @Override
    public <T> ArrayList<T> query(Class<T> claxx) {
        SQLStatement stmt = new QueryBuilder(claxx).createStatement();
        return stmt.query(mHelper.getReadableDatabase(), claxx);
    }

    @Override
    public ArrayList<Relation> queryRelation(Class class1, Class class2, List<String> key1List, List<String> key2List) {
        final EntityTable table1 = TableUtil.getTable(class1);
        final EntityTable table2 = TableUtil.getTable(class2);
        QueryBuilder builder = new QueryBuilder(class1).queryMappingInfo(class2);
        ArrayList<String> keyList = new ArrayList<String>();
        StringBuilder sb = null;
        if (!Checker.isEmpty(key1List)) {
            sb = new StringBuilder();
            sb.append(table1.name);
            sb.append(" IN ( ");
            for (int i = 0, size = key1List.size(); i < size; i++) {
                if (i == 0) {
                    sb.append("?");
                } else {
                    sb.append(",?");
                }
            }
            sb.append(" ) ");
            keyList.addAll(key1List);
        }
        if (!Checker.isEmpty(key2List)) {
            if (sb == null) sb = new StringBuilder();
            else sb.append(" AND ");

            sb.append(table2.name);
            sb.append(" IN (");
            for (int i = 0, size = key2List.size(); i < size; i++) {
                if (i == 0) {
                    sb.append("?");
                } else {
                    sb.append(",?");
                }
            }
            sb.append(")");
            keyList.addAll(key2List);
        }
        //ArrayList<Long> longs = new ArrayList<Long>();
        //for(String s : keyList){
        //    longs.add(Long.parseLong(s));
        //}
        if (sb != null) builder.where(sb.toString(), keyList.toArray(new String[0]));
        final ArrayList<Relation> list = new ArrayList<Relation>();
        Querier.doQuery(mHelper.getReadableDatabase(), builder.createStatement(), new Querier.CursorParser() {
            @Override
            public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                Relation relation = new Relation();
                relation.key1 = c.getString(c.getColumnIndex(table1.name));
                relation.key2 = c.getString(c.getColumnIndex(table2.name));
                list.add(relation);
            }
        });
        return list;
    }

    @Override
    public <E, T> boolean mapping(Collection<E> col1, Collection<T> col2) {
        if (Checker.isEmpty(col1) || Checker.isEmpty(col2)) return false;
        try {
            return keepMapping(col1, col2) | keepMapping(col2, col1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private <E, T> boolean keepMapping(Collection<E> col1, Collection<T> col2) throws IllegalAccessException, InstantiationException {
        Class claxx1 = col1.iterator().next().getClass();
        Class claxx2 = col2.iterator().next().getClass();
        EntityTable table1 = TableUtil.getTable(claxx1);
        EntityTable table2 = TableUtil.getTable(claxx2);
        if (table1.mappingList != null) {
            for (MapProperty mp : table1.mappingList) {
                Class itemClass;
                Class fieldClass = mp.field.getType();
                if (mp.isToMany()) {
                    // N对多关系
                    if (ClassUtil.isCollection(fieldClass)) {
                        itemClass = FieldUtil.getGenericType(mp.field);
                    } else {
                        throw new RuntimeException("OneToMany and ManyToMany Relation, You must use array or collection object");
                    }
                } else {
                    itemClass = fieldClass;
                }
                if (itemClass == claxx2) {
                    ArrayList<String> key1List = new ArrayList<String>();
                    for (Object e : col1) {
                        if (e != null) {
                            Object key1 = FieldUtil.get(table1.key.field, e);
                            if (key1 != null) {
                                key1List.add(String.valueOf(key1));
                            }
                        }
                    }
                    ArrayList<Relation> mapList = queryRelation(claxx1, claxx2, key1List, null);
                    if (!Checker.isEmpty(mapList)) {
                        HashMap<String, Object> map1 = new HashMap<String, Object>();
                        HashMap<String, Object> map2 = new HashMap<String, Object>();
                        for (Relation m : mapList) {
                            for (Object e : col1) {
                                if (e != null) {
                                    Object key1 = FieldUtil.get(table1.key.field, e);
                                    if (key1 != null && key1.toString().equals(m.key1)) {
                                        map1.put(m.key1, e);
                                    }
                                }
                            }
                            for (Object t : col2) {
                                if (t != null) {
                                    Object key2 = FieldUtil.get(table2.key.field, t);
                                    if (key2 != null && key2.toString().equals(m.key2)) {
                                        map2.put(m.key2, t);
                                    }
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
                                        throw new RuntimeException("OneToMany and ManyToMany Relation, You must use array or collection object");
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

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        return mHelper.getReadableDatabase();
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        return mHelper.getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        releaseReference();
    }

    @Override
    protected void onAllReferencesReleased() {
        if (instance != null) {
            instance.mConfig = null;
            instance.mHelper.close();
            instance.mTableManager.clear();
            instance = null;
        }
    }

}
