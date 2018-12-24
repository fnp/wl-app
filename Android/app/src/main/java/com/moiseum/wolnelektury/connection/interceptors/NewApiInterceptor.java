package com.moiseum.wolnelektury.connection.interceptors;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Piotr Ostrowski on 24.09.2018.
 */
public class NewApiInterceptor implements Interceptor {

	private static final String NEW_API_HEADER = "New-Api";
	private static final String NEW_API_PARAM = "new_api";

	@Override
	public Response intercept(@NonNull Chain chain) throws IOException {
		if (chain.request().header(NEW_API_HEADER) != null) {
			HttpUrl httpUrl = chain.request()
					.url()
					.newBuilder()
					.addQueryParameter(NEW_API_PARAM, Boolean.toString(true))
					.build();
			Request newRequest = chain.request()
					.newBuilder()
					.url(httpUrl)
					.build();
			return chain.proceed(newRequest);
		}

		return chain.proceed(chain.request());
	}
}
