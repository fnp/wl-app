package com.moiseum.wolnelektury.base.mvp;

public interface LoadingView<T> {

	void setData(T data);

	void setProgressVisible(boolean visible);

	void showError(Exception e);
}
