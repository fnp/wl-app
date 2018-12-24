package com.moiseum.wolnelektury.view.player.playlist;

import com.moiseum.wolnelektury.connection.models.MediaModel;

import java.util.List;

/**
 * Created by Piotr Ostrowski on 28.05.2018.
 */
public interface PlayerPlaylistView {

	void setPlaylist(List<MediaModel> item);

	void setCurrentPlaylistItem(int position);
}
