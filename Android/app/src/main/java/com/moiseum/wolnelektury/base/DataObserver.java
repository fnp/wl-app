package com.moiseum.wolnelektury.base;

/**
 * @author golonkos.
 */
public interface DataObserver<T> {
	void onLoadStarted();

	void onLoadSuccess(T data);

	void onLoadFailed(Exception e);
}
