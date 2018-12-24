package com.moiseum.wolnelektury.view.settings;

public interface SettingsView {

	void initializeSettings(boolean notifications, boolean userPremium);

	void showProgressDialog(boolean visible);

	void showDeletionCompleted();

	void showDeletionFailed(Throwable error);
}
