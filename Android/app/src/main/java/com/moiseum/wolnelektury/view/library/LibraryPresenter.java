package com.moiseum.wolnelektury.view.library;

import android.os.Bundle;

import com.moiseum.wolnelektury.base.DataObserver;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.RestClientCallback;
import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.models.ReadingStateModel;
import com.moiseum.wolnelektury.connection.services.BooksService;
import com.moiseum.wolnelektury.events.LoggedInEvent;
import com.moiseum.wolnelektury.storage.BookStorage;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;
import com.moiseum.wolnelektury.view.book.BookType;
import com.moiseum.wolnelektury.view.book.list.AudiobooksDataProvider;
import com.moiseum.wolnelektury.view.book.list.FavouritesDataProvider;
import com.moiseum.wolnelektury.view.book.list.NewestBooksDataProvider;
import com.moiseum.wolnelektury.view.book.list.ReadingStateDataProvider;
import com.moiseum.wolnelektury.view.book.list.RecommendedBooksDataProvider;
import com.moiseum.wolnelektury.view.main.MainActivity;
import com.moiseum.wolnelektury.view.main.NavigationElement;
import com.moiseum.wolnelektury.view.main.events.PremiumStatusEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import retrofit2.Call;

/**
 * @author golonkos
 */
class LibraryPresenter extends FragmentPresenter<LibraryView> {

	private static final String TAG = LibraryPresenter.class.getSimpleName();
	private final NewestBooksDataProvider newestBooksDataProvider;
	private final RecommendedBooksDataProvider recommendedBooksDataProvider;
	private final ReadingStateDataProvider nowReadingBooksDataProvider;
	private BookDetailsModel premiereBook;
	private Call currentCall;
	private RestClient client = WLApplication.getInstance().getRestClient();

	private SharedPreferencesUtils preferences = WLApplication.getInstance().getPreferences();

	LibraryPresenter(LibraryView view) {
		super(view);
		newestBooksDataProvider = new NewestBooksDataProvider();
		newestBooksDataProvider.setDataObserver(new NewestDataObserver());

		recommendedBooksDataProvider = new RecommendedBooksDataProvider();
		recommendedBooksDataProvider.setDataObserver(new RecommendedDataObserver());

		nowReadingBooksDataProvider = new ReadingStateDataProvider(ReadingStateModel.ReadingState.STATE_READING);
		nowReadingBooksDataProvider.setDataObserver(new NowReadingDataObserver());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
		newestBooksDataProvider.load(null);
		recommendedBooksDataProvider.load(null);
		if (preferences.isUserLoggedIn()) {
			getView().setNowReadingVisibility(true);
			nowReadingBooksDataProvider.load(null);
		}
		fetchHeader();
	}

	@Override
	public void onResume() {
		super.onResume();
		getView().showBecomeAFriendHeader(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		newestBooksDataProvider.cancel();
		recommendedBooksDataProvider.cancel();
		nowReadingBooksDataProvider.cancel();
		if (currentCall != null) {
			currentCall.cancel();
		}
		EventBus.getDefault().unregister(this);
	}

	void onNowReadingSeeAllClicked() {
		EventBus.getDefault().post(new MainActivity.ChangeNavigationEvent(NavigationElement.NOW_READING));
	}

	void onBookClicked(BookModel book) {
		getView().openBookDetailsView(book.getSlug(), BookType.TYPE_DEFAULT);
	}

	void reloadNewest() {
		newestBooksDataProvider.load(null);
	}

	void reloadRecommended() {
		recommendedBooksDataProvider.load(null);
	}

	void reloadNowReading() {
		nowReadingBooksDataProvider.load(null);
	}

	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onBookAdded(BookStorage.BookAddedEvent event) {
		if (preferences.isUserLoggedIn()) {
			nowReadingBooksDataProvider.load(null);
		}
	}

	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onBookDeleted(BookStorage.BookDeletedEvent event) {
		if (preferences.isUserLoggedIn()) {
			nowReadingBooksDataProvider.load(null);
		}
	}

	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onPremiumChanged(PremiumStatusEvent event) {
		getView().showBecomeAFriendHeader(event.isPremium());
	}

	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onLoggedIn(LoggedInEvent event) {
		getView().showBecomeAFriendHeader(true);
		getView().setNowReadingVisibility(true);
		nowReadingBooksDataProvider.load(null);
	}

	void onPremiereHeaderClicked() {
		if (premiereBook != null) {
			getView().openBookDetailsView(premiereBook.getSlug(), BookType.TYPE_PREMIUM);
		}
	}

	void fetchHeader() {
		getView().setProgressContainerVisible(true);
		getView().setHeaderProgressVisible(false);
		getView().showHeaderEmpty(true);
		//		currentCall = client.call(new RestClientCallback<List<BookDetailsModel>, BooksService>() {
		//			@Override
		//			public void onSuccess(List<BookDetailsModel> data) {
		//				getView().setHeaderProgressVisible(false);
		//				if (data.size() > 0) {
		//					premiereBook = data.get(0);
		//					getView().initHeader(premiereBook);
		//					getView().setProgressContainerVisible(false);
		//				} else {
		//					getView().showHeaderEmpty(preferences.isUserLoggedIn());
		//				}
		//			}
		//
		//			@Override
		//			public void onFailure(Exception e) {
		//				getView().showHeaderError();
		//			}
		//
		//			@Override
		//			public void onCancel() {
		//				// nop.
		//			}
		//
		//			@Override
		//			public Call<List<BookDetailsModel>> execute(BooksService service) {
		//				return service.getPreview();
		//			}
		//		}, BooksService.class);
	}


	private class NewestDataObserver implements DataObserver<List<BookModel>> {

		@Override
		public void onLoadStarted() {
			getView().setNewestProgressVisible(true);
		}

		@Override
		public void onLoadSuccess(List<BookModel> data) {
			getView().setNewestProgressVisible(false);
			getView().setNewest(data);
		}

		@Override
		public void onLoadFailed(Exception e) {
			getView().setNewestProgressVisible(false);
			getView().showNewestError(e);
		}
	}

	private class RecommendedDataObserver implements DataObserver<List<BookModel>> {

		@Override
		public void onLoadStarted() {
			getView().setRecommendedProgressVisible(true);
		}

		@Override
		public void onLoadSuccess(List<BookModel> data) {
			getView().setRecommendedProgressVisible(false);
			getView().setRecommended(data);
		}

		@Override
		public void onLoadFailed(Exception e) {
			getView().setRecommendedProgressVisible(false);
			getView().showRecommendedError(e);
		}
	}

	private class NowReadingDataObserver implements DataObserver<List<BookModel>> {

		@Override
		public void onLoadStarted() {
			getView().setNowReadingProgressVisible(true);
		}

		@Override
		public void onLoadSuccess(List<BookModel> data) {
			getView().setNowReadingProgressVisible(false);
			getView().setNowReading(data);
		}

		@Override
		public void onLoadFailed(Exception e) {
			getView().setNowReadingProgressVisible(false);
			getView().showNowReadingError(e);
		}
	}
}
