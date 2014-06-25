package com.yolo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;

import com.parse.ParseUser;
import com.yolo.R;
import com.yolo.SettingsAdapter;
import com.yolo.models.User;

public class SettingsActivity extends Activity {
	
	/*********************************
	 * Constants and Class members
	 **********************************/	
	public User currentUser;

	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		CheckBox chkAll = (CheckBox) findViewById(R.id.selectAllCheckBox);
		currentUser = (User) ParseUser.getCurrentUser();
		String[] list = {"Push Notifications", "Text Messages", "Emails"};

		final ListView mListView = (ListView)findViewById(android.R.id.list);
		SettingsAdapter adapter = new SettingsAdapter(this, R.layout.each_settings, list, chkAll);
		mListView.setAdapter(adapter);
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