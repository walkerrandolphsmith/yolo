package com.yolo.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.commonsware.cwac.merge.MergeAdapter;
import com.parse.ParseUser;
import com.yolo.ListAdapterSettingsNotifications;
import com.yolo.ListAdapterSettingsAccount;
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

        View header;
		CompoundButton chkAll;
		boolean isFallback;
		int resourceId;
        setContentView(R.layout.activity_settings);
		if(currentSDKVersion >= 14){
			isFallback = false;

			resourceId = R.layout.each_settings_notifications;
            header = getLayoutInflater().inflate(R.layout.listview_header_notifications, null, false);
            chkAll = (Switch) header.findViewById(R.id.selectAllSwitch);
		}else{
			isFallback = true;

			resourceId = R.layout.each_settings_notifications_fallback;
            header = getLayoutInflater().inflate(R.layout.listview_header_notifications_fallback, null, false);
            chkAll = (CheckBox) header.findViewById(R.id.selectAllCheckBox);
        }
		final ListView mListView = (ListView)findViewById(android.R.id.list);
		ListAdapterSettingsNotifications adapter = new ListAdapterSettingsNotifications(this, resourceId, notificationTypes, chkAll, isFallback);
        ListAdapterSettingsAccount accountAdapter = new ListAdapterSettingsAccount(this,R.layout.each_settings_account,new String[]{currentUser.getUsername().toUpperCase(), "Change Password", currentUser.getPhone(), currentUser.getEmail()});

        MergeAdapter mergeAdapter = new MergeAdapter();
        mergeAdapter.addView(header);
        mergeAdapter.addAdapter(adapter);
        mergeAdapter.addView(getLayoutInflater().inflate(R.layout.listview_header_account, null, false));
        mergeAdapter.addAdapter(accountAdapter);


		mListView.setAdapter(mergeAdapter);
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