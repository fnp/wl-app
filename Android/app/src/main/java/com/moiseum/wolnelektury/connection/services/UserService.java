package com.moiseum.wolnelektury.connection.services;

import com.moiseum.wolnelektury.connection.models.OAuthTokenModel;
import com.moiseum.wolnelektury.connection.models.UserModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by Piotr Ostrowski on 06.06.2018.
 */
public interface UserService {

	@Headers("Token-Requested: true")
	@GET("oauth/request_token/")
	Call<OAuthTokenModel> requestToken();

	@Headers("Token-Requested: true")
	@GET("oauth/access_token/")
	Call<OAuthTokenModel> accessToken();

	@Headers("Authentication-Required: true")
	@GET("username/")
	Call<UserModel> getUser();
}
