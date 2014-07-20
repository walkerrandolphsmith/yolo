package com.yolo.activities;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.yolo.list_adapters.ListAdapterSettingsNotifications;
import com.yolo.models.User;

import java.util.ArrayList;

public class SettingsActivity extends BaseActivity {

    /* Milliseconds per unit */
    private static int second = 1000;
    private static int minute = 6000; //60 * second;
    private static int quarter_hour = 90000; //15 * minute
    private static int half_hour = 180000; //30 * minute
    private static int hour = 360000; //60 * minute;
    private static int half_day = 4320000; //12 * hour
    private static int day = 8640000; //24 * hour;
    private static int week = 60480000; //7 * day;
    private static final int[] milis = new int[] { quarter_hour, half_hour, hour, hour*2, hour*3, hour*4, hour*6, hour*8, hour*10, half_day, hour*15, hour*18, hour*20, hour*21, hour*22, hour*23, day, day*2, day*3, day*5, week };
    private static final String[] milis_phrases = new String[] { "15 minutes", "30 minutes", "1 hour", "2 hours", "3 hours","4 hours", "6 hours","8 hours", "10 hours","12 hours", "15 hours","18 hours", "20 hours","21 hours", "22 hours","23 hours", "Day", "2 Days", "3 Days", "5 Days", "Week"};

    private TextView phoneTextView,emailTextView, emailVerifiedTextView;

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
		if(currentSDKVersion >= REQUIRE_SDK_14){
			isFallback = false;

			resourceId = R.layout.each_settings_notifications;
            notificationPreferencesHeader = getLayoutInflater().inflate(R.layout.settings_notifications, null, false);
            chkAll = (Switch) notificationPreferencesHeader.findViewById(R.id.selectAllSwitch);
		}else{
			isFallback = true;

			resourceId = R.layout.each_settings_notifications_fallback;
            notificationPreferencesHeader = getLayoutInflater().inflate(R.layout.settings_notifications_fallback, null, false);
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
        View reminderFrequencyHeader = getLayoutInflater().inflate(R.layout.settings_frequency_reminder, null, false);
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
        View accountHeader = getLayoutInflater().inflate(R.layout.settings_account, null, false);

        TextView usernameTextView = (TextView)accountHeader.findViewById(R.id.username);
        phoneTextView = (TextView)accountHeader.findViewById(R.id.phone);
        emailTextView = (TextView)accountHeader.findViewById(R.id.email);
        emailVerifiedTextView = (TextView)accountHeader.findViewById(R.id.verifiedText);

        usernameTextView.setText(currentUser.getUsername());
        phoneTextView.setText(currentUser.getPhone());
        emailTextView.setText(currentUser.getEmail());
        if(currentUser.getEmailVerified()) {
            emailVerifiedTextView.setText(getResources().getString(R.string.emailVerified));
            emailVerifiedTextView.setTextColor(getResources().getColor(R.color.yolotheme_green));
        }else {
            emailVerifiedTextView.setText(getResources().getString(R.string.emailNotVerified));
            emailVerifiedTextView.setTextColor(getResources().getColor(R.color.yolotheme_red));
        }
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
        /* End Account Settings */



        boolean isUpdated = getIntent().getBooleanExtra("updated", false);
        if(isUpdated){
            updateAccount(phoneTextView, emailTextView);
        }
        boolean isDeleted = getIntent().getBooleanExtra("deleted", false);
        if(isDeleted){
            deleteAccount();
        }
        mListView.setAdapter(mergeAdapter);
	}

    /*********************************
     * Update Account
     **********************************/

    public void deleteAccount(){
        new DeleteUserTask().execute();
        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void updateAccount(TextView phoneTextView, TextView emailTextView){
        String password = getIntent().getStringExtra("password");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");

        new UpdateUserTask(this).execute(new String[]{password, email, phone});
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


    /*********************************
     * Async Task Delete User
     **********************************/

    public class DeleteUserTask extends  AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void ... config) {
            currentUser.deleteInBackground();
            getApp().getInstall().put("channels", new ArrayList<String>());
            getApp().getInstall().saveInBackground();
            return null;
        }
    }


    /*********************************
     * Async Task Update User
     **********************************/

    private class UpdateUserTask extends AsyncTask<String , Void, String[]> {

        private SettingsActivity activity;

        public UpdateUserTask(SettingsActivity activity){
            this.activity = activity;
        }

        @Override
        protected String[] doInBackground(String ... config) {
            String password = config[0];
            String email = config[1];
            String phone = config[2];

            try {
                currentUser.refresh();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (!password.isEmpty()) {
                currentUser.setPassword(password);
                currentUser.saveInBackground();
            }
            if (!phone.isEmpty()) {
                currentUser.setPhone(phone);
                currentUser.saveInBackground();
            }

            if (!email.isEmpty()) {
                currentUser.setEmail(email);
                currentUser.saveInBackground();
            }
            else if(!currentUser.getEmailVerified()){
                currentUser.setEmail(currentUser.getEmail());
                currentUser.saveInBackground();
            }
            return config;
        }

        protected void onPostExecute(String[] config){
            String email = config[1];
            String phone = config[2];

            if (!phone.isEmpty()) {
                phoneTextView.setText(phone);
            }

            if (!email.isEmpty()) {
                emailTextView.setText(email);
            }

        }


    }
}