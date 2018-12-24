package com.moiseum.wolnelektury.view.main;

import android.net.Uri;
import android.support.annotation.StringRes;

/**
 * Created by Piotr Ostrowski on 12.06.2018.
 */
public interface MainView {

    void setLoggedIn(boolean loggedIn);

    void setLoggedUsername(String username);

    void setProgressDialogVisibility(boolean visible);

    void showToastMessage(@StringRes int messageResId);

    void showCustomTabsAuthentication(Uri authorizationUrl);

    void showPremiumBook(String slug);

	void showNoPremiumBookAvailable(boolean userLoggedIn);

	void showPremiumForm();

	void showLoginFirst();

    void relaunch(@StringRes int relaunchMessageResId);
}
