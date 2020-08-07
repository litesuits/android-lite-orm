package com.litesuits.orm;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

public class LiteOrmApplication {
    private static String mPwd;
    public static void init(Context context, String pwd){
        SQLiteDatabase.loadLibs(context);
        mPwd = pwd;
    }

    public static String getPwd() {
        return mPwd;
    }
}
