package com.vmd.webViewControl.motionDetection;

import com.vmd.webViewControl.motionDetection.image.AndroidImage;
import com.vmd.webViewControl.motionDetection.image.AndroidImageFactory;
import com.vmd.webViewControl.motionDetection.image.Size;
import android.content.SharedPreferences;
import android.util.Log;


/* TODO analyse the scene and automatically set the values for the motion detection */
/* TODO evaluate performance, consider using NDK for optimal speed */
public class MotionDetection {

	private static final String TAG = "MotionDetection";

	/* File storing motion detection's preferences */
	public static final String PREFS_NAME = "prefs_md";
	
	// Control the threshold above which two pixels are considered different 25 means 10% pixel difference 
	private static final KeyValue<String,Integer> mPixelThreshold = new KeyValue<String,Integer>("pim.md.pixel_threshold", 25);//2);
	
	// Control the threshold above which two images are considered different 9216 = 3% of a 640x480 image 
	private static final KeyValue<String,Integer> mThreshold = 	new KeyValue<String,Integer>("pim.md.threshold", 9216);//);
	
	// Control the erosion level to perform (unused)
	private static final KeyValue<String,Integer> mErosionLevel = new KeyValue<String,Integer>("pim.md.erosion_level", 10);

	// Percentage of pixels of the new image to be merged  with the background  (unused)*/
	private static final KeyValue<String,Integer> mMorphLevel = new KeyValue<String, Integer>("pim.md.morph_level", 80);
	
	private static final KeyValue<String,Size<Integer,Integer>> mSize = new KeyValue<String,Size<Integer,Integer>>("pim.md.size", new Size<Integer,Integer>(640,480)); 
	
	/* The format of the preview frame */
	private static final KeyValue<String,Integer> mPixelFormat = new KeyValue<String, Integer>("pim.md.pixel_format", AndroidImageFactory.IMAGE_FORMAT_NV21);
	
	// Background image
	private AndroidImage mBackground;

	// The image that is used for motion detection
	private AndroidImage mAndroidImage;

	private int mPixelDifferent = 0; 
	
	private SharedPreferences mPrefs;
	
	private com.vmd.webViewControl.ActivityMain.motionDetectCallback mCallback;
		
	public MotionDetection(SharedPreferences prefs, com.vmd.webViewControl.ActivityMain.motionDetectCallback callback) {
		mCallback = callback;
		mPrefs = prefs;
		mPixelThreshold.value = mPrefs.getInt(mPixelThreshold.key, mPixelThreshold.value);
		mThreshold.value = mPrefs.getInt(mThreshold.key, mThreshold.value);
		mErosionLevel.value = mPrefs.getInt(mErosionLevel.key, mErosionLevel.value);
		mMorphLevel.value = mPrefs.getInt(mMorphLevel.key, mMorphLevel.value);
		mPixelFormat.value = mPrefs.getInt(mPixelFormat.key, mPixelFormat.value);
	}

	public boolean detect(byte[] data) {
		if(mBackground == null) {
			mBackground = AndroidImageFactory.createImage(data, mSize.value, mPixelFormat.value);//.erode(mErosionLevel.value);
			Log.i(TAG, "Creating background image");
			return false;
		}
		
		boolean motionDetected = false;
		
		// TODO avoid creating every time a new image, reuse an existing one
		mAndroidImage = AndroidImageFactory.createImage(data, mSize.value, mPixelFormat.value);
		
		int pixelDifferent = mAndroidImage.isDifferent(mBackground, mPixelThreshold.value, mThreshold.value);
		motionDetected = pixelDifferent > mPixelThreshold.value;

		int percentDifferent = (100 / ( 307200 / pixelDifferent));
		if (mPixelDifferent != percentDifferent) {
			mPixelDifferent = percentDifferent;
			mCallback.motionDetect(percentDifferent);

			Log.i(TAG, "Image is different ? " + motionDetected + "Number of different pixels: " + pixelDifferent + " -> " + percentDifferent + "%");
		}
		
		mBackground = mAndroidImage;
		return motionDetected;
	}
}