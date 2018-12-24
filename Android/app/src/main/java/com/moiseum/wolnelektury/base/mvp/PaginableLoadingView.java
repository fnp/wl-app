package com.moiseum.wolnelektury.base.mvp;

/**
 * Created by Piotr Ostrowski on 28.11.2017.
 */

public interface PaginableLoadingView<T> {

	void setData(T data, boolean reload);

	void setInitialProgressVisible(boolean visible);

	void setLoadMoreProgressVisible(boolean visible);

	void showError(Exception e, boolean loadMore);
}
