package com.moiseum.wolnelektury.connection;

import java.io.IOException;

import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

/**
 * @author golonkos.
 */

public class ErrorHandler<T> {

	private static final String TAG = ErrorHandler.class.getSimpleName();
	private final Response<T> response;

	public ErrorHandler(Response<T> response) {
		this.response = response;
	}

	public void handle() throws IOException {
		// There is no error model returned for this API
		switch (response.code()) {
			case HTTP_BAD_REQUEST:
			case HTTP_NOT_FOUND:
			case HTTP_BAD_METHOD:
			case HTTP_INTERNAL_ERROR:
			case HTTP_FORBIDDEN:
			default:
				throw new IOException("Unknown or unhandled exception for response " + response.code() + ", " + response.message());
		}

	}

	//	public ErrorModel parseError(Response<T> response) {
	//		try {
	//			Gson gson = new GsonBuilder().create();
	//			return gson.fromJson(response.errorBody().string(), ErrorModel.class);
	//		} catch (IOException | JsonSyntaxException e) {
	//			Log.e(TAG, "Error while parsing error json", e);
	//			return null;
	//		}
	//	}

	public int getResponseCode() {
		return response.code();
	}
}
