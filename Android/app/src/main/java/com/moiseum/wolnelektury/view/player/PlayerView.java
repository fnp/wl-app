package com.moiseum.wolnelektury.view.player;

/**
 * Created by Piotr Ostrowski on 22.05.2018.
 */
interface PlayerView {

	void setTrackDuration(int trackDuration, String totalProgress);

	void setTrackPosition(int position, String currentProgress);

	void setTrackTexts(String title, int chapter);

	void setPlayButtonState(boolean playing);

	void onPlayerError();
}
