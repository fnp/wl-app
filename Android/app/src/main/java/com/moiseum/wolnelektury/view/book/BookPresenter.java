package com.moiseum.wolnelektury.view.book;

import android.os.Bundle;
import android.util.Log;

import com.folioreader.util.AppUtil;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;
import com.moiseum.wolnelektury.connection.downloads.FileCacheUtils;
import com.moiseum.wolnelektury.connection.downloads.FileDownloadIntentService;
import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.models.FavouriteStateModel;
import com.moiseum.wolnelektury.connection.models.ReadingStateModel;
import com.moiseum.wolnelektury.connection.services.BooksService;
import com.moiseum.wolnelektury.events.BookFavouriteEvent;
import com.moiseum.wolnelektury.storage.BookStorage;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;
import com.moiseum.wolnelektury.view.book.components.ProgressDownloadButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Piotr Ostrowski on 17.11.2017.
 */

class BookPresenter extends FragmentPresenter<BookView> {

	private static final String TAG = BookPresenter.class.getSimpleName();

	private BookDetailsModel book;
	private BookModel storedBook;
	private String bookSlug;
	private BookType bookType;

	private SharedPreferencesUtils preferences = WLApplication.getInstance().getPreferences();
	private BooksService booksService = WLApplication.getInstance().getRestClient().obtainBookService();
	private BookStorage storage = WLApplication.getInstance().getBookStorage();

	BookPresenter(String slug, BookType type, BookView view) {
		super(view);
		this.bookSlug = slug;
		this.bookType = type;
	}

