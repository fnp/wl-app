package com.moiseum.wolnelektury.view.news;

import com.moiseum.wolnelektury.base.mvp.LoadingView;
import com.moiseum.wolnelektury.connection.models.NewsModel;

import java.util.List;

/**
 * Created by Piotr Ostrowski on 06.08.2018.
 */
public interface NewsListView extends LoadingView<List<NewsModel>> {

	void launchNews(NewsModel news);
}
