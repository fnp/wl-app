package com.moiseum.wolnelektury.view.main;

import android.net.Uri;
import android.util.Log;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.base.mvp.Presenter;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.RestClientCallback;
import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.connection.models.OAuthTokenModel;
import com.moiseum.wolnelektury.connection.models.UserModel;
import com.moiseum.wolnelektury.connection.services.BooksService;
import com.moiseum.wolnelektury.connection.services.UserService;
import com.moiseum.wolnelektury.events.LoggedInEvent;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;
import com.moiseum.wolnelektury.view.main.events.PremiumStatusEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Piotr Ostrowski on 12.06.2018.
 */
class MainPresenter extends Presenter<MainView> {

	private static final String TAG = MainPresenter.class.getSimpleName();
	private static final String OAUTH_CALLBACK_VALUE = "wolnelekturyapp://oauth.callback/?oauth_token=";
	private static final String PAYPAL_SUCCESS_CALLBACK_VALUE = "wolnelekturyapp://paypal_return";
	private static final String PAYPAL_ERROR_CALLBACK_VALUE = "wolnelekturyapp://paypal_error";

	private RestClient client = WLApplication.getInstance().getRestClient();
	private SharedPreferencesUtils preferences = WLApplication.getInstance().getPreferences();
	private Call currentCall;
	private Call checkCall;

	MainPresenter(MainView view) {
		super(view);
		getView().setLoggedIn(preferences.isUserLoggedIn());
		getView().setLoggedUsername(preferences.getUsername());
	}

	@Override
	public void onResume() {
		super.onResume();
		if (preferences.isUserLoggedIn()) {
			checkPremiumStatus();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (currentCall != null) {
			currentCall.cancel();
		}
		if (checkCall != null) {
			checkCall.cancel();
		}
	}

	void onLoginClicked() {
		getView().setProgressDialogVisibility(true);
		currentCall = client.call(new RestClientCallback<OAuthTokenModel, UserService>() {

			@Override
			public void onSuccess(OAuthTokenModel data) {
				preferences.setTemporaryLoginToken(data.getToken());
				String authUrl = String.format(RestClient.WEB_OAUTH_AUTHORIZATION_URL, data.getToken());
				getView().setProgressDialogVisibility(false);
				getView().showCustomTabsAuthentication(Uri.parse(authUrl));
			}

			@Override
			public void onFailure(Exception e) {
				Log.e(TAG, "Failed to obtain request token.", e);
				getView().setProgressDialogVisibility(false);
				getView().showToastMessage(R.string.login_request_token_failed);
			}

			@Override
			public void onCancel() {
				getView().setProgressDialogVisibility(false);
			}

			@Override
			public Call<OAuthTokenModel> execute(UserService service) {
				return service.requestToken();
			}
		}, UserService.class);
	}

	void onLogoutClicked() {
		client.clearOAuthTokens();
		preferences.clearUserData();
		getView().relaunch(R.string.logout_successful);
	}

	void onBecomeAFriendClick() {
		if (preferences.isUserLoggedIn()) {
			getView().showPremiumForm();
		} else {
			getView().showLoginFirst();
		}
	}

	void onBrowserCallback(String intentData) {
		if (PAYPAL_SUCCESS_CALLBACK_VALUE.equals(intentData)) {
			preferences.setPremium(true);
			EventBus.getDefault().post(new PremiumStatusEvent(true));
			getView().showToastMessage(R.string.premium_purchase_succeeded);
		} else if (PAYPAL_ERROR_CALLBACK_VALUE.equals(intentData)) {
			getView().showToastMessage(R.string.premium_purchase_failed);
		} else {
			onAuthorizationIntent(intentData);
		}
	}

	void checkForPremiumBook() {
		fetchHeader();
	}

	private void onAuthorizationIntent(String intentData) {
		String correctDataString = OAUTH_CALLBACK_VALUE + preferences.getTemporaryLoginToken();
		if (intentData.compareTo(correctDataString) != 0) {
			getView().showToastMessage(R.string.login_auth_callback_malformed);
			return;
		}

		getView().setProgressDialogVisibility(true);
		currentCall = client.call(new RestClientCallback<OAuthTokenModel, UserService>() {

			@Override
			public void onSuccess(OAuthTokenModel data) {
				preferences.storeAccessToken(data);
				fetchUser();
			}

			@Override
			public void onFailure(Exception e) {
				Log.e(TAG, "Failed to obtain access token.", e);
				getView().setProgressDialogVisibility(false);
				getView().showToastMessage(R.string.login_access_token_failed);
			}

			@Override
			public void onCancel() {
				getView().setProgressDialogVisibility(false);
			}

			@Override
			public Call<OAuthTokenModel> execute(UserService service) {
				return service.accessToken();
			}
		}, UserService.class);
	}

	private void fetchUser() {
		getView().setProgressDialogVisibility(true);

		/*
		 * We are marking user as logged in cause we already have credentials at this point.
		 * If this request fails we can perform it later on and ignore those errors for now.
		 */
		currentCall = client.call(new RestClientCallback<UserModel, UserService>() {
			@Override
			public void onSuccess(UserModel userModel) {
				preferences.setUsername(userModel.getUsername());
				preferences.setPremium(userModel.isPremium());
				EventBus.getDefault().post(new LoggedInEvent());
				getView().setProgressDialogVisibility(false);
				getView().setLoggedIn(true);
				getView().setLoggedUsername(userModel.getUsername());
			}

			@Override
			public void onFailure(Exception e) {
				EventBus.getDefault().post(new LoggedInEvent());
				getView().setProgressDialogVisibility(false);
				getView().setLoggedIn(true);
			}

			@Override
			public void onCancel() {
				EventBus.getDefault().post(new LoggedInEvent());
				getView().setProgressDialogVisibility(false);
				getView().setLoggedIn(true);
			}

			@Override
			public Call<UserModel> execute(UserService service) {
				return service.getUser();
			}
		}, UserService.class);
	}

	private void fetchHeader() {
		getView().setProgressDialogVisibility(true);
		currentCall = client.call(new RestClientCallback<List<BookDetailsModel>, BooksService>() {
			@Override
			public void onSuccess(List<BookDetailsModel> data) {
				getView().setProgressDialogVisibility(false);
				if (data.size() > 0) {
					getView().showPremiumBook(data.get(0).getSlug());
				} else {
					getView().showNoPremiumBookAvailable(preferences.isUserLoggedIn());
				}
			}

			@Override
			public void onFailure(Exception e) {
				getView().showToastMessage(R.string.fetching_premium_failed);
			}

			@Override
			public void onCancel() {
				// nop.
			}

			@Override
			public Call<List<BookDetailsModel>> execute(BooksService service) {
				return service.getPreview();
			}
		}, BooksService.class);
	}

	private void checkPremiumStatus() {
		checkCall = client.call(new RestClientCallback<UserModel, UserService>() {
			@Override
			public void onSuccess(UserModel userModel) {
				boolean currentPremiumStatus = preferences.isUserPremium();
				preferences.setUsername(userModel.getUsername());
				preferences.setPremium(userModel.isPremium());
				if (currentPremiumStatus && !userModel.isPremium()) {
					getView().relaunch(R.string.subscription_lost);
				}
			}

			@Override
			public void onFailure(Exception e) {
				// nop
			}

			@Override
			public void onCancel() {
				// nop
			}

			@Override
			public Call<UserModel> execute(UserService service) {
				return service.getUser();
			}
		}, UserService.class);
	}
}
