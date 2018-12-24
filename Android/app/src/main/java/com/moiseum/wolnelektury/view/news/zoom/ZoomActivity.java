package com.moiseum.wolnelektury.view.news.zoom;

import android.content.Context;
import android.os.Bundle;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractActivity;
import com.moiseum.wolnelektury.base.AbstractIntent;

import java.util.ArrayList;

/**
 * Created by Piotr Ostrowski on 07.08.2018.
 */
public class ZoomActivity extends AbstractActivity {

	private static final String PHOTOS_URL_KEY = "PhotoUrls";
	private static final String POSITION_KEY = "PositionKey";
	private static final String ZOOM_FRAGMENT_TAG = "ZoomFragmentTag";

	public static class ZoomIntent extends AbstractIntent {
		public ZoomIntent(ArrayList<String> photoUrls, int position, Context context) {
			super(context, ZoomActivity.class);
			putExtra(PHOTOS_URL_KEY, photoUrls);
			putExtra(POSITION_KEY, position);
		}
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_blank;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {
		if (!getIntent().hasExtra(PHOTOS_URL_KEY) || !getIntent().hasExtra(POSITION_KEY)) {
			throw new IllegalStateException("Activity intent is missing news extras.");
		}

		ArrayList<String> photoUrls = getIntent().getStringArrayListExtra(PHOTOS_URL_KEY);
		int position = getIntent().getIntExtra(POSITION_KEY, 0);
		ZoomFragment zoomFragment = (ZoomFragment) getSupportFragmentManager().findFragmentByTag(ZOOM_FRAGMENT_TAG);
		if (zoomFragment == null) {
			zoomFragment = ZoomFragment.newInstance(photoUrls, position);
			getSupportFragmentManager().beginTransaction().add(R.id.flContainer, zoomFragment, ZOOM_FRAGMENT_TAG).commit();
		}
	}
}
