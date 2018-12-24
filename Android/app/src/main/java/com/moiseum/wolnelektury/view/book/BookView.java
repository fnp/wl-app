package com.moiseum.wolnelektury.view.book;

import android.content.Context;

import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.view.book.components.ProgressDownloadButton;

/**
 * Created by Piotr Ostrowski on 17.11.2017.
 */

public interface BookView {

	void initializeBookView(BookDetailsModel book);

	void changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState state, boolean forAudiobook);

	void showCurrentStateProgress(int percentage, boolean forAudiobook);

	void showInitializationError();

	void showDownloadFileError();

	void startShareActivity(String shareUrl);

	Context getContext();

	void openBook(String downloadedBookUrl);

	void launchPlayer(BookDetailsModel book);

	void updateReadingProgress(int currentChapter, int count, boolean forAudiobook);

	void startLikeClicked();

	void stopLikeClicked();

	void showFavouriteButton(BookDetailsModel book);

	void showPremiumLock(boolean lock);
}
