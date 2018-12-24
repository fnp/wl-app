package com.moiseum.wolnelektury.view.main;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractIntent;
import com.moiseum.wolnelektury.base.mvp.PresenterActivity;
import com.moiseum.wolnelektury.view.book.BookActivity;
import com.moiseum.wolnelektury.view.book.BookType;
import com.moiseum.wolnelektury.view.supportus.SupportUsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.moiseum.wolnelektury.view.main.MainActivity.MainIntent.RELAUNCH_MESSAGE_KEY;

public class MainActivity extends PresenterActivity<MainPresenter> implements MainView {

	private ProgressDialog progressDialog;

	public static class MainIntent extends AbstractIntent {

		static final String RELAUNCH_MESSAGE_KEY = "RelaunchMessageKey";

		public MainIntent(Context context) {
			super(context, MainActivity.class);
		}

		public MainIntent(@StringRes int relaunchMessageResId, Context context) {
			super(context, MainActivity.class);
			this.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			this.putExtra(RELAUNCH_MESSAGE_KEY, relaunchMessageResId);
		}
	}

	public static class ChangeNavigationEvent {
		private final NavigationElement element;

		public ChangeNavigationEvent(NavigationElement element) {
			if (element == NavigationElement.SEPARATOR || element == NavigationElement.SUPPORT_US) {
				throw new IllegalArgumentException("Unsupported navigation element");
			}
			this.element = element;
		}

		public NavigationElement getElement() {
			return element;
		}
	}

	@BindView(R.id.drawer_layout)
	DrawerLayout drawerLayout;
	@BindView(R.id.rvNavigation)
	RecyclerView rvNavigation;
	@BindView(R.id.btnLogin)
	Button btnLogin;
	@BindView(R.id.llLoggedInContainer)
	View llLoggedInContainer;
	@BindView(R.id.tvUsername)
	TextView tvUsername;

	private ActionBarDrawerToggle drawerToggle;
	private NavigationAdapter navigationAdapter;
	private NavigationElement currentNavigationElement;

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_main;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {
		setBackButtonEnable(true);
		initDrawer();
		EventBus.getDefault().register(this);

		if (getIntent().hasExtra(RELAUNCH_MESSAGE_KEY)) {
			Toast.makeText(this, getIntent().getIntExtra(RELAUNCH_MESSAGE_KEY, 0), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected MainPresenter createPresenter() {
		return new MainPresenter(this);
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String action = intent.getAction();
		String data = intent.getDataString();
		if (Intent.ACTION_VIEW.equals(action) && data != null) {
			getPresenter().onBrowserCallback(data);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	private void initDrawer() {
		navigationAdapter = new NavigationAdapter(this, () -> {
			showPayPalForm();
			drawerLayout.closeDrawers();
		});
		navigationAdapter.setOnItemClickListener((item, view, position) -> {
			if (item != NavigationElement.SEPARATOR) {
				selectItem(item);
			}
		});
		navigationAdapter.selectItem(NavigationElement.LIBRARY);
		rvNavigation.setLayoutManager(new LinearLayoutManager(this));
		rvNavigation.setAdapter(navigationAdapter);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}
		};
		drawerLayout.addDrawerListener(drawerToggle);

		selectItem(NavigationElement.LIBRARY);
	}

	public void selectItem(NavigationElement navigationElement) {
		if (NavigationElement.PREMIUM == navigationElement) {
			getPresenter().checkForPremiumBook();
		} else {
			currentNavigationElement = navigationElement;
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content, navigationElement.getFragment()).commit();
			setTitle(navigationElement.getTitle());
		}
		drawerLayout.closeDrawers();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onBackPressed() {
		if (NavigationElement.LIBRARY != currentNavigationElement) {
			selectItem(NavigationElement.LIBRARY);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	@Override
	protected void onHomeClicked() {
		drawerLayout.openDrawer(GravityCompat.START);
	}

	@SuppressWarnings("unused")
	@Subscribe
	public void onChangeNavigation(ChangeNavigationEvent event) {
		selectItem(event.getElement());
		navigationAdapter.selectItem(event.getElement());
	}

	@OnClick(R.id.btnLogin)
	public void onLoginClicked() {
		getPresenter().onLoginClicked();
	}

	@OnClick(R.id.btnLogout)
	public void onLogoutClicked() {
		getPresenter().onLogoutClicked();
	}

	@Override
	public void setLoggedIn(boolean loggedIn) {
		if (loggedIn) {
			btnLogin.setVisibility(View.GONE);
			llLoggedInContainer.setVisibility(View.VISIBLE);
		} else {
			btnLogin.setVisibility(View.VISIBLE);
			llLoggedInContainer.setVisibility(View.GONE);
		}
	}

	@Override
	public void setLoggedUsername(String username) {
		tvUsername.setText(username);
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
	}

	@Override
	public void showPremiumBook(String slug) {
		startActivity(new BookActivity.BookIntent(slug, BookType.TYPE_PREMIUM, this));
	}

	@Override
	public void showNoPremiumBookAvailable(boolean userLoggedIn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle(R.string.no_prapremiere_title);

		if (!userLoggedIn) {
			builder.setMessage(R.string.no_prapremiere_message)
					.setPositiveButton(R.string.become_a_friend, (dialog, which) -> getPresenter().onBecomeAFriendClick())
					.setNegativeButton(R.string.no_thanks, (dialog, which) -> {
						// nop.
					});
		} else {
			builder.setMessage(R.string.no_prapremiere_message_logged)
					.setPositiveButton(R.string.OK, (dialog, which) -> {
						// nop.
					});
		}
		builder.create()
				.show();
	}

	@Override
	public void showPremiumForm() {
		showPayPalForm();
	}

	@Override
	public void showLoginFirst() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.login)
				.setMessage(R.string.login_first)
				.setPositiveButton(R.string.login, (dialog, which) -> {
					getPresenter().onLoginClicked();
				})
				.setNegativeButton(R.string.no_thanks, (dialog, which) -> {
					// nop.
				})
				.create()
				.show();
	}

	@Override
	public void relaunch(int relaunchMessageResId) {
		MainIntent intent = new MainIntent(relaunchMessageResId, this);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
