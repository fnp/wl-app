package com.moiseum.wolnelektury.connection.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Piotr Ostrowski on 11.06.2018.
 */
@Parcel(Parcel.Serialization.BEAN)
public class OAuthTokenModel {

	@SerializedName("oauth_token_secret")
	private String tokenSecret;
	@SerializedName("oauth_token")
	private String token;

	public OAuthTokenModel() {
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
