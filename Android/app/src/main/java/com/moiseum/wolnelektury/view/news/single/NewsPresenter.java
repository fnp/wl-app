package com.moiseum.wolnelektury.view.news.single;

import android.os.Bundle;

import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;
import com.moiseum.wolnelektury.connection.models.NewsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piotr Ostrowski on 06.08.2018.
 */
class NewsPresenter extends FragmentPresenter<NewsView> {

	private final NewsModel news;

	NewsPresenter(NewsModel news, NewsView view) {
		super(view);
		this.news = news;

		List<String> galleryUrls = new ArrayList<>(news.getGalleryUrl().size() + 1);
		if (news.getImageUrl() != null) {
			galleryUrls.add(news.getImageUrl());
		}
		galleryUrls.addAll(news.getGalleryUrl());
		this.news.setGalleryUrl(galleryUrls);
	}

	@Override
	public void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
		getView().initializeNewsView(news);
	}

	void onShareNewsClicked() {
		getView().startShareActivity(news.getUrl());
	}
}
