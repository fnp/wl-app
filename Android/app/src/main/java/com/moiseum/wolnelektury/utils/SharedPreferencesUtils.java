package com.moiseum.wolnelektury.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.moiseum.wolnelektury.connection.models.OAuthTokenModel;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

/**
 * Created by Piotr Ostrowski on 14.06.2018.
 */
public final class SharedPreferencesUtils {

	private static final String PREFERENCES_FILENAME = "WolneLekturyPreferences";
	private static final String ACCESS_TOKEN_KEY = "AccessToken";
	private static final String ACCESS_TOKEN_SECRET_KEY = "AccessTokenSecret";
	private static final String USERNAME_KEY = "Username";
	private static final String PREMIUM_KEY = "Premium";
	private static final String NOTIFICATIONS_KEY = "Notifications";
	private static final String TEMPORARY_LOGIN_TOKEN_KEY = "TemporaryLoginTokenKey";

	private OAuthTokenModel currentToken;
	private String username;
	private Boolean isPremium;
	private Boolean notifications;
	private String temporaryLoginToken;
	private SharedPreferences preferences;

	public SharedPreferencesUtils(Context context) {
		this.preferences = context.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE);
	}

	public void storeAccessToken(OAuthTokenModel tokenModel) {
		currentToken = tokenModel;
		SecurePreferences.setValue(ACCESS_TOKEN_KEY, tokenModel.getToken());
		SecurePreferences.setValue(ACCESS_TOKEN_SECRET_KEY, tokenModel.getTokenSecret());
	}

	public OAuthTokenModel getAccessToken() {
		if (currentToken != null) {
			return currentToken;
		}

		String token = SecurePreferences.getStringValue(ACCESS_TOKEN_KEY, null);
		String tokenSecret = SecurePreferences.getStringValue(ACCESS_TOKEN_SECRET_KEY, null);

		if (token == null || tokenSecret == null) {
			return null;
		}
		currentToken = new OAuthTokenModel();
		currentToken.setToken(token);
		currentToken.setTokenSecret(tokenSecret);
		return currentToken;
	}

	public String getUsername() {
		if (username == null) {
			username = preferences.getString(USERNAME_KEY, null);
		}
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		preferences.edit().putString(USERNAME_KEY, username).apply();
	}

	public boolean isUserLoggedIn() {
		return getAccessToken() != null;
	}

	public boolean isUserPremium() {
		if (isPremium == null) {
			isPremium = preferences.getBoolean(PREMIUM_KEY, false);
		}
		return isPremium && isUserLoggedIn();
	}

	public void setPremium(boolean isPremium) {
		this.isPremium = isPremium;
		preferences.edit().putBoolean(PREMIUM_KEY, isPremium).apply();
	}

	public boolean getNotifications() {
		if (notifications == null) {
			notifications = preferences.getBoolean(NOTIFICATIONS_KEY, true);
		}
		return notifications;
	}

	public void setNotifications(Boolean notifications) {
		this.notifications = notifications;
		preferences.edit().putBoolean(NOTIFICATIONS_KEY, notifications).apply();
	}

	public String getTemporaryLoginToken() {
		if (temporaryLoginToken == null) {
			temporaryLoginToken = preferences.getString(TEMPORARY_LOGIN_TOKEN_KEY, null);
		}
		return temporaryLoginToken;
	}

	public void setTemporaryLoginToken(String temporaryLoginToken) {
		this.temporaryLoginToken = temporaryLoginToken;
		preferences.edit().putString(TEMPORARY_LOGIN_TOKEN_KEY, temporaryLoginToken).apply();
	}

	public void clearUserData() {
		currentToken = null;
		username = null;
		isPremium = null;
		notifications = null;
		temporaryLoginToken = null;
		SecurePreferences.clearAllValues();
		preferences.edit()
				.remove(USERNAME_KEY)
				.remove(PREMIUM_KEY)
				.remove(NOTIFICATIONS_KEY)
				.remove(TEMPORARY_LOGIN_TOKEN_KEY)
				.apply();
	}
}
