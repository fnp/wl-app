package com.moiseum.wolnelektury.view.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.components.ProgressRecyclerView;
import com.moiseum.wolnelektury.components.recycler.EndlessRecyclerOnScrollListener;
import com.moiseum.wolnelektury.components.recycler.RecyclerAdapter;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.models.CategoryModel;
import com.moiseum.wolnelektury.view.book.BookActivity;
import com.moiseum.wolnelektury.view.book.BookType;
import com.moiseum.wolnelektury.view.book.list.BooksListAdapter;
import com.moiseum.wolnelektury.view.search.dto.FilterDto;
import com.moiseum.wolnelektury.view.search.filter.FilterActivity;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.moiseum.wolnelektury.view.search.filter.FilterActivity.FILTERS_REQUEST_CODE;
import static com.moiseum.wolnelektury.view.search.filter.FilterActivity.RESULT_FILTERS_KEY;

/**
 * Created by piotrostrowski on 16.11.2017.
 */

public class BookSearchFragment extends PresenterFragment<BookSearchPresenter> implements BookSearchView, SearchView.OnQueryTextListener {

	public static BookSearchFragment newInstance() {
		return new BookSearchFragment();
	}

	@BindView(R.id.rlFiltersContainer)
	RelativeLayout rlFiltersContainer;
	@BindView(R.id.rvFilters)
	RecyclerView rvFilters;
	@BindView(R.id.rvBooks)
	ProgressRecyclerView<BookModel> rvBooks;
	@BindView(R.id.pbLoadMore)
	ProgressBar pbLoadMore;
	@BindView(R.id.btnReloadMore)
	Button btnReloadMore;

	private SearchView svSearch;

	private BookSearchFiltersAdapter filtersAdapter;
	private RecyclerAdapter.OnItemClickListener<CategoryModel> filtersAdapterClickListener = new RecyclerAdapter
			.OnItemClickListener<CategoryModel>() {
		@Override
		public void onItemClicked(CategoryModel item, View view, int position) {
			filtersAdapter.removeItem(position);
			if (filtersAdapter.getItemCount() == 0) {
				rlFiltersContainer.setVisibility(View.GONE);
			}
			getPresenter().removeFilter(item);
		}
	};

	private BooksListAdapter searchAdapter;
	private EndlessRecyclerOnScrollListener rvBooksScrollListener = new EndlessRecyclerOnScrollListener() {
		@Override
		public void onLoadMore() {
			if (searchAdapter.getItemCount() > 0) {
				BookModel lastItem = searchAdapter.getItem(searchAdapter.getItemCount() - 1);
				getPresenter().loadMoreBooks(lastItem.getKey());
			}
		}
	};
	private RecyclerAdapter.OnItemClickListener<BookModel> searchAdapterClickListener = (item, view, position) -> getPresenter().bookSelected(item, position);

	@Override
	protected BookSearchPresenter createPresenter() {
		return new BookSearchPresenter(this);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_search;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		// Filters
		rvFilters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		rvFilters.setHasFixedSize(false);
		filtersAdapter = new BookSearchFiltersAdapter(getContext());
		filtersAdapter.setOnItemClickListener(filtersAdapterClickListener);
		rvFilters.setAdapter(filtersAdapter);

		// Search list
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		searchAdapter = new BooksListAdapter(getContext());
		searchAdapter.setOnItemClickListener(searchAdapterClickListener);
		rvBooks.setLayoutManager(layoutManager);
		rvBooks.addOnScrollListener(rvBooksScrollListener);
		rvBooks.setHasFixedSize(true);
		rvBooks.setAdapter(searchAdapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_search, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem item = menu.findItem(R.id.action_search);
		svSearch = (SearchView) item.getActionView();
		svSearch.setIconifiedByDefault(false);
		svSearch.setIconified(false);
		svSearch.setQueryRefinementEnabled(true);
		svSearch.setOnQueryTextListener(this);
		item.expandActionView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_filter) {
			getPresenter().onFiltersClicked();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		getPresenter().onSearchChosen(query);
		svSearch.clearFocus();
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		getPresenter().onSearchChanged(newText);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FILTERS_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.hasExtra(RESULT_FILTERS_KEY)) {
			FilterDto dto = Parcels.unwrap(data.getParcelableExtra(RESULT_FILTERS_KEY));
			getPresenter().updateFilters(dto);
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onDestroyView() {
		rvBooks.removeOnScrollListener(rvBooksScrollListener);
		super.onDestroyView();
	}

	@OnClick(R.id.ibClearFilters)
	public void onClearFiltersClick() {
		filtersAdapter.setItems(new ArrayList<>());
		rlFiltersContainer.setVisibility(View.GONE);
		getPresenter().clearFilters();
	}

	@OnClick(R.id.btnReloadMore)
	public void onReloadMoreClicked() {
		btnReloadMore.setVisibility(View.GONE);
		pbLoadMore.setVisibility(View.VISIBLE);

		BookModel lastItem = searchAdapter.getItem(searchAdapter.getItemCount() - 1);
		getPresenter().loadMoreBooks(lastItem.getKey());
	}

	@Override
	public void setData(List<BookModel> books, boolean reload) {
		if (reload) {
			rvBooks.setItems(books);
		} else {
			rvBooks.addItems(books);
		}
	}

	@Override
	public void setInitialProgressVisible(boolean visible) {
		rvBooksScrollListener.reset();
		searchAdapter.clear();
		rvBooks.setProgressVisible(visible);
	}

	@Override
	public void setLoadMoreProgressVisible(boolean visible) {
		pbLoadMore.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void showError(Exception e, boolean loadMore) {
		Toast.makeText(getContext(), R.string.loading_results_failed, Toast.LENGTH_SHORT).show();
		if (loadMore) {
			btnReloadMore.setVisibility(View.VISIBLE);
		} else {
			searchAdapter.clear();
			rvBooks.showRetryButton(() -> getPresenter().retryInitialLoad());
		}
	}

	@Override
	public void presentBookDetails(String bookSlug) {
		startActivity(new BookActivity.BookIntent(bookSlug, BookType.TYPE_DEFAULT, getContext()));
	}

	@Override
	public void applyFilters(List<CategoryModel> filters) {
		filtersAdapter.setItems(filters);
		rlFiltersContainer.setVisibility((filters.size() > 0) ? View.VISIBLE : View.GONE);
	}

	@Override
	public void displayFiltersView(FilterDto filters) {
		FilterActivity.FilterIntent filterIntent = new FilterActivity.FilterIntent(getContext(), filters);
		startActivityForResult(filterIntent, FILTERS_REQUEST_CODE);
	}

	@Override
	public void updateFavouriteState(boolean state, Integer clickedPosition) {
		if (clickedPosition != null) {
			searchAdapter.getItem(clickedPosition).setLiked(state);
			searchAdapter.notifyDataSetChanged();
		}
	}
}
