package com.moiseum.wolnelektury.view.book.list;

import com.moiseum.wolnelektury.base.DataProvider;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.models.ReadingStateModel;
import com.moiseum.wolnelektury.connection.services.BooksService;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Piotr Ostrowski on 24.06.2018.
 */
public class ReadingStateDataProvider extends DataProvider<List<BookModel>, BooksService> {

	private ReadingStateModel.ReadingState state;

	public ReadingStateDataProvider(ReadingStateModel.ReadingState state) {
		this.state = state;
	}

	@Override
	protected Class<BooksService> getServiceClass() {
		return BooksService.class;
	}

	@Override
	public Call<List<BookModel>> execute(BooksService service) {
		return service.getReadenBooks(state.getStateName(), lastKeySlug, RestClient.PAGINATION_LIMIT);
	}
}
