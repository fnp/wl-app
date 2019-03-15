package com.moiseum.wolnelektury.base;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.moiseum.wolnelektury.BuildConfig;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.storage.BookStorage;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;
import com.moiseum.wolnelektury.utils.TrackerUtils;

import org.piwik.sdk.Tracker;

import io.fabric.sdk.android.Fabric;

public class WLApplication extends MultiDexApplication {

	private static WLApplication instance;
	private RestClient restClient;
	private BookStorage bookStorage;
	private Tracker tracker;
	private SharedPreferencesUtils preferences;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		bookStorage = new BookStorage(this);

		Crashlytics crashlytics = new Crashlytics.Builder()
				.core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
				.build();
		// TODO: Change before release
		Fabric.with(this, new Crashlytics());
	}

	public static WLApplication getInstance() {
		return instance;
	}

	public RestClient getRestClient() {
		if (restClient == null) {
			restClient = new RestClient(getApplicationContext());
		}
		return restClient;
	}

	public BookStorage getBookStorage() {
		return bookStorage;
	}

	public synchronized Tracker getTracker() {
		if (tracker == null) {
			tracker = TrackerUtils.create(this);
		}
		return tracker;
	}

	public SharedPreferencesUtils getPreferences() {
		if (preferences == null) {
			preferences = new SharedPreferencesUtils(this);
		}
		return preferences;
	}
}
