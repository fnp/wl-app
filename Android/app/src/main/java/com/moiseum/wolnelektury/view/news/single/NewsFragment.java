package com.moiseum.wolnelektury.view.news.single;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.connection.models.NewsModel;
import com.moiseum.wolnelektury.view.news.zoom.ZoomActivity;

import org.parceler.Parcels;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Piotr Ostrowski on 06.08.2018.
 */
public class NewsFragment extends PresenterFragment<NewsPresenter> implements NewsView {

	private static final String NEWS_ARGUMENT_KEY = "NewsArgumentKey";
	private static final int FIVE_PAGES = 5;

	public static NewsFragment newInstance(NewsModel news) {
		NewsFragment fragment = new NewsFragment();
		Bundle args = new Bundle(1);
		args.putParcelable(NEWS_ARGUMENT_KEY, Parcels.wrap(news));
		fragment.setArguments(args);
		return fragment;
	}

	@BindView(R.id.vpGallery)
	ViewPager vpGallery;
	@BindView(R.id.indicator)
	CircleIndicator indicator;
	@BindView(R.id.tvNewsTitle)
	TextView tvNewsTitle;
	@BindView(R.id.tvNewsTime)
	TextView tvNewsTime;
	@BindView(R.id.tvNewsPlace)
	TextView tvNewsPlace;
	@BindView(R.id.tvNewsBody)
	HtmlTextView tvNewsBody;
	@BindView(R.id.clMainView)
	View clMainView;
	@BindView(R.id.ctlCollapse)
	CollapsingToolbarLayout ctlCollapse;
	@BindView(R.id.llContentContainer)
	LinearLayout llContentContainer;

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_single_news;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		Toolbar toolbar = view.findViewById(R.id.bookToolbar);
		setupToolbar(toolbar);
	}

	@Override
	protected NewsPresenter createPresenter() {
		if (getArguments() == null || getArguments().getParcelable(NEWS_ARGUMENT_KEY) == null) {
			throw new IllegalStateException("Fragment is missing arguments");
		}
		NewsModel news = Parcels.unwrap(getArguments().getParcelable(NEWS_ARGUMENT_KEY));
		return new NewsPresenter(news, this);
	}

	@OnClick(R.id.fabShare)
	public void onShareClick() {
		getPresenter().onShareNewsClicked();
	}

	@Override
	public void initializeNewsView(NewsModel news) {
		ctlCollapse.setTitle(news.getTitle());
		ctlCollapse.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
		tvNewsTitle.setText(news.getTitle());
		tvNewsTime.setText(news.getTime());
		tvNewsPlace.setText(news.getPlace());
		tvNewsBody.setHtml(news.getBody());

		NewsGalleryAdapter galleryAdapter = new NewsGalleryAdapter(news.getGalleryUrl(), getContext());
		vpGallery.setAdapter(galleryAdapter);
		vpGallery.setOffscreenPageLimit(FIVE_PAGES);
		indicator.setViewPager(vpGallery);
		addDisposable(galleryAdapter.getPageClickObservable().subscribe(position -> {
			ArrayList<String> urls = new ArrayList<>(news.getGalleryUrl());
			startActivity(new ZoomActivity.ZoomIntent(urls, position, getContext()));
		}));
		enableToolbarCollapse();
	}

	@Override
	public void startShareActivity(String shareUrl) {
		showShareActivity(shareUrl);
	}

	private void enableToolbarCollapse() {
		ViewTreeObserver viewTreeObserver = llContentContainer.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					llContentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					if (ctlCollapse.getHeight() + llContentContainer.getHeight() > clMainView.getHeight()) {
						AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) ctlCollapse.getLayoutParams();
						params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
						ctlCollapse.setLayoutParams(params);
					}
				}
			});
		}
	}
}
