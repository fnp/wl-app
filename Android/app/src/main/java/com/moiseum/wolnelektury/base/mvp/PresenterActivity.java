package com.moiseum.wolnelektury.base.mvp;

import android.os.Bundle;

import com.moiseum.wolnelektury.base.AbstractActivity;

/**
 * Created by Piotr Ostrowski on 13.06.2018.
 */
public abstract class PresenterActivity<P extends Presenter> extends AbstractActivity {

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
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }
}
