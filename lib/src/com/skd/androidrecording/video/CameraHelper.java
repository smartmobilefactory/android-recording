/*
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova, Viacheslav Tyagotenkov
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

import java.util.List;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Build;
import android.view.Surface;

/*
 * Represents camera management helper class.
 * Holds method for setting camera display orientation. 
 */

public class CameraHelper {

	public static int getAvailableCamerasCount() {
		return Camera.getNumberOfCameras();
	}
	
	public static int getDefaultCameraID() {
		int camerasCnt = getAvailableCamerasCount();
		int defaultCameraID = -1;
		CameraInfo cameraInfo = new CameraInfo();
        for (int i=0; i <camerasCnt; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
            	defaultCameraID = i;
            }
        }
        return defaultCameraID;
	}
	
	public static boolean isCameraFacingBack(int cameraID) {
		CameraInfo cameraInfo = new CameraInfo();
		Camera.getCameraInfo(cameraID, cameraInfo);
		return (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK);
	}
	
	@SuppressLint("NewApi")
	public static List<Size> getCameraSupportedVideoSizes(android.hardware.Camera camera) {
		
		List sizes;
		
		if ((Build.VERSION.SDK_INT >= 11) && (camera != null)) {
			if (camera.getParameters().getSupportedVideoSizes() != null) {
				sizes = camera.getParameters().getSupportedVideoSizes();
			}
			else {
				// Video sizes may be null, which indicates that all the supported 
				// preview sizes are supported for video recording.
				sizes = camera.getParameters().getSupportedPreviewSizes();
			}
			return sizes;
		}
		else {
			return null;
		}
	}
	
	public static List<Size> getCameraSupportedPreviewSizes(android.hardware.Camera camera) {
		List<Size> sizes = camera.getParameters().getSupportedPreviewSizes();
		return sizes;
	}
	
	public static List<Size> getCameraSupportedPictureSizes(android.hardware.Camera camera) {
		
		if ((Build.VERSION.SDK_INT >= 5) && (camera != null)) {
			if (camera.getParameters().getSupportedPictureSizes() != null) {
				return camera.getParameters().getSupportedPictureSizes();
			}
		}

		return null;
	}
	
	public static int setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera, int displayRotation) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int degrees = 0;
		switch (displayRotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		int camRotationDegree = 0;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			camRotationDegree = (info.orientation + degrees) % 360;
			camRotationDegree = (360 - camRotationDegree) % 360; // compensate the mirror
		} else { 
			camRotationDegree = (info.orientation - degrees + 360) % 360;
		}

		if (camera != null) {
			camera.setDisplayOrientation(camRotationDegree);
		}
		return camRotationDegree;
	}
}
