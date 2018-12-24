package com.moiseum.wolnelektury.base.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.moiseum.wolnelektury.base.AbstractFragment;


/**
 * Fragment that creates {@link LifecyclePresenter} and in its lifecycle methods calls corresponding methods of
 * presenter.
 *
 * @param <P> type of presenter for this fragment.
 */
public abstract class PresenterFragment<P extends FragmentPresenter> extends AbstractFragment {

	private P presenter;

	protected abstract P createPresenter();

	protected P getPresenter() {
		return presenter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		presenter = createPresenter();
		presenter.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		presenter.onViewCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		presenter.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		presenter.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		presenter.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		presenter.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		presenter.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		presenter.onDestroy();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		presenter.onSaveInstanceState(outState);
	}
}
