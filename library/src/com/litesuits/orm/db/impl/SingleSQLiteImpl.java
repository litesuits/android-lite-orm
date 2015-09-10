package com.litesuits.orm.db.impl;

import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.*;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.EntityTable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 数据SQLite操作实现
 * 可查阅 <a href="http://www.sqlite.org/lang.html">SQLite操作指南</a>
 *
 * @author mty
 * @date 2013-6-2下午2:32:56
 */
public final class SingleSQLiteImpl extends LiteOrm {

    public static final String TAG = SingleSQLiteImpl.class.getSimpleName();

    protected SingleSQLiteImpl(LiteOrm dataBase) {
        super(dataBase);
    }

    private SingleSQLiteImpl(DataBaseConfig config) {
        super(config);
    }


    public synchronized static LiteOrm newInstance(DataBaseConfig config) {
        return new SingleSQLiteImpl(config);
    }

    @Override
    public LiteOrm single() {
        return this;
    }

    @Override
    public LiteOrm cascade() {
        if (otherDatabase == null) {
            otherDatabase = new CascadeSQLiteImpl(this);
        }
        return otherDatabase;
    }

    @Override
    public long save(Object entity) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            mTableManager.checkOrCreateTable(db, entity);
            return SQLBuilder.buildReplaceSql(entity).execInsert(db, entity);
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
                return stmt.execInsertCollection(db, collection);
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
            return SQLBuilder.buildInsertSql(entity, conflictAlgorithm).execInsert(db, entity);
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
                return stmt.execInsertCollection(db, collection);
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
            mTableManager.checkOrCreateTable(db, entity);
            return SQLBuilder.buildUpdateSql(entity, cvs, conflictAlgorithm).execUpdate(db);
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
                mTableManager.checkOrCreateTable(db, entity);
                SQLStatement stmt = SQLBuilder.buildUpdateAllSql(entity, cvs, conflictAlgorithm);
                return stmt.execUpdateCollection(db, collection, cvs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public int delete(Object entity) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            mTableManager.checkOrCreateTable(db, entity);
            return SQLBuilder.buildDeleteSql(entity).execDelete(db);
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
                SQLStatement stmt = SQLBuilder.buildDeleteSql(collection);
                SQLiteDatabase db = mHelper.getWritableDatabase();
                mTableManager.checkOrCreateTable(db, table.claxx);
                return stmt.execDeleteCollection(db, collection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    @Deprecated
    public int delete(Class<?> claxx, WhereBuilder where) {
        return delete(where);
    }

    @Override
    public int delete(WhereBuilder where) {
        acquireReference();
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            mTableManager.checkOrCreateTable(db, where.getTableClass());
            return where.createStatementDelete().execDelete(db);
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
            mTableManager.checkOrCreateTable(db, claxx);
            int num = stmt.execDelete(db);
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
            SQLiteDatabase db = mHelper.getWritableDatabase();
            mTableManager.checkOrCreateTable(db, claxx);
            return stmt.execDelete(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseReference();
        }
        return SQLStatement.NONE;
    }

    @Override
    public <T> ArrayList<T> query(Class<T> claxx) {
        acquireReference();
        try {
            SQLStatement stmt = new QueryBuilder(claxx).createStatement();
            SQLiteDatabase db = mHelper.getReadableDatabase();
            mTableManager.checkOrCreateTable(db, claxx);
            return stmt.query(db, claxx);
        } finally {
            releaseReference();
        }
    }

    @Override
    public <T> ArrayList<T> query(QueryBuilder qb) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        mTableManager.checkOrCreateTable(db, qb.getQueryClass());
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
            SQLStatement stmt = new QueryBuilder(claxx)
                    .where(table.key.column + "=?", new String[]{id})
                    .createStatement();
            mTableManager.checkOrCreateTable(db, claxx);
            ArrayList<T> list = stmt.query(db, claxx);
            if (!Checker.isEmpty(list)) {
                return list.get(0);
            }
        } finally {
            releaseReference();
        }
        return null;
    }

}
