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

import java.io.IOException;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.util.Log;
import android.view.SurfaceHolder;

/*
 * Manages media recorder recording 
 */

public class MediaRecorderManager {
	private static final int VIDEO_W_DEFAULT = 800;
	private static final int VIDEO_H_DEFAULT = 480;
	
	private MediaRecorder recorder;
	private boolean isRecording;

	public MediaRecorderManager() {
		recorder = new MediaRecorder();
	}

	@SuppressLint("NewApi")
	public boolean startRecording(Camera camera, String fileName, Size sz, int cameraRotationDegree, SurfaceHolder v) {
		if (sz == null) {
			sz = camera.new Size(VIDEO_W_DEFAULT, VIDEO_H_DEFAULT);
		}
		
		try {
			Log.i("SIZE", " size is "+sz.width + " "+ sz.height);
			//Camera camera = getCamera();
//			camera.stopPreview();
//			camera.lock();
//			camera.release();
//
//			camera = Camera.open();
			camera.unlock();
			recorder.setCamera(camera);
			recorder.setPreviewDisplay(v.getSurface());
			recorder.setOrientationHint(cameraRotationDegree);
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setVideoSize(sz.width, sz.height);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			recorder.setVideoEncodingBitRate(5000000);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			recorder.setOutputFile(fileName);
			recorder.prepare();
			recorder.start();
			isRecording = true;
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return isRecording;
	}

	public boolean stopRecording() {
		if (isRecording) {
			isRecording = false;
			recorder.stop();
			recorder.reset();
			return true;
		}
		return false;
	}
	
	public MediaRecorder getRecorder() {
		return this.recorder;
	}

	public void releaseRecorder() {
		recorder.release();
		recorder = null;
	}

	public boolean isRecording() {
		return isRecording;
	}
}
