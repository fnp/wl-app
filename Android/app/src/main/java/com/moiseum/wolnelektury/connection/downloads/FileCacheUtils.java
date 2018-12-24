package com.moiseum.wolnelektury.connection.downloads;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.connection.models.MediaModel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.Completable;
import okhttp3.ResponseBody;

import static android.os.Environment.isExternalStorageRemovable;

/**
 * Created by Piotr Ostrowski on 10.05.2017.
 */

public class FileCacheUtils {

	private static final String FILES_CACHE = "FilesCache";
	private static final int BUFFER_SIZE = 4096;
	private static final long PROGRESS_UPDATE_RATE = 10;
	private static final String TAG = FileCacheUtils.class.getSimpleName();

	private FileCacheUtils() {
		// nop.
	}

	public static String getCachedFileForUrl(String url) {
		File cachedFile = new File(getCurrentCachePath() + File.separator + url.hashCode());
		if (cachedFile.exists()) {
			return cachedFile.getAbsolutePath();
		} else {
			return null;
		}
	}

	public static boolean deleteFileForUrl(String url) {
		File cachedFile = new File(getCurrentCachePath() + File.separator + url.hashCode());
		if (cachedFile.exists()) {
			return cachedFile.delete();
		} else {
			Log.e(TAG, "There is no file to be removed: " + url);
			return false;
		}
	}

	public static boolean writeResponseBodyToDiskCache(ResponseBody body, String fileUrl) {
		try {
			String cachePath = getCurrentCachePath();
			File fileCacheDir = new File(cachePath);
			File downloadFile = new File(cachePath + File.separator + fileUrl.hashCode() + ".download");
			createCacheFolderAndFile(cachePath, fileCacheDir, downloadFile);

			InputStream inputStream = null;
			OutputStream outputStream = null;

			try {
				byte[] fileReader = new byte[BUFFER_SIZE];
				long fileSize = body.contentLength();
				long fileSizeDownloaded = 0;

				inputStream = body.byteStream();
				outputStream = new FileOutputStream(downloadFile);

				int updateRate = 0;
				while (true) {
					int read = inputStream.read(fileReader);
					if (read == -1) {
						break;
					}

					outputStream.write(fileReader, 0, read);
					fileSizeDownloaded += read;
					if (updateRate++ % PROGRESS_UPDATE_RATE == 0) {
						EventBus.getDefault().post(new DownloadProgressEvent(fileUrl, fileSizeDownloaded, fileSize));
					}
				}
				outputStream.flush();

				File audioFileDest = new File(cachePath + File.separator + fileUrl.hashCode());
				boolean renamed = downloadFile.renameTo(audioFileDest);
				if (!renamed) {
					throw new IOException("Failed to rename downloaded file: " + audioFileDest.getAbsolutePath());
				}
				return true;
			} catch (IOException e) {
				Log.e(TAG, "Failed to save file to cache: " + fileUrl, e);
				downloadFile.delete();
				return false;
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "File creation or streaming closure failed", e);
			return false;
		}
	}

	private static void createCacheFolderAndFile(String cachePath, File audioCacheDir, File audioFile) throws IOException {
		if (!audioCacheDir.exists()) {
			boolean result = audioCacheDir.mkdir();
			if (!result) {
				throw new IOException("Failed to create AudioCache Dir.");
			}
		}
		if (!audioFile.exists()) {
			boolean result = audioFile.createNewFile();
			if (!result) {
				throw new IOException("Failed to create file in path: " + audioFile.getAbsolutePath());
			}
		}
	}

	@NonNull
	private static String getCurrentCachePath() {
		File externalCacheDir = WLApplication.getInstance().getApplicationContext().getExternalCacheDir();
		String cachePath = (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable()) &&
				externalCacheDir != null ? externalCacheDir.getPath() : WLApplication.getInstance().getCacheDir().getPath();
		return cachePath + File.separator + FILES_CACHE;
	}

	public static Completable deleteAudiobookFiles(List<String> fileUrls) {
		return Completable.fromAction(() -> {
			for (String fileUrl : fileUrls) {
				boolean deleted = deleteFileForUrl(fileUrl);
				if (!deleted) {
					Log.e(TAG, "Failed to delete file " + FileCacheUtils.getCachedFileForUrl(fileUrl));
				}
			}
		});
	}

	public static Completable deleteEbookFile(final String epubUrl) {
		return Completable.fromAction(() -> deleteFileForUrl(epubUrl));
	}

	/**
	 * Event indicating progress.
	 */
	public static class DownloadProgressEvent {

		private String fileUrl;
		private long downloaded;
		private long total;

		public DownloadProgressEvent(String fileUrl, long downloaded, long total) {
			this.fileUrl = fileUrl;
			this.downloaded = downloaded;
			this.total = total;
		}

		public String getFileUrl() {
			return fileUrl;
		}

		public long getDownloaded() {
			return downloaded;
		}

		public long getTotal() {
			return total;
		}
	}
}
