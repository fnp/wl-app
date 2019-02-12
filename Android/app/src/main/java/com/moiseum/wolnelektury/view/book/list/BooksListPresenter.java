package com.moiseum.wolnelektury.view.book.list;

import android.os.Bundle;

import com.folioreader.util.AppUtil;
import com.moiseum.wolnelektury.base.DataObserver;
import com.moiseum.wolnelektury.base.DataProvider;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;
import com.moiseum.wolnelektury.connection.downloads.FileCacheUtils;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.services.BooksService;
import com.moiseum.wolnelektury.storage.BookStorage;
import com.moiseum.wolnelektury.events.BookFavouriteEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author golonkos
 */

class BooksListPresenter extends FragmentPresenter<BooksListView> {

	private static final String TAG = BooksListPresenter.class.getSimpleName();

	private final DataProvider<List<BookModel>, BooksService> dataProvider;
	private BookStorage storage;
	private final BookListType bookListType;
	private String lastKey;
	private Integer clickedPosition;

	BooksListPresenter(BooksListView view, BookListType type) {
		super(view);
		bookListType = type;
		lastKey = null;
		dataProvider = type.getDataProvider();
		dataProvider.setDataObserver(new BooksListDataObserver());
		storage = WLApplication.getInstance().getBookStorage();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
		loadBooks();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dataProvider.cancel();
		EventBus.getDefault().unregister(this);
	}

	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onBookAdded(BookStorage.BookAddedEvent event) {
		if (bookListType == BookListType.DOWNLOADED) {
			reloadBooks();
		}
	}

	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onBookDeleted(BookStorage.BookDeletedEvent event) {
		if (bookListType == BookListType.DOWNLOADED) {
			reloadBooks();
		}
	}

	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFavouriteStateChanged(BookFavouriteEvent event) {
		// TODO: Change that full reload
		if (bookListType == BookListType.FAVOURITES) {
			reloadBooks();
		} else {
			getView().updateFavouriteState(event.getState(), clickedPosition);
		}
	}

	public void reloadBooks() {
		lastKey = null;
		getView().clearList();
		loadBooks();
	}

	public void loadMoreBooks() {
		if (bookListType.isPageable()) {
			loadBooks();
		}
	}

	private void loadBooks() {
		dataProvider.load(lastKey);
	}

	void onBookClicked(BookModel book, int position) {
		clickedPosition = position;
		getView().openBookDetailsView(book.getSlug());
	}

	void onBookDeleteClicked(BookModel book) {
		BookModel downloadedBook = storage.find(book.getSlug());

		List<Completable> deletionOperations = new ArrayList<>();
		if (downloadedBook != null && downloadedBook.isEbookDownloaded()) {
			AppUtil.removeBookState(WLApplication.getInstance().getApplicationContext(), downloadedBook.getEbookName());
			deletionOperations.add(FileCacheUtils.deleteEbookFile(downloadedBook.getEbookFileUrl()));
		}
		if (downloadedBook != null && downloadedBook.isAudioDownloaded()) {
			deletionOperations.add(FileCacheUtils.deleteAudiobookFiles(downloadedBook.getAudioFileUrls()));
		}

		storage.remove(book.getSlug(), false);
		addDisposable(Completable.concat(deletionOperations).andThen(Completable.fromAction(() -> {
		})).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> getView().updateEmptyViewVisibility(), error -> {
			getView().showError(new Exception(error));
			//restore books
			loadBooks();
		}));
	}

	private class BooksListDataObserver implements DataObserver<List<BookModel>> {

		private List<BookModel> matchDownloadedBooks(List<BookModel> books) {
			if (bookListType.isDeletable()) {
				List<BookModel> merged = new ArrayList<>(books.size());
				List<BookModel> downloadedBooks = storage.all();

				for (BookModel book : books) {
					boolean found = false;
					for (BookModel downloaded : downloadedBooks) {
						if (book.getSlug().equals(downloaded.getSlug())) {
							merged.add(downloaded);
							found = true;
							break;
						}
					}
					if (!found) {
						merged.add(book);
					}
				}
				return merged;
			} else {
				return books;
			}
		}

		@Override
		public void onLoadStarted() {
			getView().setProgressVisible(true);
		}

		@Override
		public void onLoadSuccess(List<BookModel> data) {
			if (data.size() > 0) {
				lastKey = data.get(data.size() - 1).getSortedKey();
				data = matchDownloadedBooks(data);
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
}
