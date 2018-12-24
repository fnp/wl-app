package com.moiseum.wolnelektury.base.mvp;

/**
 * Created by Piotr Ostrowski on 13.06.2018.
 */
public class Presenter<V> extends LifecyclePresenter {

    private V view;

    public Presenter(V view) {
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
