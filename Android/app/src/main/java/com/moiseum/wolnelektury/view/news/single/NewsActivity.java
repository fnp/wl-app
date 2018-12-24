package com.moiseum.wolnelektury.view.news.single;

import android.content.Context;
import android.os.Bundle;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractActivity;
import com.moiseum.wolnelektury.base.AbstractIntent;
import com.moiseum.wolnelektury.connection.models.NewsModel;

import org.parceler.Parcels;

import static com.moiseum.wolnelektury.view.news.single.NewsActivity.NewsIntent.NEWS_KEY;

/**
 * Created by Piotr Ostrowski on 06.08.2018.
 */
public class NewsActivity extends AbstractActivity {

	private static final String NEWS_FRAGMENT_TAG = "NewsFragmentTag";

	public static class NewsIntent extends AbstractIntent {
		static final String NEWS_KEY = "NewsKey";

		public NewsIntent(NewsModel news, Context context) {
			super(context, NewsActivity.class);
			putExtra(NEWS_KEY, Parcels.wrap(news));
		}
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_blank;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {
		if (!getIntent().hasExtra(NEWS_KEY)) {
			throw new IllegalStateException("Activity intent is missing news extras.");
		}
		setTitle("");

		NewsModel news = Parcels.unwrap(getIntent().getParcelableExtra(NEWS_KEY));
		NewsFragment newsFragment = (NewsFragment) getSupportFragmentManager().findFragmentByTag(NEWS_FRAGMENT_TAG);
		if (newsFragment == null) {
			newsFragment = NewsFragment.newInstance(news);
			getSupportFragmentManager().beginTransaction().add(R.id.flContainer, newsFragment, NEWS_FRAGMENT_TAG).commit();
		}
	}
}
