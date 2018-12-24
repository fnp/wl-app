package com.moiseum.wolnelektury.view.player.header;

import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;

/**
 * Created by Piotr Ostrowski on 22.05.2018.
 */
public class PlayerHeaderPresenter extends FragmentPresenter<PlayerHeaderView> {

    public PlayerHeaderPresenter(String author, String title, String coverUrl, PlayerHeaderView view) {
        super(view);
    }
}
