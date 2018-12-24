package com.moiseum.wolnelektury.connection;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Response;

public abstract class RestClientCallback<T, S> implements retrofit2.Callback<T> {

	private static final String TAG = RestClientCallback.class.getSimpleName();

	@Override
	public void onResponse(Call<T> call, Response<T> response) {
		if (response.isSuccessful()) {
			onSuccess(response.body());
		} else {
			try {
				ErrorHandler<T> errorHandler = new ErrorHandler<>(response);
				errorHandler.handle();
			} catch (Exception e) {
				onFailure(e);
			}
		}
	}

	@Override
	public void onFailure(Call<T> call, Throwable t) {
		Log.e(TAG, t.getMessage(), t);
		if (!call.isCanceled()) {
			onFailure(new Exception(t));
		} else {
			onCancel();
		}
	}


	public abstract void onSuccess(T data);

	public abstract void onFailure(Exception e);

	public abstract void onCancel();

	public abstract Call<T> execute(S service);
}
