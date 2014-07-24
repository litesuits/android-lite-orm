package com.litesuits.orm;

import android.content.Context;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.impl.DataBaseSQLiteImpl;

/**
 * 数据管理
 *
 * @author mty
 * @date 2013-6-2下午4:35:44
 */
public final class LiteOrm {

    private static DataBase mDataBase;

    /**
     * 简单获取数据操作对象，默认配置，打开日志。
     *
     * @param context
     * @return
     */
    public synchronized static DataBase via(Context context) {
        if (mDataBase == null) {
            mDataBase = newInstance(context, DataBaseConfig.DEFAULT_DB_NAME);
        }
        return mDataBase;
    }

    public static void close() {
        close(mDataBase);
    }



    public synchronized static DataBase newInstance(Context context, String dbName) {
        return newInstance(new DataBaseConfig(context, dbName));
    }

    public synchronized static DataBase newInstance(DataBaseConfig config) {
        return DataBaseSQLiteImpl.newInstance(config);
    }

    public synchronized static void close(DataBase db) {
        if (db != null) {
            db.close();
        }
    }
}
