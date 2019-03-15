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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;
import com.moiseum.wolnelektury.view.login.LoginActivity;

import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class AbstractActivity extends AppCompatActivity {

	private static final String CHROME_PACKAGE_ID = "com.android.chrome";

	private CompositeDisposable disposables = new CompositeDisposable();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		ActionBar actionBar = getSupportActionBar();
		//		if (actionBar != null) {
		//			actionBar.setElevation(0);
		//		}

		setContentView(getLayoutResourceId());
		ButterKnife.bind(this);
		prepareView(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onHomeClicked();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		disposables.dispose();
	}

	protected void addDisposable(Disposable disposable) {
		this.disposables.add(disposable);
	}

	protected void onHomeClicked() {
		finish();
	}

	protected void setBackButtonEnable(boolean enable) {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(enable);
		}
	}

	protected void hideToolbar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
	}

	protected void setupToolbar(Toolbar toolbar) {
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
	}

	protected void showPayPalForm() {
		showBrowserView(Uri.parse(RestClient.WEB_PAYPAL_FORM_URL));
	}

	protected void showBrowserView(Uri uri) {
		if (checkForPackageExistance(CHROME_PACKAGE_ID)) {
			CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
				@Override
				public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient client) {
					CustomTabsIntent intent = new CustomTabsIntent.Builder()
							.setToolbarColor(ContextCompat.getColor(AbstractActivity.this, R.color.colorAccent))
							.build();

					client.warmup(0L);
					intent.launchUrl(AbstractActivity.this, uri);
				}

				@Override
				public void onServiceDisconnected(ComponentName name) {

				}
			};
			CustomTabsClient.bindCustomTabsService(this, CHROME_PACKAGE_ID, connection);
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			} else {
				Toast.makeText(this, R.string.install_chrome, Toast.LENGTH_LONG).show();
			}
		}
	}

	private boolean checkForPackageExistance(String targetPackage) {
		List<ApplicationInfo> packages;
		PackageManager pm;

		pm = getPackageManager();
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
	 * Method called from @link{{@link AbstractActivity}#onCreate}. This will be the place to setup view stuff.
	 *
	 * @param savedInstanceState Bundle with current instance state.
	 */
	public abstract void prepareView(Bundle savedInstanceState);
}
