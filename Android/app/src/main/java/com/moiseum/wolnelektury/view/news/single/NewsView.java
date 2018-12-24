package com.moiseum.wolnelektury.view.news.single;

import com.moiseum.wolnelektury.connection.models.NewsModel; /**
 * Created by Piotr Ostrowski on 06.08.2018.
 */
public interface NewsView {

	void initializeNewsView(NewsModel news);

	void startShareActivity(String shareUrl);
}
