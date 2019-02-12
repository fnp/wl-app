package com.moiseum.wolnelektury.view.player;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;
import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.storage.BookStorage;
import com.moiseum.wolnelektury.view.player.service.AudiobookLibrary;
import com.moiseum.wolnelektury.view.player.service.AudiobookService;
import com.moiseum.wolnelektury.view.player.service.MediaBrowserHelper;

import java.util.List;
import java.util.Locale;

/**
 * Created by Piotr Ostrowski on 22.05.2018.
 */
public class PlayerPresenter extends FragmentPresenter<PlayerView> {

	private final BookDetailsModel book;
	private final BookModel storedBook;
	private final String bookSlug;

	private final BookStorage storage = WLApplication.getInstance().getBookStorage();
	private MediaBrowserHelper mMediaBrowserHelper;
	private boolean mIsPlaying;

	/**
	 * Customize the connection to our {@link android.support.v4.media.MediaBrowserServiceCompat}
	 * and implement our app specific desires.
	 */
	private class MediaBrowserConnection extends MediaBrowserHelper {
		private MediaBrowserConnection(Context context) {
			super(context, AudiobookService.class);
		}

		@Override
		protected void onChildrenLoaded(@NonNull String parentId,
		                                @NonNull List<MediaBrowserCompat.MediaItem> children) {
			super.onChildrenLoaded(parentId, children);
			AudiobookLibrary.createAudiobookMetadata(book, bookSlug);

			if (isSamePlaylist(children, AudiobookLibrary.getMediaItems())) {
				// Do not modify the playlist when entering the same audiobook
				return;
			}

			final MediaControllerCompat mediaController = getMediaController();
			mediaController.getTransportControls().sendCustomAction(AudiobookService.ACTION_CLEAR_PLAYLIST, null);


			for (final MediaBrowserCompat.MediaItem mediaItem : AudiobookLibrary.getMediaItems()) {
				mediaController.addQueueItem(mediaItem.getDescription());
			}

			// Call prepare now so pressing play just works.
			mediaController.getTransportControls().prepare();
			if (storedBook != null) {
				mediaController.getTransportControls().skipToQueueItem(storedBook.getCurrentAudioChapter());
				mediaController.getTransportControls().pause();
			}
		}

		private boolean isSamePlaylist(@NonNull List<MediaBrowserCompat.MediaItem> current, @NonNull List<MediaBrowserCompat.MediaItem> oncoming) {
			if (current.size() != oncoming.size()) {
				return false;
			}
			for (int i = 0; i < current.size(); i++) {
				MediaBrowserCompat.MediaItem currentItem = current.get(i);
				MediaBrowserCompat.MediaItem oncomingItem = oncoming.get(i);
				if (!currentItem.getDescription().getMediaId().equals(oncomingItem.getDescription().getMediaId())) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Implementation of the {@link MediaControllerCompat.Callback} methods we're interested in.
	 * <p>
	 * Here would also be where one could override
	 * {@code onQueueChanged(List<MediaSessionCompat.QueueItem> queue)} to get informed when items
	 * are added or removed from the queue. We don't do this here in order to keep the UI
	 * simple.
	 */
	private class PlayerMediaControllerCallback extends MediaControllerCompat.Callback {
		@Override
		public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
			if (playbackState != null) {
				if (playbackState.getState() == PlaybackStateCompat.STATE_ERROR) {
					getView().onPlayerError();
				}

				mIsPlaying = playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
				getView().setPlayButtonState(mIsPlaying);

				if (mIsPlaying && playbackState.getExtras() != null) {
					int total = playbackState.getExtras().getInt(AudiobookService.EXTRA_PLAYBACK_TOTAL);
					getView().setTrackDuration(total, getCurrentTimerText(total));
				}
			}
		}

		@Override
		public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
			if (mediaMetadata == null) {
				return;
			}
			if (!book.getAudiobookFilesUrls().contains(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
				return;
			}

			int currentChapter = (int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER);
			if (storedBook != null) {
				storedBook.setCurrentAudioChapter(currentChapter);
				storage.update(storedBook);
			}

			String chapterTitle = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
			getView().setTrackTexts(chapterTitle, currentChapter);
		}

		@Override
		public void onExtrasChanged(Bundle extras) {
			if (extras.containsKey(AudiobookService.EXTRA_PLAYBACK_CURRENT)) {
				int position = extras.getInt(AudiobookService.EXTRA_PLAYBACK_CURRENT);
				getView().setTrackPosition(position, getCurrentTimerText(position));
			}
			if (extras.containsKey(AudiobookService.EXTRA_PLAYBACK_TOTAL)) {
				int total = extras.getInt(AudiobookService.EXTRA_PLAYBACK_TOTAL);
				getView().setTrackDuration(total, getCurrentTimerText(total));
			}
		}

		@Override
		public void onSessionDestroyed() {
			super.onSessionDestroyed();
		}

		@Override
		public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
			super.onQueueChanged(queue);
		}
	}

	PlayerPresenter(BookDetailsModel book, String slug, PlayerView view, Context context) {
		super(view);
		this.book = book;
		this.storedBook = storage.find(slug);
		this.bookSlug = slug;

		mMediaBrowserHelper = new MediaBrowserConnection(context);
		mMediaBrowserHelper.registerCallback(new PlayerMediaControllerCallback());
	}

	@Override
	public void onStart() {
		super.onStart();
		mMediaBrowserHelper.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		mMediaBrowserHelper.onStop();
	}

	public void playOrPause(boolean pauseCall) {
		if (mIsPlaying) {
			mMediaBrowserHelper.getTransportControls().pause();
		} else {
			mMediaBrowserHelper.getTransportControls().play();
		}
	}

	public void changeChapter(boolean next) {
		if (next) {
			mMediaBrowserHelper.getTransportControls().skipToNext();
		} else {
			mMediaBrowserHelper.getTransportControls().skipToPrevious();
		}
	}

	public void seekToButton(boolean forward) {
		if (forward) {
			mMediaBrowserHelper.getTransportControls().fastForward();
		} else {
			mMediaBrowserHelper.getTransportControls().rewind();
		}
	}

	public void seekTo(int userSelectedPosition) {
		mMediaBrowserHelper.getTransportControls().seekTo(userSelectedPosition);
	}

	public String getCurrentTimerText(int currentPosition) {
		StringBuilder sb = new StringBuilder();
		int minutes = (currentPosition % (1000 * 60 * 60)) / (1000 * 60);
		int seconds = ((currentPosition % (1000 * 60 * 60)) % (1000 * 60) / 1000);
		if (currentPosition > (1000 * 60 * 60)) {
			int hours = (currentPosition / (1000 * 60 * 60));
			sb.append(String.format(Locale.getDefault(), "%01d", hours));
			sb.append(":");
		}
		sb.append(String.format(Locale.getDefault(), "%02d", minutes));
		sb.append(":");
		sb.append(String.format(Locale.getDefault(), "%02d", seconds));
		return sb.toString();
	}
}
