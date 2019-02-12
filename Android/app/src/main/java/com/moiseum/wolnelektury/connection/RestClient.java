package com.moiseum.wolnelektury.connection;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moiseum.wolnelektury.BuildConfig;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.connection.interceptors.NewApiInterceptor;
import com.moiseum.wolnelektury.connection.interceptors.OAuthSigningInterceptor;
import com.moiseum.wolnelektury.connection.interceptors.UnauthorizedInterceptor;
import com.moiseum.wolnelektury.connection.models.OAuthTokenModel;
import com.moiseum.wolnelektury.connection.services.BooksService;

import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author golonkos.
 */

public class RestClient {
	public static final int PAGINATION_LIMIT = 30;

	public static final String MEDIA_URL = "http://wolnelektury.pl/media/";
	public static final String MEDIA_URL_HTTPS = "https://wolnelektury.pl/media/";
	public static final String BASE_URL = "https://wolnelektury.pl/api/";
	public static final String WEB_OAUTH_AUTHORIZATION_URL = "";
	public static final String WEB_PAYPAL_FORM_URL = "";

	private static final String CACHE_DIR = "responses";
	private static final int CACHE_SIZE_MB = 10;

	private static final String CONSUMER_KEY = "";
	private static final String CONSUMER_SECRET = "";

	private final Retrofit retrofit;
	private final OAuthSigningInterceptor oAuthInterceptor;

	public RestClient(Context context) {
		OAuthTokenModel currentToken = WLApplication.getInstance().getPreferences().getAccessToken();
		oAuthInterceptor = new OAuthSigningInterceptor(CONSUMER_KEY, CONSUMER_SECRET, new Random());
		if (currentToken != null) {
			oAuthInterceptor.setToken(currentToken.getToken(), currentToken.getTokenSecret());
		}
//		UnauthorizedInterceptor unauthorizedInterceptor = new UnauthorizedInterceptor();
		NewApiInterceptor newApiInterceptor = new NewApiInterceptor();

		GsonBuilder gsonBuilder = new GsonBuilder();
		//gsonBuilder.registerTypeAdapter(Date.class, new RestClientDateSerializer());

		Gson gson = gsonBuilder.create();

		int cacheSize = CACHE_SIZE_MB * 1024 * 1024;
		File cacheDir = new File(context.getCacheDir(), CACHE_DIR);
		Cache cache = new Cache(cacheDir, cacheSize);

		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.interceptors().add(newApiInterceptor);
		builder.interceptors().add(oAuthInterceptor);
//		builder.interceptors().add(unauthorizedInterceptor);
		if (BuildConfig.DEBUG) {
			HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
			loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
			builder.interceptors().add(loggingInterceptor);
		}
		builder.writeTimeout(60, TimeUnit.SECONDS);
		builder.readTimeout(60, TimeUnit.SECONDS);
		builder.connectTimeout(60, TimeUnit.SECONDS);

		OkHttpClient client = builder.cache(cache).build();
		retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.client(client)
				.build();
	}

	public <T, S> Call<T> call(RestClientCallback<T, S> restClientCallback, Class<S> clazz) {
		S service = createService(clazz);
		Call<T> call = restClientCallback.execute(service);
		call.enqueue(restClientCallback);
		return call;
	}

	public <S> S createService(Class<S> clazz) {
		return retrofit.create(clazz);
	}

	public void clearOAuthTokens() {
		oAuthInterceptor.setToken(null, null);
	}

	public BooksService obtainBookService() {
		return retrofit.create(BooksService.class);
	}
}