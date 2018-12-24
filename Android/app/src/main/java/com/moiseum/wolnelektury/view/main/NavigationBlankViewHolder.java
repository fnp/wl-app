package com.moiseum.wolnelektury.view.main;

import android.view.View;

import com.moiseum.wolnelektury.components.recycler.ViewHolder;

/**
 * Created by Piotr Ostrowski on 02.07.2018.
 */
public class NavigationBlankViewHolder extends ViewHolder<NavigationElement> {

	NavigationBlankViewHolder(View view) {
		super(view);
	}

	@Override
	public void bind(NavigationElement item, boolean selected) {
		// nop.
	}
}
