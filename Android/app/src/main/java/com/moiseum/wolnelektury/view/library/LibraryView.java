package com.moiseum.wolnelektury.view.library;

import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.view.book.BookType;

import java.util.List;

/**
 * @author golonkos
 */

interface LibraryView {
	void setNewest(List<BookModel> books);

	void setNewestProgressVisible(boolean visible);

	void showNewestError(Exception e);

	void setRecommended(List<BookModel> books);

	void setRecommendedProgressVisible(boolean visible);

	void showRecommendedError(Exception e);

	void setNowReadingVisibility(boolean visible);

	void setNowReading(List<BookModel> books);

	void setNowReadingProgressVisible(boolean visible);

	void showNowReadingError(Exception e);

	void openBookDetailsView(String slug, BookType bookType);

	void initHeader(BookDetailsModel item);

	void setProgressContainerVisible(boolean visible);

	void showHeaderError();

	void showHeaderEmpty(boolean userLoggedIn);

	void setHeaderProgressVisible(boolean visible);

	void showBecomeAFriendHeader(boolean premium);
}
