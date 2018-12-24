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
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.moiseum.wolnelektury.connection.downloads.FileCacheUtils;

/**
 * Exposes the functionality of the {@link MediaPlayer} and implements the {@link PlayerAdapter}
 * so that MainActivity can control music playback.
 */
public final class MediaPlayerAdapter extends PlayerAdapter {

	private class MediaPlayerException extends Exception {

		private final int what;
		private final int extra;

		public MediaPlayerException(int what, int extra) {
			this.what = what;
			this.extra = extra;
		}

		@Override
		public String getMessage() {
			return "Media player failed with code (" + what + "," + extra + ")";
		}
	}

	private static final String TAG = MediaPlayerAdapter.class.getSimpleName();
	private static final int FIVE_SECONDS = 5000;
	private static final int CALLBACK_INTERVAL = 400;

    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private String mFilename;
    private PlaybackInfoListener mPlaybackInfoListener;
    private MediaMetadataCompat mCurrentMedia;
    private int mState;
    private boolean mCurrentMediaPlayedToCompletion;

	private Handler handler = new Handler();
	private Runnable positionUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			mPlaybackInfoListener.onPlaybackProgress(mMediaPlayer.getCurrentPosition());
			handler.postDelayed(this, CALLBACK_INTERVAL);
		}
	};

    // Work-around for a MediaPlayer bug related to the behavior of MediaPlayer.seekTo()
    // while not playing.
    private int mSeekWhileNotPlaying = -1;

    MediaPlayerAdapter(Context context, PlaybackInfoListener listener) {
        super(context);
        mContext = context.getApplicationContext();
        mPlaybackInfoListener = listener;
    }

    /**
     * Once the {@link MediaPlayer} is released, it can't be used again, and another one has to be
     * created. In the onStop() method of the MainActivity the {@link MediaPlayer} is
     * released. Then in the onStart() of the MainActivity a new {@link MediaPlayer}
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */
    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                mPlaybackInfoListener.onPlaybackCompleted();

                // Set the state to "paused" because it most closely matches the state
                // in MediaPlayer with regards to available state transitions compared
                // to "stop".
                // Paused allows: seekTo(), start(), pause(), stop()
                // Stop allows: stop()
                setNewState(PlaybackStateCompat.STATE_PAUSED);
            });
            mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
	            Log.e(TAG, "Media player experienced failure: " + what + ", " + extra);
            	setNewState(PlaybackStateCompat.STATE_ERROR);
	            return false;
            });
        }
    }

    // Implements PlaybackControl.
    @Override
    public void playFromMedia(MediaMetadataCompat metadata) {
        mCurrentMedia = metadata;
        final String mediaId = metadata.getDescription().getMediaId();
        playFile(AudiobookLibrary.getMusicFilename(mediaId));
    }

    @Override
    public MediaMetadataCompat getCurrentMedia() {
        return mCurrentMedia;
    }

    private void playFile(String filename) {
        boolean mediaChanged = (mFilename == null || !filename.equals(mFilename));
        if (mCurrentMediaPlayedToCompletion) {
            // Last audio file was played to completion, the resourceId hasn't changed, but the
            // player was released, so force a reload of the media file for playback.
            mediaChanged = true;
            mCurrentMediaPlayedToCompletion = false;
        }
        if (!mediaChanged) {
            if (!isPlaying()) {
                play();
            }
            return;
        } else {
            release();
        }

        mFilename = filename;

        initializeMediaPlayer();

        try {
            String cachedFileForUrl = FileCacheUtils.getCachedFileForUrl(mFilename);
            mMediaPlayer.setDataSource(cachedFileForUrl);
            mMediaPlayer.prepare();
            mPlaybackInfoListener.onPlaybackPrepared(mMediaPlayer.getDuration());
        } catch (Exception e) {
        	Log.e(TAG, "Failed to load file: " + mFilename, e);
        	setNewState(PlaybackStateCompat.STATE_ERROR);
        }

        play();
    }

    @Override
    public void onStop() {
        // Regardless of whether or not the MediaPlayer has been created / started, the state must
        // be updated, so that MediaNotificationManager can take down the notification.
        setNewState(PlaybackStateCompat.STATE_STOPPED);
        release();
    }

    private void release() {
        if (mMediaPlayer != null) {
	        handler.removeCallbacks(positionUpdateRunnable);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    protected void onPlay() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
	        handler.postDelayed(positionUpdateRunnable, CALLBACK_INTERVAL);
            setNewState(PlaybackStateCompat.STATE_PLAYING);
        }
    }

    @Override
    protected void onPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
	        handler.removeCallbacks(positionUpdateRunnable);
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }
    }

    // This is the main reducer for the player state machine.
    private void setNewState(@PlaybackStateCompat.State int newPlayerState) {
        mState = newPlayerState;
        if (mState == PlaybackStateCompat.STATE_ERROR) {
        	handler.removeCallbacks(positionUpdateRunnable);
        	mFilename = null;
        }

        // Whether playback goes to completion, or whether it is stopped, the
        // mCurrentMediaPlayedToCompletion is set to true.
        if (mState == PlaybackStateCompat.STATE_STOPPED) {
            mCurrentMediaPlayedToCompletion = true;
        }

        // Work around for MediaPlayer.getCurrentPosition() when it changes while not playing.
        final long reportPosition;
        if (mSeekWhileNotPlaying >= 0) {
            reportPosition = mSeekWhileNotPlaying;

            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                mSeekWhileNotPlaying = -1;
            }
        } else {
            reportPosition = mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
        }

        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(mState,
                              reportPosition,
                              1.0f,
                              SystemClock.elapsedRealtime());
        if (mState == PlaybackStateCompat.STATE_PLAYING) {
	        Bundle extras = new Bundle();
	        extras.putInt(AudiobookService.EXTRA_PLAYBACK_TOTAL, mMediaPlayer.getDuration());
        	stateBuilder.setExtras(extras);
        }
        mPlaybackInfoListener.onPlaybackStateChange(stateBuilder.build());
    }

    /**
     * Set the current capabilities available on this session. Note: If a capability is not
     * listed in the bitmask of capabilities then the MediaSession will not handle it. For
     * example, if you don't want ACTION_STOP to be handled by the MediaSession, then don't
     * included it in the bitmask that's returned.
     */
    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                       | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                       | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                       | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        switch (mState) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                           | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                           | PlaybackStateCompat.ACTION_PAUSE
                           | PlaybackStateCompat.ACTION_SEEK_TO;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                           | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                           | PlaybackStateCompat.ACTION_PLAY_PAUSE
                           | PlaybackStateCompat.ACTION_STOP
                           | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    @Override
    public void seekTo(long position) {
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mSeekWhileNotPlaying = (int) position;
            }
            mMediaPlayer.seekTo((int) position);

            // Set the state (to the current state) because the position changed and should
            // be reported to clients.
            setNewState(mState);
        }
    }

    @Override
    public void fastForward() {
	    if (mMediaPlayer != null) {
	    	int seekTo = mMediaPlayer.getCurrentPosition() + FIVE_SECONDS;
	    	int newState = mState;

	    	if (seekTo > mMediaPlayer.getDuration()) {
	    		seekTo = mMediaPlayer.getDuration();
	    		newState = PlaybackStateCompat.STATE_PAUSED;
	    		mMediaPlayer.pause();
		    }

		    mMediaPlayer.seekTo(seekTo);
		    setNewState(newState);
	    }
    }

    @Override
    public void rewind() {
	    if (mMediaPlayer != null) {
		    int seekTo = mMediaPlayer.getCurrentPosition() - FIVE_SECONDS;
		    int newState = mState;

		    if (seekTo < 0) {
			    seekTo = 0;
			    newState = PlaybackStateCompat.STATE_PAUSED;
			    mMediaPlayer.pause();
		    }

		    mMediaPlayer.seekTo(seekTo);
		    setNewState(newState);
	    }
    }

    @Override
    public void setVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }
}
