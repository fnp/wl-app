package com.moiseum.wolnelektury.view.player.playlist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.moiseum.wolnelektury.base.DataProvider;
import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.models.MediaModel;
import com.moiseum.wolnelektury.connection.services.BooksService;
import com.moiseum.wolnelektury.storage.BookStorage;
import com.moiseum.wolnelektury.view.book.list.BookListType;
import com.moiseum.wolnelektury.view.player.PlayerPresenter;
import com.moiseum.wolnelektury.view.player.service.AudiobookService;
import com.moiseum.wolnelektury.view.player.service.MediaBrowserHelper;

import java.util.List;

/**
 * Created by Piotr Ostrowski on 28.05.2018.
 */
public class PlayerPlaylistPresenter extends FragmentPresenter<PlayerPlaylistView> {

	private class PlayerPlaylistMediaControllerCallback extends MediaControllerCompat.Callback {

		@Override
		public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
			if (mediaMetadata == null) {
				return;
			}
			if (!containsMediaId(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
				return;
			}

			int currentChapter = (int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER);
			getView().setCurrentPlaylistItem(currentChapter);
		}

		private boolean containsMediaId(String mediaIdUrl) {
			for (MediaModel model : media) {
				if (model.getUrl().equals(mediaIdUrl)) {
					return true;
				}
			}
			return false;
		}
	}

	private final List<MediaModel> media;
	private MediaBrowserHelper mMediaBrowserHelper;

	PlayerPlaylistPresenter(List<MediaModel> mediaFiles, PlayerPlaylistView view, Context context) {
		super(view);
		media = mediaFiles;
		mMediaBrowserHelper = new MediaBrowserHelper(context, AudiobookService.class);
		mMediaBrowserHelper.registerCallback(new PlayerPlaylistMediaControllerCallback());
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

	@Override
	public void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
		getView().setPlaylist(media);
	}

	void onPlaylistItemClick(int itemPosition) {
		mMediaBrowserHelper.getTransportControls().skipToQueueItem(itemPosition);
	}
}
