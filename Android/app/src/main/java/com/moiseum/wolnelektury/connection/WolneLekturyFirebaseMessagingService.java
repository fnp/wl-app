package com.moiseum.wolnelektury.connection;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moiseum.wolnelektury.BuildConfig;
import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;
import com.moiseum.wolnelektury.view.main.MainActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import timber.log.Timber;

/**
 * Created by Piotr Ostrowski on 27.08.2018.
 */
public class WolneLekturyFirebaseMessagingService extends FirebaseMessagingService {

	private static final String TAG = WolneLekturyFirebaseMessagingService.class.getSimpleName();
	private SharedPreferencesUtils preferences = WLApplication.getInstance().getPreferences();

	@Override
	public void onNewToken(String s) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Refreshed token: " + s);
		}
		FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.default_notification_topic));
	}

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		Log.d(TAG, "Received notification " + remoteMessage);
		if (preferences.getNotifications()) {
			String title = remoteMessage.getData().get("title");
			String body = remoteMessage.getData().get("body");
			String imageUrl = remoteMessage.getData().get("imageUrl");
			Bitmap bitmap = getBitmapFromUrl(imageUrl);
			sendNotification(title, body, bitmap);
		} else {
			Log.d(TAG, "Skipping notification cause of user preference");
		}
	}

	private void sendNotification(String title, String body, Bitmap image) {
		MainActivity.MainIntent intent = new MainActivity.MainIntent(R.string.app_name, this);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
				.setLargeIcon(image)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(title)
				.setContentText(body)
				.setColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setColorized(true)
				.setStyle(new NotificationCompat.BigPictureStyle()
						.bigPicture(image)
						.setBigContentTitle(title)
						.setSummaryText(body)
				)
				.setAutoCancel(true)
				.setSound(defaultUri)
				.setContentIntent(pendingIntent);

		if (notificationManager != null) {
			notificationManager.notify(0, notificationBuilder.build());
		}
	}

	/*
	 *To get a Bitmap image from the URL received
	 * */
	private Bitmap getBitmapFromUrl(String imageUrl) {
		if (imageUrl == null) {
			return null;
		}
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			return BitmapFactory.decodeStream(input);
		} catch (Exception e) {
			Timber.tag(TAG).e(e, "Failed to fetch notification image " + e.getMessage());
			return null;
		}
	}
}