	@Override
	public void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
		loadBookDetails();
		getView().showPremiumLock(!preferences.isUserPremium() && bookType.shouldShowPremiumLock());
		EventBus.getDefault().register(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		this.storedBook = storage.find(bookSlug);
		if (storedBook != null && storedBook.isEbookDownloaded()) {
			getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_READING, false);
			getView().updateReadingProgress(storedBook.getCurrentChapter(), storedBook.getTotalChapters(), false);
		}
		if (storedBook != null && storedBook.isAudioDownloaded()) {
			getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_READING, true);
			getView().updateReadingProgress(storedBook.getCurrentAudioChapter(), storedBook.getTotalAudioChapters(), true);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(FileCacheUtils.DownloadProgressEvent event) {
		if (event.getFileUrl().equals(book.getEpub())) {
			int percentage = (int) ((double) event.getDownloaded() / (double) event.getTotal() * 100.0);
			getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_DOWNLOADING, false);
			getView().showCurrentStateProgress(percentage, false);
		} else if (book.getAudiobookFilesUrls().contains(event.getFileUrl())) {
			ArrayList<String> filesUrls = book.getAudiobookFilesUrls();
			int fileIndex = filesUrls.indexOf(event.getFileUrl());
			double part = (double) event.getDownloaded() / (double) event.getTotal() / (double) filesUrls.size();
			double completed = (double) fileIndex / (double) filesUrls.size();
			int percentage = (int) ((part + completed) * 100.0);
			getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_DOWNLOADING, true);
			getView().showCurrentStateProgress(percentage, true);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(FileDownloadIntentService.DownloadFileEvent event) {
		if (event.getFileUrl().equals(book.getEpub())) {
			if (event.isSuccess()) {
                storeDownloadedBook(false);
				getView().showCurrentStateProgress(0, false);
				getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_READING, false);
				launchFolioReader();
			} else {
				getView().showCurrentStateProgress(0, false);
				getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_INITIAL, false);
				getView().showDownloadFileError();
			}
		} else if (book.getAudiobookFilesUrls().contains(event.getFileUrl())) {
			if (event.isSuccess()) {
				ArrayList<String> filesUrls = book.getAudiobookFilesUrls();
				int fileIndex = filesUrls.indexOf(event.getFileUrl());
				if (fileIndex == filesUrls.size() - 1) {
                    storeDownloadedBook(true);
					getView().showCurrentStateProgress(0, true);
					getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_READING, true);
					launchAudioPlayer();
				} else {
					int percentage = (int) ((fileIndex + 1.0) / ((double) filesUrls.size()) * 100.0);
					getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_DOWNLOADING, true);
					getView().showCurrentStateProgress(percentage, true);
				}
			} else {
				getView().showCurrentStateProgress(0, true);
				getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_INITIAL, true);
				getView().showDownloadFileError();
			}

		}
	}

	void launchEbookForState(ProgressDownloadButton.ProgressDownloadButtonState state) {
		if (state.isDownloaded()) {
			launchFolioReader();
		} else {
			getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_DOWNLOADING, false);
			FileDownloadIntentService.downloadFile(getView().getContext(), book.getEpub());
		}
	}

	void launchAudiobookForState(ProgressDownloadButton.ProgressDownloadButtonState state) {
		if (state.isDownloaded()) {
			launchAudioPlayer();
		} else {
			getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_DOWNLOADING, true);
			FileDownloadIntentService.downloadFiles(getView().getContext(), book.getAudiobookFilesUrls());
		}
	}

	void reloadBookDetails() {
		loadBookDetails();
	}

	void deleteEbook() {
		addDisposable(FileCacheUtils.deleteEbookFile(book.getEpub())
				.andThen(Completable.fromAction(this::updateStoredBookAfterDeletion))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						() -> getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_INITIAL, false),
						error -> { }
				)
		);
	}

	void deleteAudiobook() {
		addDisposable(FileCacheUtils.deleteAudiobookFiles(book.getAudiobookFilesUrls())
				.andThen(Completable.fromAction(this::updateStoredAudiobookAfterDeletion))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						() -> getView().changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState.STATE_INITIAL, true),
						error -> { }
				)
		);
	}

	void onShareEbookClicked() {
		getView().startShareActivity(book.getUrl());
	}

	void onFavouriteEbookClicked() {
		if (!book.getFavouriteState()) {
			getView().startLikeClicked();
			updateFavouriteState(true);
		} else {
			getView().stopLikeClicked();
			updateFavouriteState(false);
		}
	}

	void onBackFromReader(String bookName, int currentChapter, int count) {
		storedBook.setEbookName(bookName);
		storedBook.setCurrentChapter(currentChapter);
		storedBook.setTotalChapters(count);
		storage.update(storedBook);
		getView().updateReadingProgress(currentChapter, count, false);

		if (currentChapter == count && book.getState() == ReadingStateModel.ReadingState.STATE_READING) {
			updateReadingState(ReadingStateModel.ReadingState.STATE_COMPLETED);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------
	// Helper methods
	// ------------------------------------------------------------------------------------------------------------------------------------------

	private void updateStoredBookAfterDeletion() {
		if (!storedBook.isAudioDownloaded()) {
			storage.remove(storedBook.getSlug(), true);
			storedBook = null;
			return;
		}

		AppUtil.removeBookState(WLApplication.getInstance().getApplicationContext(), storedBook.getEbookName());
		storedBook.setEbookName(null);
		storedBook.setEbookFileUrl(null);
		storedBook.setCurrentChapter(0);
		storedBook.setTotalChapters(0);
		storage.update(storedBook);
	}

	private void updateStoredAudiobookAfterDeletion() {
		if (!storedBook.isEbookDownloaded()) {
			storage.remove(storedBook.getSlug(), true);
			storedBook = null;
			return;
		}

		storedBook.setAudioFileUrls(null);
		storedBook.setCurrentAudioChapter(0);
		storedBook.setTotalAudioChapters(0);
		storage.update(storedBook);
	}

	private Single<BookDetailsModel> getBookDetails() {
		Single<ReadingStateModel> readingStateSingle = preferences.isUserLoggedIn() ? booksService.getReadingState(bookSlug) : Single.just(new ReadingStateModel());
		Single<FavouriteStateModel> favouriteStateModelSingle = preferences.isUserLoggedIn() ? booksService.getFavouriteState(bookSlug) : Single.just(new FavouriteStateModel());
		Single<BookDetailsModel> bookDetailsSingle = Single.zip(
				readingStateSingle,
				favouriteStateModelSingle,
				booksService.getBookDetails(bookSlug),
				(readingStateModel, favouriteStateModel, bookDetailsModel) -> {
					bookDetailsModel.setState(readingStateModel.getState());
					bookDetailsModel.setFavouriteState(favouriteStateModel.getState());
			return bookDetailsModel;
		});
		return bookDetailsSingle.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	private void loadBookDetails() {
		addDisposable(getBookDetails().subscribe(bookDetailsModel -> {
			book = bookDetailsModel;
			getView().initializeBookView(bookDetailsModel);
		}, error -> getView().showInitializationError()));
	}

	private void updateReadingState(ReadingStateModel.ReadingState state) {
		if (preferences.isUserLoggedIn()) {
			addDisposable(booksService.setReadingState(bookSlug, state.getStateName())
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(updatedState -> book.setState(updatedState.getState()), error -> Log.e(TAG, "Failed to update reading state.", error)));
		}
	}

	private void updateFavouriteState(boolean state) {
		addDisposable(booksService.setFavouriteState(bookSlug, book.getFavouriteString(state))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(updatedState -> {
					book.setFavouriteState(state);
					EventBus.getDefault().post(new BookFavouriteEvent(state));
				}, error -> Log.e(TAG, "Failed to update favourite state.", error)));
	}

	private void launchFolioReader() {
		if (book.getState() == ReadingStateModel.ReadingState.STATE_NOT_STARTED) {
			updateReadingState(ReadingStateModel.ReadingState.STATE_READING);
		}
		String downloadedBookUrl = FileCacheUtils.getCachedFileForUrl(book.getEpub());
		if (downloadedBookUrl != null) {
			getView().openBook(downloadedBookUrl);
		}
	}

	private void launchAudioPlayer() {
		if (book.getState() == ReadingStateModel.ReadingState.STATE_NOT_STARTED) {
			updateReadingState(ReadingStateModel.ReadingState.STATE_READING);
		}
		getView().launchPlayer(book);
	}

	private void storeDownloadedBook(boolean forAudiobook) {
		BookModel stored = storedBook == null ? book.getStorageModel(bookSlug) : storedBook;
		if (forAudiobook) {
		    List<String> mediaUrls = book.getAudiobookFilesUrls();
			stored.setAudioFileUrls(mediaUrls);
			stored.setTotalAudioChapters(mediaUrls.size());
		} else {
			stored.setEbookFileUrl(book.getEpub());
		}
		if (storedBook == null) {
			storedBook = stored;
			storage.add(storedBook);
		} else {
			storage.update(stored);
		}
	}

	public void showFavouriteButton(BookDetailsModel book) {
		if(preferences.isUserLoggedIn()) {
			getView().showFavouriteButton(book);
		}
	}
}
