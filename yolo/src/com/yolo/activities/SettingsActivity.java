package com.yolo.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.parse.ParseUser;
import com.yolo.BaseActivity;
import com.yolo.ListAdapterSettings;
import com.yolo.R;
import com.yolo.models.User;

public class SettingsActivity extends BaseActivity {
		
	private final String[] notificationTypes = {"Push Notifications", "Text Messages", "Emails"};

	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentUser = (User) ParseUser.getCurrentUser();

		CompoundButton chkAll;
		boolean isFallback;
		int resourceId;
		if(currentSDKVersion >= 14){
			isFallback = false;
			setContentView(R.layout.activity_settings);
			chkAll = (Switch)findViewById(R.id.selectAllSwitch);
			resourceId = R.layout.each_settings;
		}else{
			isFallback = true;
			setContentView(R.layout.activity_settings_fallback);
			chkAll = (CheckBox) findViewById(R.id.selectAllCheckBox);
			resourceId = R.layout.each_settings_fallback;
		}
		final ListView mListView = (ListView)findViewById(android.R.id.list);
		ListAdapterSettings adapter = new ListAdapterSettings(this, resourceId, notificationTypes, chkAll, isFallback);
		mListView.setAdapter(adapter);
	}
	
	
	/*********************************
	 * ActionBar MenuItems
	 **********************************/

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	onBackPressed();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}