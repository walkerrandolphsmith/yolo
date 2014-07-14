package com.yolo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.commonsware.cwac.merge.MergeAdapter;
import com.parse.ParseUser;
import com.yolo.R;
import com.yolo.list_adapters.ListAdapterSettingsAccount;
import com.yolo.list_adapters.ListAdapterSettingsNotifications;
import com.yolo.models.User;

public class SettingsActivity extends BaseActivity {
		
	private final String[] notificationTypes = {"Push Notifications", "Text Messages", "Emails"};
    public ListAdapterSettingsAccount accountAdapter;

    private BroadcastReceiver editPasswordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String password = intent.getStringExtra("password");
            String email = intent.getStringExtra("email");
            String phone = intent.getStringExtra("phone");

            if (!password.isEmpty()) {
                currentUser.setPassword(password);
            }
            if (!email.isEmpty()) {
                currentUser.setEmail(email);
                accountAdapter.settings[3] = email;
            }
            if(!phone.isEmpty()) {
                currentUser.setPhone(phone);
                accountAdapter.settings[2] = phone;
            }
            accountAdapter.notifyDataSetChanged();
            currentUser.saveInBackground();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(editPasswordReceiver, new IntentFilter("com.yolo.action.EDITACCOUNT"));
    }

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
        ListAdapterSettingsNotifications notificationAdapter = new ListAdapterSettingsNotifications(this, resourceId, notificationTypes, chkAll, isFallback);
        accountAdapter = new ListAdapterSettingsAccount(this,R.layout.each_settings_account,new String[]{currentUser.getUsername().toUpperCase(), "Change Password", currentUser.getPhone(), currentUser.getEmail()});

        MergeAdapter mergeAdapter = new MergeAdapter();
        mergeAdapter.addView(header);
        mergeAdapter.addAdapter(notificationAdapter);
        View header2 = getLayoutInflater().inflate(R.layout.listview_header_account, null, false);
        mergeAdapter.addView(header2);
        Button updateAccount = (Button) header2.findViewById(R.id.update_account);
        updateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, EditPasswordActivity.class);
                startActivity(intent);
            }
        });
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