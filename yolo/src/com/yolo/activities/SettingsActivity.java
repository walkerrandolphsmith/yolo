package com.yolo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.yolo.R;
import com.yolo.list_adapters.ListAdapterSettingsAccount;
import com.yolo.list_adapters.ListAdapterSettingsNotifications;
import com.yolo.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

            if (!password.isEmpty()) {
                currentUser.setPassword(password);
            }
            if (!phone.isEmpty()) {
                currentUser.setPhone(phone);
                accountAdapter.settings[2] = phone;
            }

            if (!email.isEmpty()) {
                currentUser.setEmail(email);
                accountAdapter.settings[3] = email;
            }
            else if(!currentUser.getEmailVerified()){
                currentUser.setEmail(currentUser.getEmail());
                currentUser.saveInBackground();
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

    @Override
    public void onPause()
    {
        super.onPause();
        //unregisterReceiver(updateAccountReceiver);
        //unregisterReceiver(deleteAccountReceiver);
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

        View reminderFrequencyHeader = getLayoutInflater().inflate(R.layout.listview_header_reminder_freq, null, false);

        final TextView intervalTextView = (TextView) reminderFrequencyHeader.findViewById(R.id.interval);
        final HashMap<Integer, String> termMap = new HashMap<Integer, String>();
        termMap.put(900000, "15 mins");
        termMap.put(1800000, "30 mins");
        termMap.put(3600000, "1 hr");
        termMap.put(3600000*2, "2 hrs");
        for(int i = 4; i <= 17; i++){
            termMap.put(3600000*i, i + " hrs");
        }
        termMap.put(3600000*24, "Daily");
        termMap.put(3600000*48, "2 days");
        termMap.put(3600000*168, "Weekly");

        int initialFrequency = currentUser.getReminderFrequency();
        intervalTextView.setText(termMap.get(initialFrequency));

        SeekBar slider = (SeekBar) reminderFrequencyHeader.findViewById(R.id.frequency_slider);
        int[] keys = new int[21];
        int counter = 0;
        for (Map.Entry<Integer, String> entry : termMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            keys[counter] = entry.getKey();
            counter++;
        }
        Arrays.sort(keys);
        for(int i = 0; i < keys.length; i++){
            if(keys[i] == initialFrequency){
                slider.setProgress(i);
            }
        }

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.w("onProgress Changed", "Changed by " + i + " amount.");
                int hour = 3600000;
                int frequency;

                switch(i){
                    case 0:
                        frequency = 900000;//Once every 15 minutes
                        break;
                    case 1:
                        frequency = 1800000;//Once every 30 minutes
                        break;
                    case 2:
                        frequency = hour;//Once an hour
                        break;
                    case 3:
                        frequency = hour * 2;//Once every 2 hours
                        break;
                    case 18:
                        frequency = hour * 24;//Once day
                        break;
                    case 19:
                        frequency = hour * 48;//Once every other day
                        break;
                    case 20:
                        frequency = hour * 168;//Once week
                        break;
                     default:
                        frequency = hour * i; //Once every i hours
                         break;

                }
                intervalTextView.setText(termMap.get(frequency));
                currentUser.setReminderFrequency(frequency);
                currentUser.saveInBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mergeAdapter.addView(reminderFrequencyHeader);

        View accountHeader = getLayoutInflater().inflate(R.layout.listview_header_account, null, false);
        mergeAdapter.addView(accountHeader);
        Button updateAccount = (Button) accountHeader.findViewById(R.id.update_account);
        updateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, UpdateAccountActivity.class);
                intent.putExtra("isVerified", currentUser.getEmailVerified());
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