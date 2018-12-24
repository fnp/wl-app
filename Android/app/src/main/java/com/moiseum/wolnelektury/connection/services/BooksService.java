package com.moiseum.wolnelektury.connection.services;

import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.models.FavouriteStateModel;
import com.moiseum.wolnelektury.connection.models.ReadingStateModel;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Piotr Ostrowski on 16.11.2017.
 */

public interface BooksService {

	@Headers("New-Api: true")
	@GET("filter-books/")
	Call<List<BookModel>> getSearchBooks(@Query("search") String search, @Query("epochs") String epochs, @Query("genres") String genres, @Query("kinds") String kinds,
	                                     @Query("audiobook") Boolean audiobook, @Query("lektura") Boolean lecture, @Query("after") String lastKey, @Query("count") int count);

	@GET("books/{slug}")
	Single<BookDetailsModel> getBookDetails(@Path("slug") String slug);

	@Streaming
	@GET
	Call<ResponseBody> downloadFileWithUrl(@Url String fileUrl);

	@Headers("New-Api: true")
	@GET("newest/")
	Call<List<BookModel>> getNewest();

	@Headers("New-Api: true")
	@GET("recommended/")
	Call<List<BookModel>> getRecommended();

	@Headers("New-Api: true")
	@GET("audiobooks/")
	Call<List<BookModel>> getAudiobooks(@Query("after") String lastKey, @Query("count") int count);

	@Headers("Authentication-Required: true")
	@POST("reading/{slug}/{state}/")
	Single<ReadingStateModel> setReadingState(@Path("slug") String slug, @Path("state") String state);

	@Headers("Authentication-Required: true")
	@GET("reading/{slug}/")
	Single<ReadingStateModel> getReadingState(@Path("slug") String slug);

	@Headers({"Authentication-Required: true", "New-Api: true"})
	@GET("shelf/{state}/")
	Call<List<BookModel>> getReadenBooks(@Path("state") String state, @Query("after") String lastKey, @Query("count") int count);

	@Headers("Authentication-Required: true")
	@POST("like/{slug}/")
	Single<FavouriteStateModel> setFavouriteState(@Path("slug") String slug, @Query("action") String action);

	@Headers("Authentication-Required: true")
	@GET("like/{slug}/")
	Single<FavouriteStateModel> getFavouriteState(@Path("slug") String slug);

	@GET("preview/")
	Call<List<BookDetailsModel>> getPreview();

	@GET("books/{slug}")
	Call<BookDetailsModel> getPreviewMockup(@Path("slug") String slug);

	@Headers({"Authentication-Required: true", "New-Api: true"})
	@GET("shelf/likes/")
	Call<List<BookModel>> getFavourites(@Query("after") String lastKey, @Query("count") int count);

}
