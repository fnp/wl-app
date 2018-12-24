package com.moiseum.wolnelektury.view.book.list;

import com.moiseum.wolnelektury.storage.BookStorage;
import com.moiseum.wolnelektury.base.DataProvider;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.services.BooksService;

import java.util.List;

import retrofit2.Call;

/**
 * @author golonkos
 */
public class DownloadedBooksDataProvider extends DataProvider<List<BookModel>, BooksService> {

	@Override
	public void load(String lastKey) {
		if (dataObserver != null && lastKey == null) {
			BookStorage bookStorage = WLApplication.getInstance().getBookStorage();
			dataObserver.onLoadSuccess(bookStorage.all());
		}
	}

	@Override
	public Call<List<BookModel>> execute(BooksService service) {
		return null;
	}

	@Override
	protected Class<BooksService> getServiceClass() {
		return null;
	}
}
