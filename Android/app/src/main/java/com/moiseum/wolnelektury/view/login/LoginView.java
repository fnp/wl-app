package com.moiseum.wolnelektury.view.login;

import android.net.Uri;
import android.support.annotation.StringRes;

/**
 * Created by Piotr Ostrowski on 12.09.2018.
 */
public interface LoginView {

	void setProgressDialogVisibility(boolean visible);

	void showToastMessage(@StringRes int messageResId);

	void showCustomTabsAuthentication(Uri authorizationUrl);
}
