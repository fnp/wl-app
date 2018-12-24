package com.moiseum.wolnelektury.base;

import android.util.Log;

import com.moiseum.wolnelektury.connection.RestClientCallback;

import javax.annotation.Nullable;

import retrofit2.Call;

/**
 * @author golonkos.
 */

public abstract class DataProvider<T, S> extends RestClientCallback<T, S> {

	private final static String TAG = DataProvider.class.getSimpleName();

	protected DataObserver<T> dataObserver;
	protected String lastKeySlug = null;
	private Call<T> call;

	public DataProvider() {
	}

	public void setDataObserver(DataObserver<T> dataObserver) {
		this.dataObserver = dataObserver;
	}

	@Override
	public void onSuccess(T data) {
		if (dataObserver != null) {
			dataObserver.onLoadSuccess(data);
		}
	}

	@Override
	public void onFailure(Exception e) {
		Log.e(TAG, "Failed to load data", e);
		if (dataObserver != null) {
			dataObserver.onLoadFailed(e);
		}
	}

	@Override
	public void onCancel() {
		//nop
	}

	/**
	 * Invoked in order to load data.
	 * @param lastKey Last book slug for pagination. Can be null if there is no pagination.
	 */
	public void load(@Nullable String lastKey) {
		cancel();
		lastKeySlug = lastKey;
		call = WLApplication.getInstance().getRestClient().call(this, getServiceClass());
		if (dataObserver != null) {
			dataObserver.onLoadStarted();
		}
	}

	public void cancel() {
		if (call != null) {
			call.cancel();
			call = null;
		}
	}

	public void release() {
		dataObserver = null;
	}

	protected abstract Class<S> getServiceClass();
}
