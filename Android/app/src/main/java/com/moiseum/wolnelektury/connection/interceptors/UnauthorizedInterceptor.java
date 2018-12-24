package com.moiseum.wolnelektury.connection.interceptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.view.main.MainActivity;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Piotr Ostrowski on 23.06.2018.
 */
public class UnauthorizedInterceptor implements Interceptor {

	private static final String TAG = UnauthorizedInterceptor.class.getSimpleName();

	@Override
	public Response intercept(@NonNull Chain chain) throws IOException {
		Response response = chain.proceed(chain.request());
		if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
			Log.e(TAG, "Provided credentials were invalid. Re-launching app");

			WLApplication.getInstance().getPreferences().clearUserData();
			WLApplication.getInstance().getRestClient().clearOAuthTokens();

			Context context = WLApplication.getInstance().getApplicationContext();
			MainActivity.MainIntent intent = new MainActivity.MainIntent(R.string.unauthorized, context);
			context.startActivity(intent);
		}
		return response;
	}
}
