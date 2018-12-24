package com.moiseum.wolnelektury.view.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractActivity;
import com.moiseum.wolnelektury.base.AbstractIntent;
import com.moiseum.wolnelektury.base.mvp.PresenterActivity;

import butterknife.OnClick;

/**
 * Created by Piotr Ostrowski on 11.09.2018.
 */
public class LoginActivity extends PresenterActivity<LoginPresenter> implements LoginView {

	public static class LoginIntent extends AbstractIntent {

		public LoginIntent(Context context) {
			super(context, LoginActivity.class);
		}
	}

	private ProgressDialog progressDialog;

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_login;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {

	}

	@Override
	protected LoginPresenter createPresenter() {
		return new LoginPresenter(this);
	}

	@Override
	public void setProgressDialogVisibility(boolean visible) {
		if (visible && progressDialog == null) {
			String dialogMessage = getString(R.string.main_view_progress);
			progressDialog = ProgressDialog.show(this, null, dialogMessage, true, false);
		} else if (!visible && progressDialog != null) {
			progressDialog.hide();
			progressDialog = null;
		}
	}

	@Override
	public void showToastMessage(int messageResId) {
		Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show();
	}

	@Override
	public void showCustomTabsAuthentication(Uri authorizationUrl) {
		showBrowserView(authorizationUrl);
		finish();
	}

	@OnClick(R.id.ibBack)
	public void onBackClicked() {
		finish();
	}

	@OnClick(R.id.btnLogin)
	public void onLoginClicked() {
		getPresenter().onLoginClicked();
	}
}
