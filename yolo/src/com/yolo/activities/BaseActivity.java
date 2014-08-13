package com.yolo.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import com.yolo.Application;
import com.yolo.models.User;

public class BaseActivity extends Activity {

    public static final int REQUIRE_SDK_14 = 14;

	public int currentSDKVersion;	
	private Application app;
	public User currentUser;

    public Application getApp(){
        return app;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		currentSDKVersion = android.os.Build.VERSION.SDK_INT;
        app = (Application)getApplication();
        getApp().setContext(this);

		Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
        	
        }else if(config.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE)){
        	
        }else if(config.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_NORMAL)){
        	
        }else if(config.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_SMALL)){
        	
        }
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/*********************************
	 * SettingsActivity Behavior
	 **********************************/

	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();		
	}
	
	@Override
    public void onDestroy(){
		super.onDestroy();
	}
}
