package com.moiseum.wolnelektury.view.library;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.components.ProgressRecyclerView;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.utils.StringUtils;
import com.moiseum.wolnelektury.view.book.BookActivity;
import com.moiseum.wolnelektury.view.book.BookType;
import com.moiseum.wolnelektury.view.book.list.BookListActivity;
import com.moiseum.wolnelektury.view.book.list.BookListType;
import com.moiseum.wolnelektury.view.main.MainActivity;
import com.moiseum.wolnelektury.view.main.NavigationElement;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author golonkos
 */

public class LibraryFragment extends PresenterFragment<LibraryPresenter> implements LibraryView {

	@BindView(R.id.rlReadingNowContainer)
	View rlReadingNowContainer;
	@BindView(R.id.rvNowReading)
	ProgressRecyclerView<BookModel> rvNowReading;
	@BindView(R.id.rvNewest)
	ProgressRecyclerView<BookModel> rvNewest;
	@BindView(R.id.rvRecommended)
	ProgressRecyclerView<BookModel> rvRecommended;
	@BindView(R.id.ivBookCover)
	ImageView ivBookCover;
	@BindView(R.id.tvBookAuthor)
	TextView tvBookAuthor;
	@BindView(R.id.tvBookTitle)
	TextView tvBookTitle;
	@BindView(R.id.tvBookEpoch)
	TextView tvBookEpoch;
	@BindView(R.id.tvBookKind)
	TextView tvBookKind;
	@BindView(R.id.tvBookGenre)
	TextView tvBookGenre;
	@BindView(R.id.pbHeaderLoading)
	ProgressBar pbHeaderLoading;
	@BindView(R.id.tvEmpty)
	TextView tvEmpty;
	@BindView(R.id.ibRetry)
	ImageButton ibRetry;
	@BindView(R.id.rlHeaderLoadingContainer)
	RelativeLayout rlHeaderLoadingContainer;
	@BindView(R.id.rlBecomeAFriend)
	View rlBecomeAFriend;
	@BindView(R.id.vBecomeAFriendSeparator)
	View vBecomeAFriendSeparator;
	@BindView(R.id.ivAudiobook)
	ImageView ivHeaderAudiobook;

	public static LibraryFragment newInstance() {
		return new LibraryFragment();
	}

