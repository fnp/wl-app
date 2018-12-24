package com.moiseum.wolnelektury.base;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;
import com.moiseum.wolnelektury.utils.TrackerUtils;
import com.moiseum.wolnelektury.view.login.LoginActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Base fragment with view binding.
 */
public abstract class AbstractFragment extends Fragment {

	private static final String CHROME_PACKAGE_ID = "com.android.chrome";

	private Unbinder unbinder;
	private CompositeDisposable disposables = new CompositeDisposable();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(getLayoutResourceId(), container, false);
		unbinder = ButterKnife.bind(this, view);
		prepareView(view, savedInstanceState);
		trackScreen();
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
		disposables.dispose();
	}

	protected void addDisposable(Disposable disposable) {
		this.disposables.add(disposable);
	}

	private void trackScreen() {
		String path = getClass().getPackage().getName();
		String name = getNameForTracker();
		TrackerUtils.trackScreen(path, name);
	}

	protected String getNameForTracker() {
		return getClass().getSimpleName().replaceAll("Fragment", "");
	}

	protected void setupToolbar(Toolbar toolbar) {
		AbstractActivity activity = (AbstractActivity) getActivity();
		if (activity != null) {
			activity.setupToolbar(toolbar);
		}
	}

	protected void showPayPalForm() {
		SharedPreferencesUtils preferences = WLApplication.getInstance().getPreferences();
		if (preferences.isUserLoggedIn()) {
			showBrowserView(Uri.parse(RestClient.WEB_PAYPAL_FORM_URL));
		} else {
			startActivity(new LoginActivity.LoginIntent(getContext()));
		}
	}

	protected void showBrowserView(Uri uri) {
		if (getActivity() != null) {
			if (checkForPackageExistance(CHROME_PACKAGE_ID)) {
				CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
					@Override
					public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient client) {
						CustomTabsIntent intent = new CustomTabsIntent.Builder()
								.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
								.build();

						client.warmup(0L);
						intent.launchUrl(getActivity(), uri);
					}

					@Override
					public void onServiceDisconnected(ComponentName name) {

					}
				};
				CustomTabsClient.bindCustomTabsService(getActivity(), CHROME_PACKAGE_ID, connection);
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), R.string.install_chrome, Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	protected void showShareActivity(String shareUrl) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
		startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)));
	}

	private boolean checkForPackageExistance(String targetPackage) {
		List<ApplicationInfo> packages;
		PackageManager pm;

		pm = getActivity().getPackageManager();
		packages = pm.getInstalledApplications(0);
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals(targetPackage)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Providing layout resource ID for inflating.
	 *
	 * @return layout resource ID.
	 */
	public abstract int getLayoutResourceId();

	/**
	 * Method called from @link{BindingFragment#onCreateView}. This will be the place to setup view stuff.
	 *
	 * @param view inflated View.
	 * @param savedInstanceState Bundle with current instance state.
	 */
	public abstract void prepareView(View view, Bundle savedInstanceState);
}
