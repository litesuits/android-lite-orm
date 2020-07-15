package com.litesuits.orm;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

public class LiteOrmApplication {
    public static void init(Context context){
        SQLiteDatabase.loadLibs(context);
    }
}
