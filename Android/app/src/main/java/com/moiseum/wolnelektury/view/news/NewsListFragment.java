package com.moiseum.wolnelektury.view.news;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.components.ProgressRecyclerView;
import com.moiseum.wolnelektury.components.recycler.EndlessRecyclerOnScrollListener;
import com.moiseum.wolnelektury.connection.models.NewsModel;
import com.moiseum.wolnelektury.view.news.single.NewsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NewsListFragment extends PresenterFragment<NewsListPresenter> implements NewsListView {

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @BindView(R.id.rvNews)
    ProgressRecyclerView<NewsModel> rvNews;
	@BindView(R.id.pbLoadMore)
	ProgressBar pbLoadMore;
	@BindView(R.id.btnReloadMore)
	Button btnReloadMore;

    private NewsListAdapter adapter;
	private EndlessRecyclerOnScrollListener rvBooksScrollListener = new EndlessRecyclerOnScrollListener() {
		@Override
		public void onLoadMore() {
			if (adapter.getItemCount() > 0) {
				getPresenter().loadMoreNews();
			}
		}
	};

    @Override
    protected NewsListPresenter createPresenter() {
        return new NewsListPresenter(this);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_news;
    }

    @Override
    public void prepareView(View view, Bundle savedInstanceState) {
	    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
	    rvNews.setLayoutManager(layoutManager);
	    rvNews.addOnScrollListener(rvBooksScrollListener);

	    adapter = new NewsListAdapter(getContext());
	    adapter.setOnItemClickListener((item, view1, position) -> getPresenter().onNewsClicked(item));
	    rvNews.setAdapter(adapter);
    }

	@Override
	public void setData(List<NewsModel> data) {
		if (adapter.getItemCount() == 0) {
			rvNews.setItems(data);
		} else {
			rvNews.addItems(data);
		}
	}

	@Override
	public void setProgressVisible(boolean visible) {
		if (adapter.getItemCount() == 0) {
			rvNews.setProgressVisible(visible);
		} else {
			pbLoadMore.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void showError(Exception e) {
		Toast.makeText(getContext(), R.string.loading_results_failed, Toast.LENGTH_SHORT).show();
		if (adapter.getItemCount() != 0) {
			btnReloadMore.setVisibility(View.VISIBLE);
		} else {
			rvNews.showRetryButton(() -> getPresenter().loadMoreNews());
		}
	}

	@Override
	public void launchNews(NewsModel news) {
		startActivity(new NewsActivity.NewsIntent(news, getContext()));
	}

	@OnClick(R.id.btnReloadMore)
	public void onReloadMoreClicked() {
		btnReloadMore.setVisibility(View.GONE);
		pbLoadMore.setVisibility(View.VISIBLE);
		getPresenter().loadMoreNews();
	}
}