package com.moiseum.wolnelektury.view.book.list;

import com.moiseum.wolnelektury.base.mvp.LoadingView;
import com.moiseum.wolnelektury.connection.models.BookModel;

import java.util.List;

/**
 * @author golonkos
 */
interface BooksListView extends LoadingView<List<BookModel>> {
	void openBookDetailsView(String bookSlug);

	void updateEmptyViewVisibility();

	void updateFavouriteState(boolean state, Integer clickedPosition);

	void clearList();
}
