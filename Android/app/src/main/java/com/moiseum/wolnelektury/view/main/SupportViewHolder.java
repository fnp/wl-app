package com.moiseum.wolnelektury.view.main;

import android.view.View;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.components.recycler.ViewHolder;

import butterknife.OnClick;

/**
 * @author golonkos
 */

public class SupportViewHolder extends ViewHolder<NavigationElement> {

	private final NavigationAdapter.SupportUsListener supportUsListener;

	public SupportViewHolder(View view, NavigationAdapter.SupportUsListener supportUsListener) {
		super(view);
		this.supportUsListener = supportUsListener;
	}

	@Override
	public void bind(NavigationElement item, boolean selected) {
		//nop
	}

	@SuppressWarnings("unused")
	@OnClick(R.id.btnSupportUs)
	public void onSupportUsClicked() {
		if (supportUsListener != null) {
			supportUsListener.onSupportUsClicked();
		}
	}
}
