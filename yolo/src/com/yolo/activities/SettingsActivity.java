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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.yolo.R;
import com.yolo.dialogs.EmailVerificationDialog;
import com.yolo.list_adapters.ListAdapterSettingsAccount;
import com.yolo.list_adapters.ListAdapterSettingsNotifications;
import com.yolo.models.User;

import java.util.ArrayList;

public class SettingsActivity extends BaseActivity {
		
	private final String[] notificationTypes = {"Push Notifications", "Text Messages", "Emails"};
    public ListAdapterSettingsAccount accountAdapter;

    private BroadcastReceiver updateAccountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String password = intent.getStringExtra("password");
            String email = intent.getStringExtra("email");
            String phone = intent.getStringExtra("phone");
            try {
                currentUser.refresh();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(currentUser.getEmailVerified()) {
                if (!password.isEmpty()) {
                    currentUser.setPassword(password);
                }
                if (!phone.isEmpty()) {
                    currentUser.setPhone(phone);
                    accountAdapter.settings[2] = phone;
                }
            }
            else{
                showEmailVerificationDialog();
            }

            if (!email.isEmpty()) {
                currentUser.setEmail(email);
                accountAdapter.settings[3] = email;
            }
            accountAdapter.notifyDataSetChanged();
            currentUser.saveInBackground();
        }
    };

    private BroadcastReceiver deleteAccountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            currentUser.deleteInBackground();
            install.put("channels", new ArrayList<String>());
            install.saveInBackground();
            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(i);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(updateAccountReceiver, new IntentFilter("com.yolo.action.EDITACCOUNT"));
        registerReceiver(deleteAccountReceiver, new IntentFilter("com.yolo.action.DELETEACCOUNT"));
    }

	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentUser = (User) ParseUser.getCurrentUser();

        View notificationPreferencesHeader;
		CompoundButton chkAll;
		boolean isFallback;
		int resourceId;
        setContentView(R.layout.activity_settings);
		if(currentSDKVersion >= 14){
			isFallback = false;

			resourceId = R.layout.each_settings_notifications;
            notificationPreferencesHeader = getLayoutInflater().inflate(R.layout.listview_header_notifications, null, false);
            chkAll = (Switch) notificationPreferencesHeader.findViewById(R.id.selectAllSwitch);
		}else{
			isFallback = true;

			resourceId = R.layout.each_settings_notifications_fallback;
            notificationPreferencesHeader = getLayoutInflater().inflate(R.layout.listview_header_notifications_fallback, null, false);
            chkAll = (CheckBox) notificationPreferencesHeader.findViewById(R.id.selectAllCheckBox);
        }
		final ListView mListView = (ListView)findViewById(android.R.id.list);
        ListAdapterSettingsNotifications notificationAdapter = new ListAdapterSettingsNotifications(this, resourceId, notificationTypes, chkAll, isFallback);
        accountAdapter = new ListAdapterSettingsAccount(this,R.layout.each_settings_account,new String[]{currentUser.getUsername().toUpperCase(), "Change Password", currentUser.getPhone(), currentUser.getEmail()});

        MergeAdapter mergeAdapter = new MergeAdapter();
        mergeAdapter.addView(notificationPreferencesHeader);
        mergeAdapter.addAdapter(notificationAdapter);

        View accountHeader = getLayoutInflater().inflate(R.layout.listview_header_account, null, false);
        mergeAdapter.addView(accountHeader);
        Button updateAccount = (Button) accountHeader.findViewById(R.id.update_account);
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

    public void showEmailVerificationDialog(){
        new EmailVerificationDialog(this).show();
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