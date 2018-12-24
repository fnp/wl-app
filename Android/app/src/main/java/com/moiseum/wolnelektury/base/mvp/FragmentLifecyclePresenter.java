package com.moiseum.wolnelektury.base.mvp;

import android.os.Bundle;

/**
 * Created by Piotr Ostrowski on 13.06.2018.
 */
public abstract class FragmentLifecyclePresenter extends LifecyclePresenter {

    public void onViewCreated(Bundle savedInstanceState) {
    }

    public void onDestroyView() {
    }
}
