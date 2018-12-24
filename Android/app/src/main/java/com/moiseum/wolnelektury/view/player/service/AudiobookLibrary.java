/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moiseum.wolnelektury.view.player.service;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.connection.models.MediaModel;
import com.moiseum.wolnelektury.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


public class AudiobookLibrary {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
//    private static final HashMap<String, Integer> albumRes = new HashMap<>();
    private static final HashMap<String, String> musicFileName = new HashMap<>();

    public static String getRoot() {
        return "root";
    }

//    private static String getAlbumArtUri(String albumArtResName) {
//        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
//                BuildConfig.APPLICATION_ID + "/drawable/" + albumArtResName;
//    }

    public static String getMusicFilename(String mediaId) {
        return musicFileName.containsKey(mediaId) ? musicFileName.get(mediaId) : null;
    }

//    private static int getAlbumRes(String mediaId) {
//        return albumRes.containsKey(mediaId) ? albumRes.get(mediaId) : 0;
//    }

//    public static Bitmap getAlbumBitmap(Context context, String mediaId) {
//        return BitmapFactory.decodeResource(context.getResources(),
//                AudiobookLibrary.getAlbumRes(mediaId));
//    }

    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public static MediaMetadataCompat getMetadata(Context context, String mediaId) {
        MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
//        Bitmap albumArt = getAlbumBitmap(context, mediaId);

        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
		                MediaMetadataCompat.METADATA_KEY_TITLE,
		                MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
		                MediaMetadataCompat.METADATA_KEY_AUTHOR,
		                MediaMetadataCompat.METADATA_KEY_GENRE,
		                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER));
	    builder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS));
//        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return builder.build();
    }

	public static void createAudiobookMetadata(BookDetailsModel book) {
    	music.clear();
    	int index = 0;
		List<MediaModel> medias = book.getAudiobookMediaModels();

		for (MediaModel model : medias) {
			createMediaMetadataCompat(
					model.getUrl(),
					model.getName(),
					model.getArtist(),
					book.getTitle(),
					StringUtils.joinCategory(book.getAuthors(), ", "),
					StringUtils.joinCategory(book.getGenres(), ", "),
					book.getCoverThumb(),
					model.getUrl(),
					index++,
					medias.size()
			);
		}
	}

    private static void createMediaMetadataCompat(
            String mediaId,
            String title,
            String artist,
            String album,
			String author,
            String genre,
			String artUrl,
            String musicFilename,
            int trackNumber,
            int tracksCount
    ) {
        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
		                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
		                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
		                .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, author)
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
		                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, artUrl)
//                        .putString(
//                                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
//                                getAlbumArtUri(albumArtResName))
		                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber)
		                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, tracksCount)
                        .build());
//        albumRes.put(mediaId, albumArtResId);
        musicFileName.put(mediaId, musicFilename);
    }
}