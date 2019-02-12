package com.moiseum.wolnelektury.view.settings;

import android.os.Bundle;

import com.folioreader.util.AppUtil;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;
import com.moiseum.wolnelektury.connection.downloads.FileCacheUtils;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.events.LoggedInEvent;
import com.moiseum.wolnelektury.storage.BookStorage;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class SettingsPresenter extends FragmentPresenter<SettingsView> {

	private SharedPreferencesUtils preferences = WLApplication.getInstance().getPreferences();
	private BookStorage storage = WLApplication.getInstance().getBookStorage();

	SettingsPresenter(SettingsView view) {
		super(view);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
		getView().initializeSettings(preferences.getNotifications(), true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onLoggedIn(LoggedInEvent event) {
		getView().initializeSettings(preferences.getNotifications(), true);
	}

	public void onNotificationsChanged(boolean checked) {
		preferences.setNotifications(checked);
	}

	public void onDeleteAllClicked() {
		getView().showProgressDialog(true);
		addDisposable(deleteAllFiles()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(() -> {
					getView().showProgressDialog(false);
					getView().showDeletionCompleted();
				}, error -> {
					getView().showProgressDialog(false);
					getView().showDeletionFailed(error);
				})
		);
	}

	private Completable deleteAllFiles() {
		return Completable.fromAction(() -> {
			List<BookModel> storedBooks = storage.all();
			for (BookModel book : storedBooks) {
				if (book.getEbookFileUrl() != null) {
					AppUtil.removeBookState(WLApplication.getInstance().getApplicationContext(), book.getEbookName());
					FileCacheUtils.deleteEbookFile(book.getEbookFileUrl());
				}
				if (book.getAudioFileUrls() != null) {
					FileCacheUtils.deleteAudiobookFiles(book.getAudioFileUrls());
				}
			}
			storage.removeAll();
		});
	}
}
