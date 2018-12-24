package com.moiseum.wolnelektury.connection.interceptors;

/**
 * Created by Piotr Ostrowski on 06.06.2018.
 */
/*
 * Copyright (C) 2015 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.moiseum.wolnelektury.connection.models.OAuthTokenModel;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.ByteString;

public final class OAuthSigningInterceptor implements Interceptor {
	private static final String TAG = OAuthSigningInterceptor.class.getSimpleName();
	private static final String REQUEST_TOKEN_HEADER = "Token-Requested";
	private static final String AUTH_REQUIRED_HEADER = "Authentication-Required";
	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final String OAUTH_REALM = "realm=\"API\", ";
	private static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
	private static final String OAUTH_NONCE = "oauth_nonce";
	private static final String OAUTH_SIGNATURE = "oauth_signature";
	private static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
	private static final String OAUTH_SIGNATURE_METHOD_VALUE = "HMAC-SHA1";
	private static final String OAUTH_TIMESTAMP = "oauth_timestamp";
	private static final String OAUTH_ACCESS_TOKEN = "oauth_token";
	private static final String OAUTH_VERSION = "oauth_version";
	private static final String OAUTH_VERSION_VALUE = "1.0";
	private static final long ONE_SECOND = 1000;

	private final String consumerKey;
	private final String consumerSecret;
	private final Random random;
	private String accessToken;
	private String accessSecret;

	public OAuthSigningInterceptor(String consumerKey, String consumerSecret, Random random) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.random = random;
	}

	public void setToken(String accessToken, String accessSecret) {
		this.accessToken = accessToken;
		this.accessSecret = accessSecret;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		if (chain.request().header(REQUEST_TOKEN_HEADER) != null) {
			return handleRequestTokenRequest(chain);
		} else if (chain.request().header(AUTH_REQUIRED_HEADER) != null || isSignedIn()) {
			return chain.proceed(signRequest(chain.request()));
		} else {
			return chain.proceed(chain.request());
		}
	}

	private boolean isSignedIn() {
		return accessSecret != null && accessToken != null;
	}

	private Response handleRequestTokenRequest(Chain chain) throws IOException {
		Response tokenResponse = chain.proceed(requestTokenRequest(chain.request()));
		if (tokenResponse.isSuccessful() && tokenResponse.code() == 200 && tokenResponse.body() != null) {
			String jsonResponse = paramJson(tokenResponse.body().string());
			try {
				Gson gson = new Gson();
				OAuthTokenModel tokenModel = gson.fromJson(jsonResponse, OAuthTokenModel.class);
				accessToken = tokenModel.getToken();
				accessSecret = tokenModel.getTokenSecret();
				return tokenResponse.newBuilder().body(ResponseBody.create(MediaType.parse("application/json"), jsonResponse)).build();
			} catch (JsonSyntaxException e) {
				Log.v(TAG, "Failed to parse Oauth Request Token response.", e);
			}
		}
		return tokenResponse;
	}

	private Request signRequest(Request request) throws IOException {
		if (accessToken == null || accessSecret == null) {
			Log.e(TAG, "Missing authentication tokens, passing request unsigned.");
			return request;
		}

		SortedMap<String, String> parameters = getOAuthParams(request.url(), request.body());

		String baseUrl = request.url().newBuilder().query(null).build().toString();
		ByteString baseString = getBaseString(request.method(), baseUrl, parameters);
		String signingKey = utf8(consumerSecret) + "&" + (accessSecret != null ? utf8(accessSecret) : "");
		String signature = baseString.hmacSha1(ByteString.of(signingKey.getBytes())).base64();

		String authorization = "OAuth " + OAUTH_REALM
				+ OAUTH_CONSUMER_KEY + "=\"" + parameters.get(OAUTH_CONSUMER_KEY) + "\", "
				+ OAUTH_NONCE + "=\"" + parameters.get(OAUTH_NONCE) + "\", "
				+ OAUTH_SIGNATURE + "=\"" + signature + "\", "
				+ OAUTH_SIGNATURE_METHOD + "=\"" + OAUTH_SIGNATURE_METHOD_VALUE + "\", "
				+ OAUTH_TIMESTAMP + "=\"" + parameters.get(OAUTH_TIMESTAMP) + "\", "
				+ OAUTH_ACCESS_TOKEN + "=\"" + accessToken + "\", "
				+ OAUTH_VERSION + "=\"" + OAUTH_VERSION_VALUE + "\"";

		return request.newBuilder()
				.addHeader(AUTHORIZATION_HEADER, authorization)
				.build();
	}

	private Request requestTokenRequest(Request request) throws IOException {
		SortedMap<String, String> parameters = getOAuthParams(request.url(), request.body());

		String baseUrl = request.url().newBuilder().query(null).build().toString();
		ByteString baseString = getBaseString(request.method(), baseUrl, parameters);
		String signingKey = utf8(consumerSecret) + "&" + (accessSecret != null ? utf8(accessSecret) : "");
		String signature = baseString.hmacSha1(ByteString.of(signingKey.getBytes())).base64();

		HttpUrl.Builder urlBuilder = request.url().newBuilder()
				.addQueryParameter(OAUTH_CONSUMER_KEY, parameters.get(OAUTH_CONSUMER_KEY))
				.addQueryParameter(OAUTH_NONCE, parameters.get(OAUTH_NONCE))
				.addQueryParameter(OAUTH_SIGNATURE_METHOD, OAUTH_SIGNATURE_METHOD_VALUE)
				.addQueryParameter(OAUTH_TIMESTAMP, parameters.get(OAUTH_TIMESTAMP))
				.addQueryParameter(OAUTH_VERSION, OAUTH_VERSION_VALUE)
				.addQueryParameter(OAUTH_SIGNATURE, signature);
		if (accessToken != null) {
			urlBuilder.addQueryParameter(OAUTH_ACCESS_TOKEN, accessToken);
		}
		HttpUrl requestUrl = urlBuilder.build();

		return request.newBuilder().url(requestUrl).build();
	}

	private SortedMap<String, String> getOAuthParams(HttpUrl url, RequestBody requestBody) throws IOException {
		byte[] nonce = new byte[32];
		random.nextBytes(nonce);

		String oauthNonce = ByteString.of(nonce).base64().replaceAll("\\W", "");
		String oauthTimestamp = String.valueOf(System.currentTimeMillis() / ONE_SECOND);

		SortedMap<String, String> parameters = new TreeMap<>();
		parameters.put(OAUTH_CONSUMER_KEY, utf8(consumerKey));
		parameters.put(OAUTH_NONCE, oauthNonce);
		parameters.put(OAUTH_SIGNATURE_METHOD, OAUTH_SIGNATURE_METHOD_VALUE);
		parameters.put(OAUTH_TIMESTAMP, oauthTimestamp);
		parameters.put(OAUTH_VERSION, OAUTH_VERSION_VALUE);
		if (accessToken != null) {
			parameters.put(OAUTH_ACCESS_TOKEN, accessToken);
		}

		// Adding query params
		for (int i = 0; i < url.querySize(); i++) {
			parameters.put(utf8(url.queryParameterName(i)), utf8(url.queryParameterValue(i)));
		}

		// Adding body params
		if (requestBody != null) {
			Buffer body = new Buffer();
			requestBody.writeTo(body);

			while (!body.exhausted()) {
				long keyEnd = body.indexOf((byte) '=');
				if (keyEnd == -1) {
					throw new IllegalStateException("Key with no value: " + body.readUtf8());
				}
				String key = body.readUtf8(keyEnd);
				body.skip(1); // Equals.

				long valueEnd = body.indexOf((byte) '&');
				String value = valueEnd == -1 ? body.readUtf8() : body.readUtf8(valueEnd);
				if (valueEnd != -1) {
					body.skip(1); // Ampersand.
				}

				parameters.put(key, value);
			}
		}

		return parameters;
	}

	@NonNull
	private ByteString getBaseString(String method, String baseUrl, SortedMap<String, String> parameters) throws IOException {
		Buffer base = new Buffer();
		base.writeUtf8(method);
		base.writeByte('&');
		base.writeUtf8(utf8(baseUrl));
		base.writeByte('&');

		boolean first = true;
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			if (!first) {
				base.writeUtf8(utf8("&"));
			}
			first = false;
			base.writeUtf8(utf8(entry.getKey()));
			base.writeUtf8(utf8("="));
			base.writeUtf8(utf8(entry.getValue()));
		}
		return ByteString.of(base.readByteArray());
	}

	private String utf8(String escapedString) throws IOException {
		return URLEncoder.encode(escapedString, "UTF-8")
				.replace("+", "%20")
				.replace("*", "%2A")
				.replace("%7E", "~");
	}

	private String paramJson(String paramIn) {
		paramIn = paramIn.replaceAll("=", "\":\"");
		paramIn = paramIn.replaceAll("&", "\",\"");
		return "{\"" + paramIn + "\"}";
	}

}
