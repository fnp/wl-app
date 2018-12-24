package com.moiseum.wolnelektury.view.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.view.supportus.SupportUsActivity;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsFragment extends PresenterFragment<SettingsPresenter> implements SettingsView {

	public static SettingsFragment newInstance() {
		return new SettingsFragment();
	}

	@BindView(R.id.swNotifications)
	SwitchCompat swNotifications;
	@BindView(R.id.tvState)
	TextView tvState;
	@BindView(R.id.btnBecomeAFriend)
	Button btnBecomeAFriend;

	private ProgressDialog progressDialog;

	@Override
	protected SettingsPresenter createPresenter() {
		return new SettingsPresenter(this);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_settings;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
	}

	@OnClick(R.id.btnBecomeAFriend)
	public void onBecomeAFriendClicked() {
		showPayPalForm();
	}

	@OnClick(R.id.btnDelete)
	public void onDeleteAllClicked() {
		getPresenter().onDeleteAllClicked();
	}

	@OnCheckedChanged(R.id.swNotifications)
	public void onNotificationsCheckedChanged(CompoundButton button, boolean checked) {
		getPresenter().onNotificationsChanged(checked);
	}

	@Override
	public void initializeSettings(boolean notifications, boolean userPremium) {
		swNotifications.setChecked(notifications);
		tvState.setText(userPremium ? R.string.active : R.string.inactive);
		btnBecomeAFriend.setVisibility(userPremium ? View.GONE : View.VISIBLE);
	}

	@Override
	public void showProgressDialog(boolean visible) {
		if (visible && progressDialog == null) {
			String dialogMessage = getString(R.string.removing_all_files);
			progressDialog = ProgressDialog.show(getContext(), null, dialogMessage, true, false);
		} else if (!visible && progressDialog != null) {
			progressDialog.hide();
			progressDialog = null;
		}
	}

	@Override
	public void showDeletionCompleted() {
		Toast.makeText(getContext(), R.string.all_files_removed, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showDeletionFailed(Throwable error) {
		Toast.makeText(getContext(), getString(R.string.all_files_failed_to_remove, error.getMessage()), Toast.LENGTH_SHORT).show();
	}
}
