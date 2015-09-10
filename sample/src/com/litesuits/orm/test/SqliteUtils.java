package com.litesuits.orm.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.db.assit.SQLiteHelper;
import com.litesuits.orm.log.OrmLog;
import com.litesuits.orm.model.single.Boss;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MaTianyu @http://litesuits.com
 * @date 2015-09-10
 */
public class SqliteUtils {

    private static final String TAG = SqliteUtils.class.getSimpleName();
    public static SQLiteHelper helper;

    public static boolean testLargeScaleUseDefault(Context context, int max) {

        // 1. 初始化数据，并手工建表。
        final List<Boss> list = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            Boss boss = new Boss();
            boss.setAddress("ZheJiang Xihu " + i);
            boss.setPhone("1860000" + i);
            boss.setName("boss" + i);
            list.add(boss);
        }
        if (helper == null) {
            helper = new SQLiteHelper(context, "mydata", null, 1, null);
        }
        final SQLiteDatabase wdb = helper.getWritableDatabase();
        final SQLiteDatabase rdb = helper.getReadableDatabase();
        wdb.execSQL(
                "CREATE TABLE IF NOT EXISTS boss (id INTEGER PRIMARY KEY AUTOINCREMENT ,name TEXT, phone TEXT, address TEXT)");

        // 2. 全部插入
        long start = System.currentTimeMillis();
        wdb.beginTransaction();
        try {
            for (int i = 0; i < max; i++) {
                Boss boss = list.get(i);
                ContentValues values = new ContentValues();
                values.put("name", boss.getName());
                values.put("address", boss.getAddress());
                values.put("phone", boss.getPhone());
                long id = wdb.insert("boss", "", values);
                // 注意，非常重要：insert要回执ID给对象
                boss.setId(id);
                //wdb.execSQL("insert into boss (name, address, phone) values (?,?,?)", new String[]{boss.getName(), boss.getAddress(), boss.getPhone()});
            }
            wdb.setTransactionSuccessful();
        } finally {
            wdb.endTransaction();
        }
        long end = System.currentTimeMillis();
        OrmLog.i(TAG, "insert boss model num: " + list.size() + " , use time: " + (end - start) + " MS");

        // 3. 查询数量测试
        start = System.currentTimeMillis();
        Cursor cursor = rdb.rawQuery("SELECT COUNT(*) FROM boss", null);
        long count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        end = System.currentTimeMillis();
        OrmLog.i(TAG, "query all boss model num: " + count + " , use time: " + (end - start) + " MS");


        // 4. 查询最后10条测试
        start = System.currentTimeMillis();
        List<Boss> subList = new ArrayList<>();
        cursor = rdb.rawQuery("select * from boss order by id desc limit 0,10", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Boss boss = new Boss();
            boss.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            boss.setName(cursor.getString(cursor.getColumnIndex("name")));
            boss.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            boss.setId(cursor.getLong(cursor.getColumnIndex("id")));
            subList.add(boss);
            cursor.moveToNext();
        }
        cursor.close();
        end = System.currentTimeMillis();
        OrmLog.i(TAG, "select top 10 boss model num: " + subList.size() + " , use time: " + (end - start) + " MS");
        OrmLog.i(TAG, subList.toString());

        // 5. 删除全部测试
        start = System.currentTimeMillis();
        long num = wdb.delete("boss", null, null);
        end = System.currentTimeMillis();
        OrmLog.i(TAG, "delete boss model num: " + num + " , use time: " + (end - start) + " MS");

        // 6. 再次查询数量测试
        start = System.currentTimeMillis();
        cursor = rdb.rawQuery("SELECT COUNT(*) FROM boss", null);
        count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        end = System.currentTimeMillis();
        OrmLog.i(TAG, "query all boss model num: " + count + " , use time: " + (end - start) + " MS");
        return true;
    }
}
