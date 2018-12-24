package com.moiseum.wolnelektury.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractActivity;
import com.moiseum.wolnelektury.base.AbstractIntent;

/**
 * @author golonkos
 */

public class WebViewActivity extends AbstractActivity {

	private static final String PARAM_URL = "PARAM_URL";

	public static class WebViewIntent extends AbstractIntent {

		public WebViewIntent(Context packageContext, String url) {
			super(packageContext, WebViewActivity.class);
			putExtra(PARAM_URL, url);
		}
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_blank;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {
		setBackButtonEnable(true);

		if (savedInstanceState == null) {
			String url = getIntent().getStringExtra(PARAM_URL);
			Fragment fragment = WebViewFragment.newInstance(url);
			getSupportFragmentManager().beginTransaction().add(R.id.flContainer, fragment).commit();
		}
	}
}
