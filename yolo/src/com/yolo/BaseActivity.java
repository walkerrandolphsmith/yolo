package com.yolo;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;

import com.parse.ParseInstallation;
import com.yolo.models.User;

public class BaseActivity extends Activity {
	
	public int currentSDKVersion;	
	public Application app;
	public FragmentManager fragmentManager;
	public ParseInstallation install;
	public User currentUser;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		currentSDKVersion = android.os.Build.VERSION.SDK_INT;	
		app = (Application)getApplication();
		fragmentManager = getFragmentManager();
		install = ParseInstallation.getCurrentInstallation();
		
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
	
	@Override public void onDestroy(){
		super.onDestroy();
	}
}
