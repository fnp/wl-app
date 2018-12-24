package com.moiseum.wolnelektury.view.news;

import android.os.Bundle;

import com.moiseum.wolnelektury.base.DataObserver;
import com.moiseum.wolnelektury.base.DataProvider;
import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.models.NewsModel;
import com.moiseum.wolnelektury.connection.services.NewsService;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

/**
 * Created by Piotr Ostrowski on 06.08.2018.
 */
class NewsListPresenter extends FragmentPresenter<NewsListView> {

	private class NewsListDataProvider extends DataProvider<List<NewsModel>, NewsService> {

		@Override
		protected Class<NewsService> getServiceClass() {
			return NewsService.class;
		}

		@Override
		public Call<List<NewsModel>> execute(NewsService service) {
			return service.getNews(lastKeySlug, RestClient.PAGINATION_LIMIT);
		}
	}

	private class NewsListDataObserver implements DataObserver<List<NewsModel>> {

		@Override
		public void onLoadStarted() {
			getView().setProgressVisible(true);
		}

		@Override
		public void onLoadSuccess(List<NewsModel> data) {
			if (data.size() > 0) {
				lastKey = data.get(data.size() - 1).getKey();
			}
			getView().setProgressVisible(false);
			getView().setData(data);
		}

		@Override
		public void onLoadFailed(Exception e) {
			getView().setProgressVisible(false);
			getView().setData(Collections.emptyList());
			getView().showError(e);
		}
	}

	private NewsListDataProvider dataProvider;
	private String lastKey = null;

	NewsListPresenter(NewsListView view) {
		super(view);
		dataProvider = new NewsListDataProvider();
		dataProvider.setDataObserver(new NewsListDataObserver());
	}

	@Override
	public void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
		dataProvider.load(null);
	}

	public void loadMoreNews() {
		dataProvider.load(lastKey);
	}

	public void onNewsClicked(NewsModel newsModel) {
		getView().launchNews(newsModel);
	}
}
