package com.litesuits.orm.db.assit;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * SQLite辅助类
 * 
 * @author mty
 * @date 2013-6-2下午4:42:47
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	public static interface OnUpdateListener {
		public void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion);
	}

	private OnUpdateListener onUpdateListener;

	public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
						OnUpdateListener onUpdateListener) {
		super(context, name, factory, version);
		this.onUpdateListener = onUpdateListener;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (onUpdateListener != null) {
			onUpdateListener.onUpdate(db, oldVersion, newVersion);
		}
	}

	public synchronized SQLiteDatabase getWritableDatabase() {
		return super.getWritableDatabase("pwd");
	}

	public synchronized SQLiteDatabase getReadableDatabase() {
		return super.getReadableDatabase("pwd");
	}
}