package com.skd.androidrecording.video;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.skd.androidrecording.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;

@SuppressLint("NewApi")
public class CMediaController extends MediaController {
	Context mContext; 
	Button backButton;
	PlaybackHandler p;
	public CMediaController(Context context, PlaybackHandler p) {  
        super(context);  
        mContext = context; 
        this.p = p;
    }  
	
    @Override  
    public void setAnchorView(View view) {  
        super.setAnchorView(view);  
        //this. .requestFitSystemWindows();
        boolean hasNavigationBar = false;
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
            hasNavigationBar = !ViewConfiguration.get(mContext).hasPermanentMenuKey() && !hasBackKey;
        }    
        else 
        {
            hasNavigationBar = false;
        }
        Log.i("CmediaController", "height of navigation bar "+getNavigationBarHeight());
        if (hasNavigationBar) {
        	this.setPadding(0, 0, 0, getNavigationBarHeight());
        }
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(  
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
        frameParams.gravity = Gravity.RIGHT|Gravity.TOP; 
        
        
        View v = makeBackButton();  
        addView(v, frameParams);  
  
    }
    
    public  int getNavigationBarHeight() {
        Resources resources = mContext.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
    
    @SuppressLint("NewApi")
    private int getRealScreenSize(boolean returnWidth) {

        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = this.getDisplay();
        Method mGetRawH = null, mGetRawW = null;

        //Not real dimensions
        display.getMetrics(metrics);
        int width = metrics.heightPixels;
        int height = metrics.widthPixels;

        try {
            // For JellyBeans and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(metrics);

                //Real dimensions
                width = metrics.heightPixels;
                height = metrics.widthPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }

        if (returnWidth) {
            return width;
        } else {
            return height;
        }
    }
    
    private View makeBackButton() {  
    	backButton = new Button(mContext); 
    	backButton.setBackgroundColor(Color.parseColor("#f26175"));
    	backButton.setTextColor(Color.parseColor("#ffffff"));
    	backButton.setText(mContext.getResources().getString(R.string.back));
        //backButton.setImageResource(mContext.getResources().getString(R.string.app_name));  
  
    	backButton.setOnClickListener(new OnClickListener() {  
  
  
            public void onClick(View v) {  
               // p.onBack();
            }  
        });  
  
        return backButton;  
    }  
}
