package com.moiseum.wolnelektury.base.mvp;

public class FragmentPresenter<V> extends FragmentLifecyclePresenter {

	private V view;

	public FragmentPresenter(V view) {
		this.view = view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.view = null;
	}

	protected V getView() {
		return view;
	}

}
