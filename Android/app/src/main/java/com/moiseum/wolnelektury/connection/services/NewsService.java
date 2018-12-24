package com.moiseum.wolnelektury.connection.services;

import com.moiseum.wolnelektury.connection.models.NewsModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsService {

	@GET("blog")
	Call<List<NewsModel>> getNews(@Query("after") String lastKey, @Query("count") int count);
}