	@Override
	protected LibraryPresenter createPresenter() {
		return new LibraryPresenter(this);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_library;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		initList(rvNowReading);
		initList(rvNewest);
		initList(rvRecommended);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_searchable, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_search) {
			EventBus.getDefault().post(new MainActivity.ChangeNavigationEvent(NavigationElement.SEARCH));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void showBecomeAFriendHeader(boolean premium) {
		rlBecomeAFriend.setVisibility(premium ? View.GONE : View.VISIBLE);
		vBecomeAFriendSeparator.setVisibility(premium ? View.VISIBLE : View.GONE);
	}

	@Override
	public void initHeader(BookDetailsModel item) {
		if (item.getCoverThumb() != null) {
			String coverUrl = item.getCoverThumb();
			if (!coverUrl.contains(RestClient.MEDIA_URL) && !coverUrl.contains(RestClient.MEDIA_URL_HTTPS)) {
				coverUrl = RestClient.MEDIA_URL_HTTPS + coverUrl;
			}
			Glide.with(getContext()).load(coverUrl).placeholder(R.drawable.list_nocover).diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().into(ivBookCover);
		} else {
			ivBookCover.setImageResource(R.drawable.list_nocover);
		}
		tvBookAuthor.setText(StringUtils.joinCategory(item.getAuthors(), ", "));
		tvBookTitle.setText(item.getTitle());
		tvBookEpoch.setText(StringUtils.joinCategory(item.getEpochs(), ", "));
		tvBookKind.setText(StringUtils.joinCategory(item.getKinds(), ", "));
		tvBookGenre.setText(StringUtils.joinCategory(item.getGenres(), ", "));
		ivHeaderAudiobook.setVisibility(item.hasAudio() ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setProgressContainerVisible(boolean visible) {
		if (visible) {
			rlHeaderLoadingContainer.setVisibility(View.VISIBLE);
		} else {
			rlHeaderLoadingContainer.setVisibility(View.GONE);
		}
	}

	@Override
	public void showHeaderError() {
		ibRetry.setVisibility(View.VISIBLE);
	}

	@Override
	public void setHeaderProgressVisible(boolean visible) {
		pbHeaderLoading.setVisibility(visible ? View.VISIBLE : View.GONE);
		if (visible) {
			tvEmpty.setVisibility(View.GONE);
		}
	}

	@Override
	public void showHeaderEmpty(boolean userLoggedIn) {
		tvEmpty.setVisibility(View.VISIBLE);
		tvEmpty.setText(userLoggedIn ? R.string.no_prapremiere_message_logged : R.string.no_prapremiere_message);
	}

	private void initList(ProgressRecyclerView<BookModel> rvList) {
		rvList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		rvList.setHasFixedSize(true);
		rvList.setEmptyText(R.string.read_now_library_empty);
		LibraryAdapter adapter = new LibraryAdapter(getContext());
		adapter.setOnItemClickListener((item, view, position) -> getPresenter().onBookClicked(item));
		rvList.setAdapter(adapter);
	}

	@Override
	public void setNowReadingVisibility(boolean visible) {
		rlReadingNowContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
		rvNowReading.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setNewest(List<BookModel> books) {
		rvNewest.setItems(books);
	}

	@Override
	public void setNewestProgressVisible(boolean visible) {
		rvNewest.setProgressVisible(visible);
	}

	@Override
	public void showNewestError(Exception e) {
		Toast.makeText(getContext(), R.string.loading_results_failed, Toast.LENGTH_SHORT).show();
		rvNewest.showRetryButton(() -> getPresenter().reloadNewest());
	}

	@Override
	public void setRecommended(List<BookModel> books) {
		rvRecommended.setItems(books);
	}

	@Override
	public void setRecommendedProgressVisible(boolean visible) {
		rvRecommended.setProgressVisible(visible);
	}

	@Override
	public void showRecommendedError(Exception e) {
		Toast.makeText(getContext(), R.string.loading_results_failed, Toast.LENGTH_SHORT).show();
		rvRecommended.showRetryButton(() -> getPresenter().reloadRecommended());
	}

	@Override
	public void setNowReading(List<BookModel> books) {
		rvNowReading.setItems(books);
	}

	@Override
	public void setNowReadingProgressVisible(boolean visible) {
		rvNowReading.setProgressVisible(visible);
	}

	@Override
	public void showNowReadingError(Exception e) {
		Toast.makeText(getContext(), R.string.loading_results_failed, Toast.LENGTH_SHORT).show();
		rvNowReading.showRetryButton(() -> getPresenter().reloadNowReading());
	}

	@Override
	public void openBookDetailsView(String slug, BookType bookType) {
		startActivity(new BookActivity.BookIntent(slug, bookType, getContext()));
	}

	@OnClick(R.id.btnBecomeAFriend)
	public void onBecomeAFriendClick() {
		showPayPalForm();
	}

	@OnClick(R.id.btnNewestSeeAll)
	public void onNewestSeeAllClicked() {
		startActivity(new BookListActivity.BookListIntent(BookListType.NEWEST, getActivity()));
	}

	@OnClick(R.id.btnRecommendedSeeAll)
	public void onRecommendedSeeAllClicked() {
		startActivity(new BookListActivity.BookListIntent(BookListType.RECOMMENDED, getActivity()));
	}

	@OnClick(R.id.btnNowReadingSeeAll)
	public void onNowReadingSeeAllClicked() {
		getPresenter().onNowReadingSeeAllClicked();
	}

	@OnClick(R.id.ibRetry)
	public void onHeaderRetryClicked() {
		getPresenter().fetchHeader();
		ibRetry.setVisibility(View.GONE);
	}

	@OnClick(R.id.libraryHeader)
	public void onLibraryHeaderClicked() {
		getPresenter().onPremiereHeaderClicked();
	}
}
