package com.moiseum.wolnelektury.utils;

import android.content.Context;

import com.moiseum.wolnelektury.base.WLApplication;

import org.piwik.sdk.Piwik;
import org.piwik.sdk.Tracker;
import org.piwik.sdk.TrackerConfig;
import org.piwik.sdk.extra.TrackHelper;

/**
 * @author golonkos
 */

public final class TrackerUtils {

	private static final String PIWIK_URL = "https://piwik.nowoczesnapolska.org.pl/nocas/piwik.php";
	private static final String TRACKER_NAME = "MainTracker";

	public static Tracker create(Context context) {
		return Piwik.getInstance(context).newTracker(new TrackerConfig(PIWIK_URL, 29, TRACKER_NAME));
	}

	public static void trackScreen(String path, String name) {
		Tracker tracker = WLApplication.getInstance().getTracker();
		TrackHelper.track().screen(path).title(name).with(tracker);
	}
}
