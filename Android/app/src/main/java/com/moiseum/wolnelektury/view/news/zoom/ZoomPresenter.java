package com.moiseum.wolnelektury.view.news.zoom;

import android.os.Bundle;

import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;

import java.util.List;

/**
 * Created by Piotr Ostrowski on 07.08.2018.
 */
public class ZoomPresenter extends FragmentPresenter<ZoomView> {

	private final List<String> photoUrls;
	private final int initialPosition;

	ZoomPresenter(List<String> photoUrls, int initialPosition, ZoomView view) {
		super(view);
		this.photoUrls = photoUrls;
		this.initialPosition = initialPosition;
	}

	@Override
	public void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
		getView().initializeZoomView(photoUrls, initialPosition);
	}
}
