package com.litesuits.orm.db;

import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.Relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * data base operation
 *
 * @author mty
 * @date 2013-6-2上午2:37:56
 */
public interface DataBase {

    /**
     * insert or update a single entity
     */
    public long save(Object entity);

    /**
     * insert or update a collection
     */
    public int save(Collection<?> collection);

    /**
     * delete a single entity
     */
    public int delete(Object entity);

    /**
     * delete all rows
     */
    public int deleteAll(Class<?> claxx);

    /**
     * <b>start must >=0 and smaller than end</b>
     * <p>delete from start to the end, <b>(start,end].</b>
     * <p>set end={@link Integer#MAX_VALUE} will delete all rows from the start
     */
    public int delete(Class<?> claxx, int start, int end);

    /**
     * delete a collection
     */
    public int delete(Collection<?> collection);

    /**
     * query count of table rows and return
     */
    public long queryCount(Class<?> claxx);

    /**
     * query entity and put it into list
     */
    public <T> ArrayList<T> query(Class<T> claxx, Collection<?> collection, QueryBuilder qb);

    /**
     * query all data of this type
     */
    public <T> ArrayList<T> query(Class<T> claxx);
    //
    //	public <T> List<T> query(Class<T> claxx, QueryBuilder qb);
    //
    //	public <T> List<T> query(Class<T> claxx, int start, int end);
    //
    //	public <T> List<T> query(Class<T> claxx, Collection<?> collection);
    //
    //	public <T> List<T> query(Class<T> claxx, Object[] objects);
    //
    //	public <T> List<T> query(Class<T> claxx, Map<?, ?> map);
    //
    //	public boolean mapping(Collection<?> c1, Collection<?> c2);

    /**
     * find and return relation between two diffirent collection.
     */
    public ArrayList<Relation> queryRelation(Class class1, Class class2, List<String> key1List, List<String> key2List);

    /**
     * auto entity relation mapping
     */
    public <E, T> boolean mapping(Collection<E> col1, Collection<T> col2);

    /**
     * 获取可读数据库操作对象
     */
    public SQLiteDatabase getReadableDatabase();

    /**
     * 获取可写数据库操作对象
     */
    public SQLiteDatabase getWritableDatabase();

    /**
     * 关闭数据库，清空缓存。
     */
    public void close();
}
