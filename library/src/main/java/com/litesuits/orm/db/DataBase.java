/*
 * Copyright (C) 2013 litesuits.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.litesuits.orm.db;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.assit.SQLStatement;
import com.litesuits.orm.db.assit.SQLiteHelper;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.RelationKey;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
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
     * {@link #openOrCreateDatabase(String, android.database.sqlite.SQLiteDatabase.CursorFactory)}
     *
     * @return true if create successfully.
     */
    SQLiteDatabase openOrCreateDatabase();

    /**
     * save: insert or update a single entity
     *
     * @return the number of rows affected by this SQL statement execution.
     */
    long save(Object entity);

    /**
     * save: insert or update a collection
     *
     * @return the number of affected rows
     */
    <T> int save(Collection<T> collection);

    /**
     * insert a single entity
     *
     * @return the number of rows affected by this SQL statement execution.
     */
    long insert(Object entity);


    /**
     * insert a single entity with conflict algorithm
     *
     * @return the number of rows affected by this SQL statement execution.
     */
    long insert(Object entity, ConflictAlgorithm conflictAlgorithm);

    /**
     * insert a collection
     *
     * @return the number of affected rows
     */
    <T> int insert(Collection<T> collection);

    /**
     * insert a collection with conflict algorithm
     *
     * @return the number of affected rows
     */
    <T> int insert(Collection<T> collection, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a single entity
     *
     * @return number of affected rows
     */
    int update(Object entity);

    /**
     * update a single entity with conflict algorithm
     *
     * @return number of affected rows
     */
    int update(Object entity, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a single entity with conflict algorithm, and only update columns in {@link ColumnsValue}
     * if param {@link ColumnsValue} is null, update all columns.
     *
     * @return the number of affected rows
     */
    int update(Object entity, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a collection
     *
     * @return the number of affected rows
     */
    <T> int update(Collection<T> collection);

    /**
     * update a collection with conflict algorithm
     *
     * @return number of affected rows
     */
    <T> int update(Collection<T> collection, ConflictAlgorithm conflictAlgorithm);

    /**
     * update a collection with conflict algorithm, and only update columns in {@link ColumnsValue}
     * if param {@link ColumnsValue} is null, update all columns.
     *
     * @return number of affected rows
     */
    <T> int update(Collection<T> collection, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm);

    /**
     * update model use custom where clause.
     *
     * @return number of affected rows
     */
    int update(WhereBuilder builder, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm);

    /**
     * delete a single entity
     *
     * @return the number of affected rows
     */
    int delete(Object entity);

    /**
     * delete all rows
     *
     * @return the number of affected rows
     */
    <T> int delete(Class<T> claxx);

    /**
     * delete all rows
     *
     * @return the number of affected rows
     */
    <T> int deleteAll(Class<T> claxx);

    /**
     * <b>start must >=0 and smaller than end</b>
     * <p>delete from start to the end, <b>[start,end].</b>
     * <p>set end={@link Integer#MAX_VALUE} will delete all rows from the start
     *
     * @return the number of affected rows
     */
    <T> int delete(Class<T> claxx, long start, long end, String orderAscColu);

    /**
     * delete a collection
     *
     * @return the number of affected rows
     */
    <T> int delete(Collection<T> collection);

    /**
     * delete by custem where syntax
     *
     * @return the number of affected rows
     * @deprecated use {@link #delete(WhereBuilder)} instead.
     */
    <T> int delete(Class<T> claxx, WhereBuilder where);

    /**
     * delete by custem where syntax
     *
     * @return the number of affected rows
     */
    int delete(WhereBuilder where);

    /**
     * query all data of this type
     *
     * @return the query result list
     */
    <T> ArrayList<T> query(Class<T> claxx);

    /**
     * custom query
     *
     * @return the query result list
     */
    <T> ArrayList<T> query(QueryBuilder<T> qb);

    /**
     * query entity by long id
     *
     * @return the query result
     */
    <T> T queryById(long id, Class<T> clazz);

    /**
     * query entity by string id
     *
     * @return the query result
     */
    <T> T queryById(String id, Class<T> clazz);

    /**
     * query count of table rows and return
     *
     * @return the count of query result
     */
    <T> long queryCount(Class<T> claxx);

    /**
     * query count of your sql query result rows and return
     *
     * @return the count of query result
     */
    long queryCount(QueryBuilder qb);

    /**
     * build a sql statement with sql and args.
     */
    SQLStatement createSQLStatement(String sql, Object[] bindArgs);

    /**
     * Execute this SQL statement, if it is not a SELECT / INSERT / DELETE / UPDATE, for example
     * CREATE / DROP table, view, trigger, index etc.
     */
    boolean execute(SQLiteDatabase db, SQLStatement statement);

    /**
     * drop a table
     *
     * @return true if droped successfully.
     * @deprecated
     */
    @Deprecated boolean dropTable(Object entity);

    /**
     * drop a table
     *
     * @return true if droped successfully.
     */
    boolean dropTable(Class<?> claxx);

    /**
     * drop a table
     *
     * @return true if droped successfully.
     */
    boolean dropTable(String tableName);

    /**
     * find and return relation between two diffirent collection.
     *
     * @return the relation list of class1 and class2;
     */
    ArrayList<RelationKey> queryRelation(Class class1, Class class2, List<String> key1List);

    /**
     * auto entity relation mapping
     */
    <E, T> boolean mapping(Collection<E> col1, Collection<T> col2);

    /**
     * get readable database
     */
    SQLiteDatabase getReadableDatabase();

    /**
     * get writable database
     */
    SQLiteDatabase getWritableDatabase();

    /**
     * get {@link TableManager}
     */
    TableManager getTableManager();

    /**
     * get {@link SQLiteHelper}
     */
    SQLiteHelper getSQLiteHelper();

    /**
     * get {@link DataBaseConfig}
     */
    DataBaseConfig getDataBaseConfig();

    /**
     * if database in sdcard , you will need this  in manifest:
     * <p/>
     * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     * Equivalent to {@link SQLiteDatabase#openDatabase(String, android.database.sqlite.SQLiteDatabase.CursorFactory, int)}.
     */
    SQLiteDatabase openOrCreateDatabase(String path, SQLiteDatabase.CursorFactory factory);

    boolean deleteDatabase();

    /**
     * if database in sdcard , you will need this  in manifest:
     * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
     *
     * @return true if delete successfully.
     */
    boolean deleteDatabase(File file);

    /**
     * 关闭数据库，清空缓存。
     */
    void close();
}
