/*
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova, Viacheslav Tiagotenkov
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skd.androidrecording.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.ViewConfiguration;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RelativeLayout;

/*
 * Controls video playback 
 */

public class VideoPlaybackManager implements SurfaceHolder.Callback,
		OnPreparedListener, MediaPlayerControl, OnCompletionListener {
	private MediaPlayerManager playerManager;
	private MediaController controller;
	private PlaybackHandler playbackHandler;
	private boolean isPlayerPrepared, isSurfaceCreated;
	private AdaptiveSurfaceView videoView;
	private CameraManager cameraManager;

	@SuppressLint("NewApi")
	public VideoPlaybackManager(Context ctx, AdaptiveSurfaceView videoView,
			PlaybackHandler playbackHandler) {
		videoView.getHolder().addCallback(this);

		this.playerManager = new MediaPlayerManager();
		this.playerManager.getPlayer().setOnPreparedListener(this);
		this.playerManager.getPlayer().setOnCompletionListener(this);

		this.playbackHandler = playbackHandler;
		this.controller = new MediaController(ctx);
		this.controller.setMediaPlayer(this);
		this.controller.setAnchorView(videoView);
		this.videoView = videoView;
		this.cameraManager = new CameraManager();

		boolean hasNavigationBar = false;
		boolean hasBackKey = KeyCharacterMap
				.deviceHasKey(KeyEvent.KEYCODE_BACK);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			hasNavigationBar = !ViewConfiguration.get(ctx)
					.hasPermanentMenuKey() && !hasBackKey;
		} else {
			hasNavigationBar = false;
		}
		if (hasNavigationBar) {
			Log.i("PlaybackVideoActivity", "has navigation bar");
			this.controller.setPadding(0, 0, 0, getNavigationBarHeight(ctx));
		}
	}
	

	public  int getNavigationBarHeight(Context ctx) {
        Resources resources = ctx.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

	public void setupPlayback(String fileName) {
		playerManager.setupPlayback(fileName);
	}

	public void showMediaController() {
		if (!controller.isEnabled()) {
			controller.setEnabled(true);
		}
		controller.show();
	}

	public void hideMediaController() {
		controller.hide();
		controller.setEnabled(false);
	}

	public MediaPlayerManager getPlayerManager() {
		return playerManager;
	}

	public void dispose() {
		playerManager.releasePlayer();
		controller = null;
		playbackHandler = null;
	}

	// surface holder callbacks
	// ******************************************************************

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isSurfaceCreated = true;
		playerManager.setDisplay(holder);
		if (isPlayerPrepared && isSurfaceCreated) {
			playbackHandler.onPreparePlayback();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i("VideoPlayBack", "surface changed " + width + " " + height);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				width, height);
		videoView.setLayoutParams(layoutParams);
		// videoView.setPreviewSize(this.cameraManager.getCamera().new
		// Size(width, height));
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		playerManager.setDisplay(null);
	}

	// media player and controller callbacks
	// *****************************************************

	public void onPrepared(MediaPlayer mp) {
		isPlayerPrepared = true;
		if (isPlayerPrepared && isSurfaceCreated) {
			playbackHandler.onPreparePlayback();
		}
	}

	@Override
	public void start() {
		playerManager.startPlaying();
	}

	@Override
	public void pause() {
		playerManager.pausePlaying();
	}

	@Override
	public void seekTo(int arg0) {
		playerManager.seekTo(arg0);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		playerManager.seekTo(0);
	}

	@Override
	public boolean isPlaying() {
		return playerManager.isPlaying();
	}

	@Override
	public int getCurrentPosition() {
		return playerManager.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return playerManager.getDuration();
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}
}
