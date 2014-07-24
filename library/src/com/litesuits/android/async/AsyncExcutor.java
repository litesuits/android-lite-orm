package com.litesuits.android.async;

import android.os.Handler;
import android.os.Looper;
import com.litesuits.android.log.Log;

import java.util.concurrent.*;

/**
 * 异步执行
 * @author MaTianyu
 */
public class AsyncExcutor {
	private static final String TAG = AsyncExcutor.class.getSimpleName();
	private static ExecutorService threadPool;
	public static Handler handler = new Handler(Looper.getMainLooper());

	public AsyncExcutor() {
		if (threadPool == null) {
			threadPool = Executors.newCachedThreadPool();
		}
	}

	public static synchronized void shutdownNow() {
		if (threadPool != null && !threadPool.isShutdown()) threadPool.shutdownNow();
		threadPool = null;
		handler = null;
	}

	/**
	 * 将任务投入线程池执行
	 * @param worker
	 * @return
	 */
	public <T> FutureTask<T> execute(final Worker<T> worker) {
		Callable<T> call = new Callable<T>() {
			@Override
			public T call() throws Exception {
				return postResult(worker, worker.doInBackground());
			}
		};
		FutureTask<T> task = new FutureTask<T>(call) {
			@Override
			protected void done() {
				try {
					get();
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				} catch (ExecutionException e) {
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
					throw new RuntimeException("An error occured while executing doInBackground()", e.getCause());
				} catch (CancellationException e) {
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}
		};
		threadPool.execute(task);
		return task;
	}

	/**
	 * 将子线程结果传递到UI线程
	 * @param worker
	 * @param result
	 * @return
	 */
	private <T> T postResult(final Worker<T> worker, final T result) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				worker.onPostExecute(result);
			}
		});
		return result;
	}

	public <T> FutureTask<T> execute(Callable<T> call) {
		FutureTask<T> task = new FutureTask<T>(call);
		threadPool.execute(task);
		return task;
	}

	public static interface Worker<T> {
		T doInBackground();

		void onPostExecute(T data);
	}
}
