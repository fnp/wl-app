package com.moiseum.wolnelektury.view.book.list;

import com.moiseum.wolnelektury.base.DataProvider;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.services.BooksService;

import java.util.List;

import retrofit2.Call;


public class AudiobooksDataProvider extends DataProvider<List<BookModel>, BooksService> {

    @Override
    public Call<List<BookModel>> execute(BooksService service) {
        return service.getAudiobooks(lastKeySlug , RestClient.PAGINATION_LIMIT);
    }

    @Override
    protected Class<BooksService> getServiceClass() {
        return BooksService.class;
    }
}
