package com.litesuits.orm.db.impl;
import java.io.Closeable;

/**
 * An object created from a SQLiteDatabase that can be closed.
 * 
 * This class implements a primitive reference counting scheme for database
 * objects.
 */
public abstract class SQLiteClosable implements Closeable {
	private int mReferenceCount = 1;

	/**
	 * Called when the last reference to the object was released by
	 * a call to {@link #releaseReference()} or {@link #close()}.
	 */
	protected abstract void onAllReferencesReleased();

	/**
	 * Acquires a reference to the object.
	 * 
	 * @throws IllegalStateException if the last reference to the object has
	 *             already
	 *             been released.
	 */
	public void acquireReference() {
		synchronized (this) {
			if (mReferenceCount <= 0) { throw new IllegalStateException(
					"attempt to re-open an already-closed object: " + this); }
			mReferenceCount++;
		}
	}

	/**
	 * Releases a reference to the object, closing the object if the last
	 * reference
	 * was released.
	 * 
	 * @see #onAllReferencesReleased()
	 */
	public void releaseReference() {
		boolean refCountIsZero = false;
		synchronized (this) {
			refCountIsZero = --mReferenceCount == 0;
		}
		if (refCountIsZero) {
			onAllReferencesReleased();
		}
	}

	/**
	 * Releases a reference to the object, closing the object if the last
	 * reference
	 * was released.
	 * 
	 * Calling this method is equivalent to calling {@link #releaseReference}.
	 * 
	 * @see #releaseReference()
	 * @see #onAllReferencesReleased()
	 */
	public void close() {
		releaseReference();
	}
}
