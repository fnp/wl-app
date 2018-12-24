package com.moiseum.wolnelektury.connection.downloads;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.connection.ErrorHandler;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.services.BooksService;

import org.greenrobot.eventbus.EventBus;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by piotrostrowski on 07.05.2017.
 */

public class FileDownloadIntentService extends IntentService {

	private static final String TAG = FileDownloadIntentService.class.getSimpleName();
	public static final String FILE_URL_KEY = "FileUrlKey";
	public static final String FILES_URLS_KEY = "FilesUrlsKey";

	public static void downloadFile(Context context, String fileUrl) {
		Intent downloadIntent = new Intent(context, FileDownloadIntentService.class);
		downloadIntent.putExtra(FILE_URL_KEY, fileUrl);
		context.startService(downloadIntent);
	}

	public static void downloadFiles(Context context, ArrayList<String> filesUrls) {
		Intent downloadIntent = new Intent(context, FileDownloadIntentService.class);
		downloadIntent.putExtra(FILES_URLS_KEY, Parcels.wrap(filesUrls));
		context.startService(downloadIntent);
	}

	public FileDownloadIntentService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}


	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		if (intent == null) {
			return;
		}

		if (intent.hasExtra(FILE_URL_KEY)) {
			String fileUrl = intent.getStringExtra(FILE_URL_KEY);
			checkCacheAndDownload(fileUrl);
		} else if (intent.hasExtra(FILES_URLS_KEY)) {
			ArrayList<String> filesUrls = Parcels.unwrap(intent.getParcelableExtra(FILES_URLS_KEY));
			for (String fileUrl : filesUrls) {
				if (!checkCacheAndDownload(fileUrl)) {
					break;
				}
			}
		}
	}

	private boolean checkCacheAndDownload(String fileUrl) {
		if (FileCacheUtils.getCachedFileForUrl(fileUrl) != null) {
			Log.v(TAG,  fileUrl + " is already in cache.");
			EventBus.getDefault().post(new DownloadFileEvent(fileUrl, true));
			return true;
		}
		return downloadFile(fileUrl);
	}

	private boolean downloadFile(String fileUrl) {
		RestClient client = WLApplication.getInstance().getRestClient();
		BooksService booksService = client.createService(BooksService.class);
		try {
			Call<ResponseBody> call = booksService.downloadFileWithUrl(fileUrl);
			Response<ResponseBody> response = call.execute();
			if (response.isSuccessful()) {
				boolean result = FileCacheUtils.writeResponseBodyToDiskCache(response.body(), fileUrl);
				EventBus.getDefault().post(new DownloadFileEvent(fileUrl, result));
			} else {
				ErrorHandler<ResponseBody> errorHandler = new ErrorHandler<>(response);
				errorHandler.handle();
				//if nothing cause, throw exception
				throw new UnsupportedOperationException("Unhandled exception");
			}
		} catch (IOException e) {
			Log.e(TAG, "Failed to download audio file: " + fileUrl, e);
			EventBus.getDefault().post(new DownloadFileEvent(fileUrl, false));
			return false;
		}
		return true;
	}

	public static class DownloadFileEvent {

		private String fileUrl;
		private boolean success;

		DownloadFileEvent(String fileUrl, boolean success) {
			this.fileUrl = fileUrl;
			this.success = success;
		}

		public String getFileUrl() {
			return fileUrl;
		}

		public boolean isSuccess() {
			return success;
		}
	}
}
