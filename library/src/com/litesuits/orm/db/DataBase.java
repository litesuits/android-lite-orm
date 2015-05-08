package com.litesuits.orm.db;

import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.impl.SQLStatement;
import com.litesuits.orm.db.impl.SQLiteHelper;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.Relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * data base operation interface
 *
 * @author mty
 * @date 2013-6-2上午2:37:56
 */
public interface DataBase {

    /**
     * get a single data operator based on SQLite
     *
     * @return {@link com.litesuits.orm.db.impl.CascadeSQLiteImpl}
     */
    public LiteOrm single();

    /**
     * get a cascade data operator based on SQLite
     *
     * @return {@link com.litesuits.orm.db.impl.CascadeSQLiteImpl}
     */
    public LiteOrm cascade();

    /**
     * save: insert or update a single entity
     *
     * @return the number of rows affected by this SQL statement execution.
     */
    public long save(Object entity);

    /**
     * save: insert or update a collection
     *
     * @return the number of affected rows
     */
    public int save(Collection<?> collection);

    /**
     * insert a single entity
     *
     * @return the number of rows affected by this SQL statement execution.
     */
    public long insert(Object entity);


    /**
     * insert a single entity with conflict algorithm
     *
     * @return the number of rows affected by this SQL statement execution.
     */
    public long insert(Object entity, ConflictAlgorithm conflictAlgorithm);

    /**
     * insert a collection
     *
     * @return the number of affected rows
     */
    public int insert(Collection<?> collection);

    /**
     * insert a collection with conflict algorithm
     *
     * @return the number of affected rows
     */
    public int insert(Collection<?> collection, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a single entity
     *
     * @return the number of affected rows
     */
    public int update(Object entity);

    /**
     * update a single entity with conflict algorithm
     *
     * @return the number of affected rows
     */
    public int update(Object entity, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a single entity with conflict algorithm, and only update columns in {@link ColumnsValue}
     * if param {@link ColumnsValue} is null, update all columns.
     *
     * @return the number of affected rows
     */
    public int update(Object entity, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a collection
     *
     * @return the number of affected rows
     */
    public int update(Collection<?> collection);

    /**
     * update a collection with conflict algorithm
     *
     * @return the number of affected rows
     */
    public int update(Collection<?> collection, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a collection with conflict algorithm, and only update columns in {@link ColumnsValue}
     * if param {@link ColumnsValue} is null, update all columns.
     *
     * @return the number of affected rows
     */
    public int update(Collection<?> collection, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm);

    /**
     * delete a single entity
     *
     * @return the number of affected rows
     */
    public int delete(Object entity);

    /**
     * delete all rows
     *
     * @return the number of affected rows
     */
    public int delete(Class<?> claxx);

    /**
     * delete all rows
     *
     * @return the number of affected rows
     */
    public int deleteAll(Class<?> claxx);

    /**
     * <b>start must >=0 and smaller than end</b>
     * <p>delete from start to the end, <b>[start,end].</b>
     * <p>set end={@link Integer#MAX_VALUE} will delete all rows from the start
     *
     * @return the number of affected rows
     */
    public int delete(Class<?> claxx, long start, long end, String orderAscColu);

    /**
     * delete a collection
     *
     * @return the number of affected rows
     */
    public int delete(Collection<?> collection);

    /**
     * delete by custem where syntax
     *
     * @return the number of affected rows
     */
    public int delete(Class<?> claxx, WhereBuilder where);

    /**
     * query all data of this type
     *
     * @return the query result list
     */
    public <T> ArrayList<T> query(Class<T> claxx);

    /**
     * custom query
     *
     * @return the query result list
     */
    public <T> ArrayList<T> query(QueryBuilder qb);

    /**
     * query entity by long id
     *
     * @return the query result
     */
    public <T> T queryById(long id, Class<T> clazz);

    /**
     * query entity by string id
     *
     * @return the query result
     */
    public <T> T queryById(String id, Class<T> clazz);

    /**
     * query count of table rows and return
     *
     * @return the count of query result
     */
    public long queryCount(Class<?> claxx);

    /**
     * query count of your sql query result rows and return
     *
     * @return the count of query result
     */
    public long queryCount(QueryBuilder qb);

    /**
     * build a sql statement with sql and args.
     */
    public SQLStatement createSQLStatement(String sql, Object[] bindArgs);

    /**
     * Execute this SQL statement, if it is not a SELECT / INSERT / DELETE / UPDATE, for example
     * CREATE / DROP table, view, trigger, index etc.
     */
    public boolean execute(SQLiteDatabase db, SQLStatement statement);

    /**
     * drop a table
     *
     * @return true if droped successfully.
     */
    public boolean dropTable(Object entity);

    /**
     * drop a table
     *
     * @return true if droped successfully.
     */
    public boolean dropTable(String tableName);

    /**
     * find and return relation between two diffirent collection.
     *
     * @return the relation list of class1 and class2;
     */
    public ArrayList<Relation> queryRelation(Class class1, Class class2, List<String> key1List,
                                                      List<String> key2List);

    /**
     * auto entity relation mapping
     */
    public <E, T> boolean mapping(Collection<E> col1, Collection<T> col2);

    /**
     * get readable database
     */
    public SQLiteDatabase getReadableDatabase();

    /**
     * get writable database
     */
    public SQLiteDatabase getWritableDatabase();

    /**
     * get {@link TableManager}
     */
    public TableManager getTableManager();

    /**
     * get {@link SQLiteHelper}
     */
    public SQLiteHelper getSQLiteHelper();

    /**
     * get {@link DataBaseConfig}
     */
    public DataBaseConfig getDataBaseConfig();


    /**
     * {@link #openOrCreateDatabase(String, android.database.sqlite.SQLiteDatabase.CursorFactory)}
     *
     * @return true if create successfully.
     */
    public SQLiteDatabase createDatabase();

    /**
     * if database in sdcard , you will need this  in manifest:
     * <p/>
     * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     * Equivalent to {@link SQLiteDatabase#openDatabase(String, android.database.sqlite.SQLiteDatabase.CursorFactory, int)}.
     */
    public SQLiteDatabase openOrCreateDatabase(String path, SQLiteDatabase.CursorFactory factory);

    /**
     * if database in sdcard , you will need this  in manifest:
     * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
     *
     * @return true if delete successfully.
     */
    //    public boolean deleteDatabase(File file);


    /**
     * 关闭数据库，清空缓存。
     */
    public void close();
}
