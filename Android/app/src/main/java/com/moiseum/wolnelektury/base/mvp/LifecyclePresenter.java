package com.moiseum.wolnelektury.base.mvp;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class LifecyclePresenter {

	private CompositeDisposable disposables = new CompositeDisposable();

	public void onCreate(Bundle savedInstanceState) {
	}

	public void onStart() {
	}

	public void onStop() {
	}

	public void onResume() {
	}

	public void onPause() {
	}

	@CallSuper
	public void onDestroy() {
		disposables.dispose();
	}

	public void onSaveInstanceState(Bundle outState) {
	}

	protected void addDisposable(Disposable disposable) {
		disposables.add(disposable);
	}
}