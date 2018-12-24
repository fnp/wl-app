package com.moiseum.wolnelektury.view.supportus;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractActivity;
import com.moiseum.wolnelektury.base.AbstractIntent;

/**
 * @author golonkos
 */

public class SupportUsActivity extends AbstractActivity {

	public static class SupportUsIntent extends AbstractIntent {

		public SupportUsIntent(Context packageContext) {
			super(packageContext, SupportUsActivity.class);
		}
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_blank;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {
		setBackButtonEnable(true);
		setTitle(R.string.support_us);

		if (savedInstanceState == null) {
			Fragment fragment = SupportUsFragment.newInstance();
			getSupportFragmentManager().beginTransaction().add(R.id.flContainer, fragment).commit();
		}
	}
}
