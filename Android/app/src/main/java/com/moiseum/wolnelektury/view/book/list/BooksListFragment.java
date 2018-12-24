package com.moiseum.wolnelektury.view.book.list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.components.ProgressRecyclerView;
import com.moiseum.wolnelektury.components.recycler.EndlessRecyclerOnScrollListener;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.view.book.BookActivity;
import com.moiseum.wolnelektury.view.book.BookType;
import com.moiseum.wolnelektury.view.main.MainActivity;
import com.moiseum.wolnelektury.view.main.NavigationElement;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author golonkos
 */

public class BooksListFragment extends PresenterFragment<BooksListPresenter> implements BooksListView {

	private static final String PARAM_LIST_TYPE = "PARAM_LIST_TYPE";

	@BindView(R.id.rvBooksList)
	ProgressRecyclerView<BookModel> rvBooksList;
	@BindView(R.id.pbLoadMore)
	ProgressBar pbLoadMore;
	@BindView(R.id.btnReloadMore)
	Button btnReloadMore;

	public static BooksListFragment newInstance(BookListType type) {
		BooksListFragment fragment = new BooksListFragment();
		Bundle args = new Bundle(1);
		args.putSerializable(PARAM_LIST_TYPE, type);
		fragment.setArguments(args);
		return fragment;
	}

	private BookListType type;
	private BooksListAdapter adapter;

	private EndlessRecyclerOnScrollListener rvBooksScrollListener = new EndlessRecyclerOnScrollListener() {
		@Override
		public void onLoadMore() {
			if (adapter.getItemCount() > 0) {
				getPresenter().loadMoreBooks();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (getArguments() == null || getArguments().getSerializable(PARAM_LIST_TYPE) == null) {
			throw new IllegalStateException("Missing list type parameter.");
		}
		type = (BookListType) getArguments().getSerializable(PARAM_LIST_TYPE);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected BooksListPresenter createPresenter() {
		return new BooksListPresenter(this, type);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_books_list;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		rvBooksList.setLayoutManager(layoutManager);
		rvBooksList.addOnScrollListener(rvBooksScrollListener);

		adapter = new BooksListAdapter(getContext());
		adapter.setOnItemClickListener((item, view1, position) -> getPresenter().onBookClicked(item, position));
		if (type.isDeletable()) {
			adapter.setOnDeleteListener((book, position) -> {
				getPresenter().onBookDeleteClicked(book);
				adapter.removeItem(position);
				Toast.makeText(getContext() ,getString(R.string.book_deleted_message), Toast.LENGTH_SHORT).show();
			});
		}
		rvBooksList.setAdapter(adapter);
		rvBooksList.setEmptyText(type.getEmptyListText());
		if (type.isSearchable()) {
			setHasOptionsMenu(true);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (type.isSearchable()) {
			inflater.inflate(R.menu.menu_searchable, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_search) {
			EventBus.getDefault().post(new MainActivity.ChangeNavigationEvent(NavigationElement.SEARCH));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected String getNameForTracker() {
		return type.getNameForTracker();
	}

	@Override
	public void setData(List<BookModel> books) {
		if (adapter.getItemCount() == 0) {
			rvBooksList.setItems(books);
		} else {
			rvBooksList.addItems(books);
		}
	}

	@Override
	public void clearList() {
		adapter.clear();
	}

	@Override
	public void setProgressVisible(boolean visible) {
		if (adapter.getItemCount() == 0) {
			rvBooksList.setProgressVisible(visible);
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
			rvBooksList.showRetryButton(() -> getPresenter().reloadBooks());
		}
	}

	@Override
	public void openBookDetailsView(String bookSlug) {
		startActivity(new BookActivity.BookIntent(bookSlug, BookType.TYPE_DEFAULT, getActivity()));
	}

	@Override
	public void updateEmptyViewVisibility() {
		rvBooksList.updateEmptyViewVisibility();
	}

	@Override
	public void updateFavouriteState(boolean state, Integer clickedPosition) {
		if (clickedPosition != null) {
			adapter.getItem(clickedPosition).setLiked(state);
			adapter.notifyDataSetChanged();
		}
	}

	@OnClick(R.id.btnReloadMore)
	public void onReloadMoreClicked() {
		btnReloadMore.setVisibility(View.GONE);
		pbLoadMore.setVisibility(View.VISIBLE);
		getPresenter().loadMoreBooks();
	}
}
