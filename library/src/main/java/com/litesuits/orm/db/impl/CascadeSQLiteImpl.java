package com.litesuits.orm.db.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.*;
import com.litesuits.orm.db.assit.Transaction.Worker;
import com.litesuits.orm.db.model.*;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 数据SQLite操作关联实现
 * 可查阅 <a href="http://www.sqlite.org/lang.html">SQLite操作指南</a>
 *
 * @author MaTianyu
 * @date 2015-03-13
 */
public final class CascadeSQLiteImpl extends LiteOrm {

    public static final String TAG = CascadeSQLiteImpl.class.getSimpleName();

    protected CascadeSQLiteImpl(LiteOrm dataBase) {
        super(dataBase);
    }

    private CascadeSQLiteImpl(DataBaseConfig config) {
        super(config);
    }

    public synchronized static LiteOrm newInstance(DataBaseConfig config) {
        return new CascadeSQLiteImpl(config);
    }

    @Override
    public LiteOrm single() {
        if (otherDatabase == null) {
            otherDatabase = new SingleSQLiteImpl(this);
        }
        return otherDatabase;
    }

    @Override
    public LiteOrm cascade() {
        return this;
    }

    @Override
    public long save(final Object entity) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            Long rowID = Transaction.execute(db, new Worker<Long>() {
                @Override
                public Long doTransaction(SQLiteDatabase db) throws Exception {
                    HashMap<String, Integer> handleMap = new HashMap<String, Integer>();
                    return checkTableAndSaveRecursive(entity, db, handleMap);
                }
            });
            return rowID == null ? SQLStatement.NONE : rowID;
        } finally {
            releaseReference();
        }
    }

    @Override
    public <T> int save(Collection<T> collection) {
        acquireReference();
        try {
            return saveCollection(collection);
        } finally {
            releaseReference();
        }
    }

    @Override
    public long insert(Object entity) {
        return insert(entity, null);
    }

    @Override
    public long insert(final Object entity, final ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            Long rowID = Transaction.execute(db, new Worker<Long>() {
                @Override
                public Long doTransaction(SQLiteDatabase db) throws Exception {
                    mTableManager.checkOrCreateTable(db, entity);
                    return insertRecursive(SQLBuilder.buildInsertSql(entity, conflictAlgorithm),
                            entity, db, new HashMap<String, Integer>());
                }
            });
            return rowID == null ? SQLStatement.NONE : rowID;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public <T> int insert(Collection<T> collection) {
        return insert(collection, null);
    }

    @Override
    public <T> int insert(Collection<T> collection, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            return insertCollection(collection, conflictAlgorithm);
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
    public int update(final Object entity, final ColumnsValue cvs, final ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            Integer rowID = Transaction.execute(db, new Worker<Integer>() {
                @Override
                public Integer doTransaction(SQLiteDatabase db) throws Exception {
                    HashMap<String, Integer> handleMap = new HashMap<String, Integer>();
                    SQLStatement stmt = SQLBuilder.buildUpdateSql(entity, cvs, conflictAlgorithm);
                    mTableManager.checkOrCreateTable(db, entity);
                    return updateRecursive(stmt, entity, db, handleMap);
                }
            });
            return rowID == null ? SQLStatement.NONE : rowID;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public <T> int update(Collection<T> collection) {
        return update(collection, null, null);
    }

    @Override
    public <T> int update(Collection<T> collection, ConflictAlgorithm conflictAlgorithm) {
        return update(collection, null, conflictAlgorithm);
    }

    @Override
    public <T> int update(Collection<T> collection, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            return updateCollection(collection, cvs, conflictAlgorithm);
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
    public int delete(final Object entity) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            Integer rowID = Transaction.execute(db, new Worker<Integer>() {
                @Override
                public Integer doTransaction(SQLiteDatabase db) throws Exception {
                    HashMap<String, Integer> handleMap = new HashMap<String, Integer>();
                    return checkTableAndDeleteRecursive(entity, db, handleMap);
                }
            });
            if (rowID != null) {
                return rowID;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public <T> int delete(Class<T> claxx) {
        return deleteAll(claxx);
    }


    @Override
    public <T> int delete(final Collection<T> collection) {
        acquireReference();
        try {
            return deleteCollectionIfTableHasCreated(collection);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public <T> int delete(Class<T> claxx, WhereBuilder where) {
        acquireReference();
        try {
            EntityTable table = TableManager.getTable(claxx);
            List<T> list = query(QueryBuilder.create(claxx).columns(new String[]{table.key.column}).where(where));
            delete(list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public int delete(WhereBuilder where) {
        acquireReference();
        try {
            EntityTable table = TableManager.getTable(where.getTableClass());
            List<?> list = query(QueryBuilder
                    .create(where.getTableClass())
                    .columns(new String[]{table.key.column})
                    .where(where));
            deleteCollectionIfTableHasCreated(list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public <T> int deleteAll(Class<T> claxx) {
        acquireReference();
        try {
            final EntityTable table = TableManager.getTable(claxx);
            List<T> list = query(QueryBuilder.create(claxx).columns(new String[]{table.key.column}));
            return delete(list);
        } finally {
            releaseReference();
        }
    }

    /**
     * 删除从[start,end]的数据
     * 此方法暂不会删除关联映射表里的关系数据
     */
    @Override
    public <T> int delete(Class<T> claxx, long start, long end, String orderAscColumn) {
        acquireReference();
        try {
            if (start < 0 || end < start) {
                throw new RuntimeException("start must >=0" +
                        " and smaller than end");
            }
            if (start != 0) {
                start -= 1;
            }
            end = end == Integer.MAX_VALUE ? -1 : end - start;
            final EntityTable table = TableManager.getTable(claxx);
            List<T> list = query(QueryBuilder
                    .create(claxx)
                    .limit(start + SQLBuilder.COMMA + end)
                    .appendOrderAscBy(orderAscColumn)
                    .columns(new String[]{table.key.column}));
            return delete(list);
        } finally {
            releaseReference();
        }
    }

    @Override
    public <T> ArrayList<T> query(Class<T> claxx) {
        return checkTableAndQuery(claxx, new QueryBuilder<T>(claxx));
    }

    @Override
    public <T> ArrayList<T> query(QueryBuilder<T> qb) {
        return checkTableAndQuery(qb.getQueryClass(), qb);
    }

    @Override
    public <T> T queryById(long id, Class<T> claxx) {
        return queryById(String.valueOf(id), claxx);
    }

    @Override
    public <T> T queryById(String id, Class<T> claxx) {
        EntityTable table = TableManager.getTable(claxx);
        ArrayList<T> list = checkTableAndQuery(claxx, new QueryBuilder<T>(claxx)
                .whereEquals(table.key.column, String.valueOf(id)));
        if (!Checker.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /* --------------------------------  私有方法: 查询相关 -------------------------------- */

    /**
     * 过程：
     * <p/>
     * 1. 根据条件查找符合条件的所有当前对象
     * 2. 遍历所有当前对象，遍历其所有关联对象，读取关系表 map:<key1, key2>
     * 3. 如果是多对一，根据map查找key2的关联对象，赋给obj1
     * 4. 如果是一对多，根据map查找key2的关联对象，反射实例化obj1的容器，关联对象放入。
     * 5. 并对关联对象递归此过程
     */
    private <T> ArrayList<T> checkTableAndQuery(final Class<T> claxx, QueryBuilder builder) {
        acquireReference();
        final ArrayList<T> list = new ArrayList<T>();
        try {
            final EntityTable table = TableManager.getTable(claxx, false);
            if (mTableManager.isSQLTableCreated(table.name)) {
                final HashMap<String, Object> entityMap = new HashMap<String, Object>();
                final HashMap<String, Integer> queryMap = new HashMap<String, Integer>();
                SQLiteDatabase db = mHelper.getReadableDatabase();
                Querier.doQuery(db, builder.createStatement(), new Querier.CursorParser() {
                    @Override
                    public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                        T t = ClassUtil.newInstance(claxx);
                        DataUtil.injectDataToObject(c, t, table);
                        list.add(t);
                        entityMap.put(table.name + FieldUtil.get(table.key.field, t), t);
                    }
                });
                for (T t : list) {
                    queryForMappingRecursive(t, db, queryMap, entityMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return list;
    }

    /**
     * 循环遍历查找当前实体的关联实体
     */
    private void queryForMappingRecursive(Object obj1, SQLiteDatabase db, HashMap<String, Integer> queryMap,
                                          HashMap<String, Object> entityMap)
            throws IllegalAccessException, InstantiationException {
        final EntityTable table1 = TableManager.getTable(obj1);
        Object key1 = FieldUtil.getAssignedKeyObject(table1.key, obj1);
        String key = table1.name + key1;
        if (queryMap.get(key) == null) {
            queryMap.put(key, 1);
            if (table1.mappingList != null) {
                for (MapProperty mp : table1.mappingList) {
                    if (mp.isToOne()) {
                        queryMapToOne(table1, key1, obj1, mp.field, db, queryMap, entityMap);
                    } else if (mp.isToMany()) {
                        queryMapToMany(table1, key1, obj1, mp.field, db, queryMap, entityMap);
                    }
                }
            }
        }
    }

    /**
     * 查找N对一关系的实体
     */
    private void queryMapToOne(final EntityTable table1, Object key1, Object obj1,
                               Field field, SQLiteDatabase db, HashMap<String, Integer> queryMap,
                               HashMap<String, Object> entityMap)
            throws IllegalAccessException, InstantiationException {
        final EntityTable table2 = TableManager.getTable(field.getType());
        if (mTableManager.isSQLMapTableCreated(table1.name, table2.name)) {
            SQLStatement relationSql = SQLBuilder.buildQueryRelationSql(table1, table2, key1);
            final RelationKey relation = new RelationKey();
            Querier.doQuery(db, relationSql, new Querier.CursorParser() {
                @Override
                public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                    relation.key1 = c.getString(c.getColumnIndex(table1.name));
                    relation.key2 = c.getString(c.getColumnIndex(table2.name));
                    stopParse();
                }
            });
            if (relation.isOK()) {
                String key = table2.name + relation.key2;
                Object obj2 = entityMap.get(key);
                if (obj2 == null) {
                    SQLStatement entitySql = SQLBuilder.buildQueryMapEntitySql(table2, relation.key2);
                    obj2 = entitySql.queryOneEntity(db, table2.claxx);
                    entityMap.put(key, obj2);
                }
                if (obj2 != null) {
                    FieldUtil.set(field, obj1, obj2);
                    queryForMappingRecursive(obj2, db, queryMap, entityMap);
                }
            }
        }
    }

    /**
     * 查找N对关系的实体
     */
    @SuppressWarnings("unchecked")
    private void queryMapToMany(final EntityTable table1, Object key1, Object obj1,
                                Field field, SQLiteDatabase db, HashMap<String, Integer> queryMap,
                                final HashMap<String, Object> entityMap)
            throws IllegalAccessException, InstantiationException {
        final Class<?> class2;
        if (Collection.class.isAssignableFrom(field.getType())) {
            class2 = FieldUtil.getGenericType(field);
        } else if (field.getType().isArray()) {
            class2 = FieldUtil.getComponentType(field);
        } else {
            throw new RuntimeException("OneToMany and ManyToMany Relation, " +
                    "you must use collection or array object");
        }
        final EntityTable table2 = TableManager.getTable(class2);
        if (mTableManager.isSQLMapTableCreated(table1.name, table2.name)) {
            SQLStatement relationSql = SQLBuilder.buildQueryRelationSql(table1, table2, key1);
            final ArrayList<String> key2List = new ArrayList<String>();
            Querier.doQuery(db, relationSql, new Querier.CursorParser() {
                @Override
                public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                    key2List.add(c.getString(c.getColumnIndex(table2.name)));
                }
            });
            if (!Checker.isEmpty(key2List)) {
                final ArrayList<Object> allList2 = new ArrayList<Object>();
                for (int i = key2List.size() - 1; i >= 0; i--) {
                    Object obj2 = entityMap.get(table2.name + key2List.get(i));
                    if (obj2 != null) {
                        allList2.add(obj2);
                        key2List.remove(i);
                    }
                }
                //final Class<?> class2 = compClass;
                int i = 0, start = 0, end;
                while (start < key2List.size()) {
                    int next = ++i * SQLStatement.IN_TOP_LIMIT;
                    end = Math.min(key2List.size(), next);
                    List<String> subList = key2List.subList(start, end);
                    start = next;

                    SQLStatement entitySql = QueryBuilder
                            .create(class2)
                            .whereIn(table2.key.column, subList.toArray(new String[subList.size()]))
                            .createStatement();

                    Querier.doQuery(db, entitySql, new Querier.CursorParser() {
                        @Override
                        public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                            Object t = ClassUtil.newInstance(class2);
                            DataUtil.injectDataToObject(c, t, table2);
                            allList2.add(t);
                            entityMap.put(table2.name + FieldUtil.get(table2.key.field, t), t);
                        }
                    });
                }
                if (!Checker.isEmpty(allList2)) {
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        Collection coll = (Collection) ClassUtil.newCollectionForField(field);
                        coll.addAll(allList2);
                        FieldUtil.set(field, obj1, coll);
                    } else if (field.getType().isArray()) {
                        Object[] arrObj = (Object[]) ClassUtil.newArray(class2, allList2.size());
                        arrObj = allList2.toArray(arrObj);
                        FieldUtil.set(field, obj1, arrObj);
                    } else {
                        throw new RuntimeException("OneToMany and ManyToMany Relation, " +
                                "you must use collection or array object");
                    }
                    for (Object obj2 : allList2) {
                        queryForMappingRecursive(obj2, db, queryMap, entityMap);
                    }
                }
            }
        }
    }

    /* --------------------------------  私有方法: 集合操作相关 -------------------------------- */

    /**
     * 将集合更高效地存储下来
     *
     * @param collection any collection
     * @return return size of collection if do successfully, -1 or not.
     */
    private <T> int saveCollection(final Collection<T> collection) {
        if (!Checker.isEmpty(collection)) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            Integer rowID = Transaction.execute(db, new Worker<Integer>() {
                @Override
                public Integer doTransaction(SQLiteDatabase db) throws Exception {
                    //0. 保存第一个实体
                    HashMap<String, Integer> handleMap = new HashMap<String, Integer>();
                    Iterator<T> iterator = collection.iterator();
                    Object entity = iterator.next();
                    SQLStatement stmt = SQLBuilder.buildReplaceSql(entity);
                    mTableManager.checkOrCreateTable(db, entity);
                    insertRecursive(stmt, entity, db, handleMap);

                    //1.0 保存剩余实体
                    while (iterator.hasNext()) {
                        entity = iterator.next();
                        //1.1 绑定对应值
                        stmt.bindArgs = SQLBuilder.buildInsertSqlArgsOnly(entity);
                        //1.2 保存当前实体
                        insertRecursive(stmt, entity, db, handleMap);
                    }
                    return collection.size();
                }
            });
            if (rowID != null) {
                return rowID;
            }
        }
        return SQLStatement.NONE;
    }

    /**
     * 将集合更高效地存储下来
     *
     * @param collection        any collection
     * @param conflictAlgorithm when conflict
     * @return return size of collection if do successfully, -1 or not.
     */
    private <T> int insertCollection(final Collection<T> collection, final ConflictAlgorithm conflictAlgorithm) {
        if (!Checker.isEmpty(collection)) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            Integer rowID = Transaction.execute(db, new Worker<Integer>() {
                @Override
                public Integer doTransaction(SQLiteDatabase db) throws Exception {
                    //0. 保存第一个实体
                    HashMap<String, Integer> handleMap = new HashMap<String, Integer>();
                    Iterator<T> iterator = collection.iterator();
                    Object entity = iterator.next();
                    SQLStatement stmt = SQLBuilder.buildInsertSql(entity, conflictAlgorithm);
                    mTableManager.checkOrCreateTable(db, entity);
                    insertRecursive(stmt, entity, db, handleMap);

                    //1.0 保存剩余实体
                    while (iterator.hasNext()) {
                        //1.1 绑定对应值
                        entity = iterator.next();
                        //1.2 保存当前实体
                        stmt.bindArgs = SQLBuilder.buildInsertSqlArgsOnly(entity);
                        insertRecursive(stmt, entity, db, handleMap);
                    }
                    return collection.size();
                }
            });
            if (rowID != null) {
                return rowID;
            }
        }
        return SQLStatement.NONE;
    }

    /**
     * 将集合更高效地存储下来
     *
     * @param collection        any collection
     * @param conflictAlgorithm when conflict
     * @return return size of collection if do successfully, -1 or not.
     */
    private <T> int updateCollection(final Collection<T> collection, final ColumnsValue cvs,
                                     final ConflictAlgorithm conflictAlgorithm) {
        if (!Checker.isEmpty(collection)) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            Integer rowID = Transaction.execute(db, new Worker<Integer>() {
                @Override
                public Integer doTransaction(SQLiteDatabase db) throws Exception {
                    //0. 保存第一个实体
                    HashMap<String, Integer> handleMap = new HashMap<String, Integer>();
                    Iterator<T> iterator = collection.iterator();
                    Object entity = iterator.next();
                    SQLStatement stmt = SQLBuilder.buildUpdateSql(entity, cvs, conflictAlgorithm);
                    mTableManager.checkOrCreateTable(db, entity);
                    updateRecursive(stmt, entity, db, handleMap);
                    //1.0 保存剩余实体
                    while (iterator.hasNext()) {
                        //1.1 绑定对应值
                        entity = iterator.next();
                        //1.2 保存当前实体
                        stmt.bindArgs = SQLBuilder.buildUpdateSqlArgsOnly(entity, cvs);
                        updateRecursive(stmt, entity, db, handleMap);
                    }
                    return collection.size();
                }
            });
            if (rowID != null) {
                return rowID;
            }
        }
        return SQLStatement.NONE;
    }

    /**
     * 将集合更高效地删除
     *
     * @param collection any collection
     * @return return size of collection if do successfully, -1 or not.
     */
    private <T> int deleteCollectionIfTableHasCreated(final Collection<T> collection) {
        if (!Checker.isEmpty(collection)) {
            final Iterator<T> iterator = collection.iterator();
            final Object entity = iterator.next();
            EntityTable table = TableManager.getTable(entity);
            if (mTableManager.isSQLTableCreated(table.name)) {
                SQLiteDatabase db = mHelper.getWritableDatabase();
                Integer rowID = Transaction.execute(db, new Worker<Integer>() {
                    @Override
                    public Integer doTransaction(SQLiteDatabase db) throws Exception {
                        //0. 删除第一个实体
                        HashMap<String, Integer> handleMap = new HashMap<String, Integer>();
                        SQLStatement stmt = SQLBuilder.buildDeleteSql(entity);
                        deleteRecursive(stmt, entity, db, handleMap);

                        //1.0 删除剩余实体
                        while (iterator.hasNext()) {
                            //1.1 绑定对应值
                            Object next = iterator.next();
                            //1.2 保存当前实体
                            stmt.bindArgs = getDeleteStatementArgs(next);
                            deleteRecursive(stmt, next, db, handleMap);
                        }
                        return collection.size();
                    }
                });
                if (rowID != null) {
                    return rowID;
                }
            }
        }
        return SQLStatement.NONE;
    }

    /**
     * 获取被删除对象的参数
     */
    public static Object[] getDeleteStatementArgs(Object entity) throws IllegalAccessException {
        EntityTable table = TableManager.getTable(entity);
        if (table.key != null) {
            return new String[]{String.valueOf(FieldUtil.get(table.key.field, entity))};
        } else if (!Checker.isEmpty(table.pmap)) {
            Object[] args = new Object[table.pmap.size()];
            int i = 0;
            for (Property p : table.pmap.values()) {
                args[i++] = FieldUtil.get(p.field, entity);
            }
            return args;
        }
        return null;
    }

    /* --------------------------------  私有方法：增删改相关 -------------------------------- */
    public static final int TYPE_INSERT = 1;
    public static final int TYPE_UPDATE = 2;
    public static final int TYPE_DELETE = 3;

    /**
     * 通过递归保存[该对象]，以及该对象所有的[关联对象]以及它们的[映射关系]
     *
     * @param obj1 需要保存的对象
     * @param db   可写数据库对象
     * @return rowID of entity
     */
    private long handleEntityRecursive(int type, SQLStatement stmt, Object obj1, SQLiteDatabase db,
                                       HashMap<String, Integer> handleMap) throws Exception {
        EntityTable table1 = TableManager.getTable(obj1);
        Object key1 = FieldUtil.get(table1.key.field, obj1);

        // 0. 若[当前实体]已存储过，不再操作
        if (handleMap.get(table1.name + key1) != null) {
            return SQLStatement.NONE;
        }
        // 1. 存储[当前实体]
        long rowID = SQLStatement.NONE;
        switch (type) {
            case TYPE_INSERT:
                rowID = stmt.execInsert(db, obj1);
                key1 = FieldUtil.get(table1.key.field, obj1);
                break;
            case TYPE_UPDATE:
                rowID = stmt.execUpdate(db);
                break;
            case TYPE_DELETE:
                rowID = stmt.execDelete(db);
                break;
            default:

        }
        handleMap.put(table1.name + key1, 1);
        // 2. 存储[关联实体]以及其[关系映射]
        boolean insertNew = type != TYPE_DELETE;
        handleMapping(key1, obj1, db, insertNew, handleMap);
        return rowID;
    }

    /**
     * 通过递归保更新[该对象]，保存该对象所有的[关联对象]以及它们的[映射关系]
     *
     * @param obj1 需要保存的对象
     * @param db   可写数据库对象
     * @return rowID of entity
     */
    private int updateRecursive(SQLStatement stmt, Object obj1, SQLiteDatabase db,
                                HashMap<String, Integer> handleMap) throws Exception {
        EntityTable table1 = TableManager.getTable(obj1);
        Object key1 = FieldUtil.get(table1.key.field, obj1);

        // 0. 若[当前实体]已存储过，不再操作
        if (handleMap.get(table1.name + key1) != null) {
            return SQLStatement.NONE;
        }
        // 1. 更新[当前实体]
        int rowID = stmt.execUpdate(db);
        key1 = FieldUtil.get(table1.key.field, obj1);
        handleMap.put(table1.name + key1, 1);

        // 2. 存储[关联实体]以及其[关系映射]
        handleMapping(key1, obj1, db, true, handleMap);
        return rowID;
    }

    /**
     * 通过递归删除[该对象]，以及该对象所有的[关联对象]以及它们的[映射关系]
     *
     * @param obj1 需要保存的对象
     * @param db   可写数据库对象
     * @return rowID of entity
     */
    private int deleteRecursive(SQLStatement stmt, Object obj1, SQLiteDatabase db,
                                HashMap<String, Integer> handleMap) throws Exception {
        EntityTable table1 = TableManager.getTable(obj1);
        Object key1 = FieldUtil.get(table1.key.field, obj1);

        // 0. 若[当前实体]已删除过，不再操作
        if (handleMap.get(table1.name + key1) != null) {
            return SQLStatement.NONE;
        }
        // 1. 删除[当前实体]
        int rowID = stmt.execDelete(db);
        handleMap.put(table1.name + key1, 1);

        // 2. 删除[关联实体]以及其[关系映射]
        handleMapping(key1, obj1, db, false, handleMap);
        return rowID;
    }

    /**
     * 通过递归保存[该对象]，以及该对象所有的[关联对象]以及它们的[映射关系]
     *
     * @param obj1 需要保存的对象
     * @param db   可写数据库对象
     * @return rowID of entity
     */
    private long insertRecursive(SQLStatement stmt, Object obj1, SQLiteDatabase db,
                                 HashMap<String, Integer> handleMap) throws Exception {
        EntityTable table1 = TableManager.getTable(obj1);
        Object key1 = FieldUtil.get(table1.key.field, obj1);

        // 0. 若[当前实体]已存储过，不再操作
        if (handleMap.get(table1.name + key1) != null) {
            return SQLStatement.NONE;
        }
        // 1. 存储[当前实体]
        long rowID = stmt.execInsert(db, obj1);
        key1 = FieldUtil.get(table1.key.field, obj1);
        handleMap.put(table1.name + key1, 1);


        // 2. 存储[关联实体]以及其[关系映射]
        handleMapping(key1, obj1, db, true, handleMap);
        return rowID;
    }

    /* --------------------------------  私有方法:处理关系 -------------------------------- */

    /**
     * 通过递归保存[该对象]，以及该对象所有的[关联对象]以及它们的[映射关系]
     *
     * @param obj1 需要保存的对象
     * @param db   可写数据库对象
     * @return rowID of entity
     */
    private long checkTableAndSaveRecursive(Object obj1, SQLiteDatabase db, HashMap<String, Integer> handleMap)
            throws Exception {
        mTableManager.checkOrCreateTable(db, obj1);
        return insertRecursive(SQLBuilder.buildReplaceSql(obj1), obj1, db, handleMap);
    }

    /**
     * 通过递归删除[该对象]，以及该对象所有的[关联对象]以及它们的[映射关系]
     *
     * @param obj1 需要保存的对象
     * @param db   可写数据库对象
     * @return rowID of entity
     */
    private int checkTableAndDeleteRecursive(Object obj1, SQLiteDatabase db,
                                             HashMap<String, Integer> handleMap)
            throws Exception {
        EntityTable table = TableManager.getTable(obj1);
        if (mTableManager.isSQLTableCreated(table.name)) {
            return deleteRecursive(SQLBuilder.buildDeleteSql(obj1), obj1, db, handleMap);
        }
        return SQLStatement.NONE;
    }

    /**
     * 处理一个实体中所有的关联实体。
     */
    private void handleMapping(Object key1, Object obj1, SQLiteDatabase db,
                               boolean insertNew, HashMap<String, Integer> handleMap)
            throws Exception {
        EntityTable table1 = TableManager.getTable(obj1);
        // 2. 存储[关联实体]以及其[关系映射]
        if (table1.mappingList != null) {
            for (MapProperty map : table1.mappingList) {
                if (map.isToOne()) {
                    // handle <one to one>,<many to one> relation.
                    Object obj2 = FieldUtil.get(map.field, obj1);
                    EntityTable table2 = TableManager.getTable(map.field.getType());
                    handleMapToOne(table1, table2, key1, obj2, db, insertNew, handleMap);
                } else if (map.isToMany()) {
                    // hanlde <one to many>,<many to many> relation.
                    Object array = FieldUtil.get(map.field, obj1);
                    if (ClassUtil.isCollection(map.field.getType())) {
                        EntityTable table2 = TableManager.getTable(FieldUtil.getGenericType(map.field));
                        handleMapToMany(table1, table2, key1, (Collection<?>) array, db, insertNew, handleMap);
                    } else if (ClassUtil.isArray(map.field.getType())) {
                        EntityTable table2 = TableManager.getTable(FieldUtil.getComponentType(map.field));
                        Collection<?> coll = null;
                        if (array != null) {
                            // 一定要强转为(Object[])
                            coll = Arrays.asList((Object[]) array);
                        }
                        handleMapToMany(table1, table2, key1, coll, db, insertNew, handleMap);
                    } else {
                        throw new RuntimeException("OneToMany and ManyToMany Relation, you must use collection or array object");
                    }
                }
            }
        }
    }

    /**
     * 处理N对1关系的关联实体
     */
    private void handleMapToOne(EntityTable table1, EntityTable table2, Object key1, Object obj2,
                                SQLiteDatabase db, boolean insertNew, HashMap<String, Integer> handleMap)
            throws Exception {
        if (obj2 != null) {
            // 注意：先递归处理关联对象（如果其主键无值，可以通过先处理赋值）
            if (insertNew) {
                // 递归存储[关联实体]
                checkTableAndSaveRecursive(obj2, db, handleMap);
            } else {
                // 递归删除[关联实体]
                checkTableAndDeleteRecursive(obj2, db, handleMap);
            }
        }
        // 现在处理(当前实体)和(关联对象)的[映射关系]
        String mapTableName = TableManager.getMapTableName(table1, table2);

        // 删掉旧的[映射关系]
        mTableManager.checkOrCreateMappingTable(db, mapTableName, table1.name, table2.name);
        SQLStatement st = SQLBuilder.buildMappingDeleteSql(mapTableName, key1, table1);
        st.execDelete(db);

        // 存储新的[映射关系]
        if (insertNew && obj2 != null) {
            Object key2 = FieldUtil.get(table2.key.field, obj2);
            st = SQLBuilder.buildMappingToOneSql(mapTableName, key1, key2, table1, table2);
            if (st != null) {
                st.execInsert(db);
            }
        }
    }

    /**
     * 处理N对N关系的关联实体
     */
    private void handleMapToMany(EntityTable table1, EntityTable table2, Object key1, Collection coll,
                                 SQLiteDatabase db, boolean insertNew, HashMap<String, Integer> handleMap)
            throws Exception {
        //ArrayList<String> keyList = new ArrayList<String>();
        //StringBuilder sqlForMap = new StringBuilder();
        if (coll != null) {
            //boolean isF = true;
            //String key1Str = String.valueOf(key1);
            //Class<?> class2 = null;
            // 遍历每个关联的实体
            for (Object obj2 : coll) {
                if (obj2 != null) {
                    // 注意：先递归处理关联对象（如果其主键无值，可以通过先处理赋值）
                    if (insertNew) {
                        // 递归存储[关联实体]
                        checkTableAndSaveRecursive(obj2, db, handleMap);
                    } else {
                        // 递归删除[关联实体]
                        checkTableAndDeleteRecursive(obj2, db, handleMap);
                    }

                    //if (class2 == null) {
                    //    class2 = obj2.getClass();
                    //}
                    // 提前构造[存储新映射关系]的SQL语句和参数
                    //if (insertNew) {
                    //    Object key2 = FieldUtil.get(table2.key.field, obj2);
                    //    if (key2 != null) {
                    //        if (isF) {
                    //            sqlForMap.append(SQLBuilder.TWO_HOLDER);
                    //            isF = false;
                    //        } else {
                    //            sqlForMap.append(SQLBuilder.COMMA).append(SQLBuilder.TWO_HOLDER);
                    //        }
                    //        keyList.add(key1Str);
                    //        keyList.add(String.valueOf(key2));
                    //    }
                    //}
                }
            }
        }
        // 现在处理(当前实体)和(关联对象)的[映射关系]
        String tableName = TableManager.getMapTableName(table1, table2);

        // 删掉旧的[映射关系]
        mTableManager.checkOrCreateMappingTable(db, tableName, table1.name, table2.name);
        SQLStatement delSql = SQLBuilder.buildMappingDeleteSql(tableName, key1, table1);
        delSql.execDelete(db);


        // 存储新的[映射关系]
        if (insertNew && !Checker.isEmpty(coll)) {
            ArrayList<SQLStatement> sqlList = SQLBuilder.buildMappingToManySql(key1, table1, table2, coll);
            if (!Checker.isEmpty(sqlList)) {
                for(SQLStatement sql: sqlList){
                    sql.execInsert(db);
                }
            }
        }

        // 存储新的[映射关系]
        //if (insertNew && !Checker.isEmpty(keyList)) {
        //    Object[] args = keyList.toArray(new String[keyList.size()]);
        //    SQLStatement addSql = new SQLStatement();
        //    addSql.sql = SQLBuilder.REPLACE + SQLBuilder.INTO + tableName
        //            + SQLBuilder.PARENTHESES_LEFT + table1.name + SQLBuilder.COMMA
        //            + table2.name + SQLBuilder.PARENTHESES_RIGHT + SQLBuilder.VALUES + sqlForMap;
        //    addSql.bindArgs = args;
        //    addSql.execInsert(db);
        //}
    }

}