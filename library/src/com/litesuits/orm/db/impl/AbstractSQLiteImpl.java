package com.litesuits.orm.db.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.Querier;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapProperty;
import com.litesuits.orm.db.model.Relation;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.FieldUtil;

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
public abstract class AbstractSQLiteImpl extends SQLiteClosable implements DataBase {

    public static final String TAG = AbstractSQLiteImpl.class.getSimpleName();

    protected SQLiteHelper mHelper;

    protected DataBaseConfig mConfig;

    protected TableManager mTableManager;

    protected DataBase otherDatabase;

    protected AbstractSQLiteImpl(AbstractSQLiteImpl dataBase) {
        this.mHelper = dataBase.mHelper;
        this.mConfig = dataBase.mConfig;
        this.mTableManager = dataBase.mTableManager;
        this.otherDatabase = dataBase;
    }


    protected AbstractSQLiteImpl(DataBaseConfig config) {
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
