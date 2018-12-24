package com.moiseum.wolnelektury.view.login;

import android.net.Uri;
import android.util.Log;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.base.mvp.Presenter;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.RestClientCallback;
import com.moiseum.wolnelektury.connection.models.OAuthTokenModel;
import com.moiseum.wolnelektury.connection.services.UserService;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;

import retrofit2.Call;

/**
 * Created by Piotr Ostrowski on 12.09.2018.
 */
public class LoginPresenter extends Presenter<LoginView> {

	private static final String TAG = LoginPresenter.class.getSimpleName();

	private RestClient client = WLApplication.getInstance().getRestClient();
	private SharedPreferencesUtils preferences = WLApplication.getInstance().getPreferences();
	private Call currentCall;

	LoginPresenter(LoginView view) {
		super(view);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (currentCall != null) {
			currentCall.cancel();
		}
	}

	protected void onLoginClicked() {
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
}
