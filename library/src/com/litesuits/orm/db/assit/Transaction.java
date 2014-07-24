package com.litesuits.orm.db.assit;

import android.database.sqlite.SQLiteDatabase;
import com.litesuits.android.log.Log;

/**
 * 辅助事务
 * @author mty
 * @date 2013-6-15下午11:09:15
 */
public class Transaction {
	private static final String TAG = Transaction.class.getSimpleName();

	/**
	 * 因为每个具体事物都不一样，但又有相同的结构，既要维持代码的统一性，也要可以个性化解析。
	 * 
	 * @param db
	 * @param worker
	 * @return
	 */
	public static <T> T execute(SQLiteDatabase db, Worker<T> worker) {
		db.beginTransaction();
		Log.i(TAG, "----> BeginTransaction");
		T data = null;
		try {
			data = worker.doTransaction(db);
			db.setTransactionSuccessful();
			if (Log.isPrint) Log.i(TAG, "----> Transaction Successful");
		} catch (Exception e) {
			if (Log.isPrint) Log.e(TAG, "----> Transaction Failling");
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		return data;
	}

	public static interface Worker<T> {
		public T doTransaction(SQLiteDatabase db) throws Exception;
	}

}
