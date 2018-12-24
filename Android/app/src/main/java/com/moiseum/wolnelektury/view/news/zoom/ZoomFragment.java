package com.moiseum.wolnelektury.view.news.zoom;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Piotr Ostrowski on 07.08.2018.
 */
public class ZoomFragment extends PresenterFragment<ZoomPresenter> implements ZoomView {

	private static final String PHOTOS_URL_KEY = "PhotoUrls";
	private static final String POSITION_KEY = "PositionKey";
	private static final int FIVE_PAGES = 5;

	@BindView(R.id.vpGallery)
	ViewPager vpGallery;
	@BindView(R.id.indicator)
	CircleIndicator indicator;

	public static ZoomFragment newInstance(ArrayList<String> photoUrls, int position) {
		ZoomFragment fragment = new ZoomFragment();
		Bundle args = new Bundle(1);
		args.putStringArrayList(PHOTOS_URL_KEY, photoUrls);
		args.putInt(POSITION_KEY, position);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected ZoomPresenter createPresenter() {
		if (getArguments() == null || getArguments().getStringArrayList(PHOTOS_URL_KEY) == null || getArguments().getInt(POSITION_KEY, -1) == -1) {
			throw new IllegalStateException("Fragment is missing arguments");
		}
		ArrayList<String> urls = getArguments().getStringArrayList(PHOTOS_URL_KEY);
		int position = getArguments().getInt(POSITION_KEY);
		return new ZoomPresenter(urls, position, this);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_zoom;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		// nop.
	}

	@Override
	public void initializeZoomView(List<String> photoUrls, int initialPosition) {
		vpGallery.setAdapter(new ZoomPhotosAdapter(photoUrls, getContext()));
		vpGallery.setOffscreenPageLimit(FIVE_PAGES);
		vpGallery.setCurrentItem(initialPosition, false);
		indicator.setViewPager(vpGallery);
	}
}
