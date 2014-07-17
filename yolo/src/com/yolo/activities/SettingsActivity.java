package com.yolo.activities;

import android.content.Intent;
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

public class SettingsActivity extends BaseActivity {
		
    private static int hour = 3600000;
    private static final int[] milis = new int[] { 900000, 1800000, hour, hour*4, hour*5, hour*6, hour*7, hour*8, hour*9, hour*10, hour*11, hour*12, hour*13, hour*14, hour*15, hour*16, hour*17, hour*18, hour*24, hour*48, hour*168 };
    private static final String[] milis_phrases = new String[] { "15 mins", "30 mins", "1 hr", "4 hrs", "5 hrs","6 hrs", "7 hrs","8 hrs", "9 hrs","10 hrs", "11 hrs","12 hrs", "13 hrs","14 hrs", "15 hrs","16 hrs", "17 hrs", "18 hrs", "Daily", "2 days", "Weekly"};

	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentUser = (User) ParseUser.getCurrentUser();
        MergeAdapter mergeAdapter = new MergeAdapter();

        /*
            Notification Preferences
         */
        View notificationPreferencesHeader;
		CompoundButton chkAll;
		boolean isFallback;
		int resourceId;
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
        ListAdapterSettingsNotifications notificationAdapter = new ListAdapterSettingsNotifications(this, resourceId,new String[] {"Push Notifications", "Text Messages", "Emails"}, chkAll, isFallback);

        mergeAdapter.addView(notificationPreferencesHeader);
        mergeAdapter.addAdapter(notificationAdapter);
        /* End Notification Preferences */


         /*
            Reminder Frequency
         */
        View reminderFrequencyHeader = getLayoutInflater().inflate(R.layout.listview_header_reminder_freq, null, false);
        SeekBar slider = (SeekBar) reminderFrequencyHeader.findViewById(R.id.frequency_slider);

        final TextView intervalTextView = (TextView) reminderFrequencyHeader.findViewById(R.id.interval);
        int initialFrequency = currentUser.getReminderFrequency();

        int testIndex = 0;
        for(int i = 0; i < milis.length; i++){
            if(initialFrequency == milis[i]){
                testIndex = i;
            }
        }
        intervalTextView.setText(milis_phrases[testIndex]);
        slider.setProgress(testIndex);

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.w("Swipe is in position ", i+"");
                intervalTextView.setText(milis_phrases[i]);
                currentUser.setReminderFrequency(milis[i]);
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
        /* End Reminder Frequency */

        /*
            Account Settings
         */
        ListAdapterSettingsAccount accountAdapter = new ListAdapterSettingsAccount(this,R.layout.each_settings_account,new String[]{currentUser.getUsername().toUpperCase(), "*****", currentUser.getPhone(), currentUser.getEmail()});

        boolean isUpdated = getIntent().getBooleanExtra("updated", false);
        if(isUpdated){
            updateAccount(accountAdapter);
        }
        boolean isDeleted = getIntent().getBooleanExtra("deleted", false);
        if(isDeleted){
            deleteAccount();
        }

        View accountHeader = getLayoutInflater().inflate(R.layout.listview_header_account, null, false);
        Button updateAccount = (Button) accountHeader.findViewById(R.id.update_account);
        updateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, UpdateAccountActivity.class);
                intent.putExtra("isVerified", currentUser.getEmailVerified());
                startActivity(intent);
            }
        });
        mergeAdapter.addView(accountHeader);
        mergeAdapter.addAdapter(accountAdapter);
        /* End Account Settings */

		mListView.setAdapter(mergeAdapter);
	}

    public void deleteAccount(){
        currentUser.deleteInBackground();
        app.getInstall().put("channels", new ArrayList<String>());
        app.getInstall().saveInBackground();
        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void updateAccount(ListAdapterSettingsAccount accountAdapter){
        String password = getIntent().getStringExtra("password");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
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