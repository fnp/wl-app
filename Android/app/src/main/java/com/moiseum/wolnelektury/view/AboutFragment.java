package com.moiseum.wolnelektury.view;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractFragment;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.events.LoggedInEvent;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author golonkos
 */

public class AboutFragment extends AbstractFragment {

	public static AboutFragment newInstance() {
		return new AboutFragment();
	}

	@BindView(R.id.btnBecomeAFriend)
	Button btnBecomeAFriend;
	@BindView(R.id.tvAbout)
	TextView tvAbout;

	private SharedPreferencesUtils preferences = WLApplication.getInstance().getPreferences();

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_about;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		btnBecomeAFriend.setVisibility(preferences.isUserPremium() ? View.GONE : View.VISIBLE);
		tvAbout.setText(Html.fromHtml(getString(R.string.about_text)));
		tvAbout.setLinksClickable(true);
		tvAbout.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onLoggedIn(LoggedInEvent event) {
		btnBecomeAFriend.setVisibility(View.GONE);
	}

	@OnClick(R.id.btnBecomeAFriend)
	public void onBecomeAFriendClicked() {
		showPayPalForm();
	}
}
