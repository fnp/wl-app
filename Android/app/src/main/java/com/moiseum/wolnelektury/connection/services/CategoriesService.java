package com.moiseum.wolnelektury.connection.services;

import com.moiseum.wolnelektury.connection.models.CategoryModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by piotrostrowski on 25.11.2017.
 */

public interface CategoriesService {

	@GET("epochs")
	Call<List<CategoryModel>> getEpochs(@Query("book_only") boolean bookOnly);

	@GET("genres")
	Call<List<CategoryModel>> getGenres(@Query("book_only") boolean bookOnly);

	@GET("kinds")
	Call<List<CategoryModel>> getKinds(@Query("book_only") boolean bookOnly);
}
