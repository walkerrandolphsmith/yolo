package com.yolo.activities;


import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.yolo.Application.MyAdmin;
import com.yolo.BaseActivity;
import com.yolo.R;
import com.yolo.models.User;


public class MainActivity extends BaseActivity implements LocationListener {

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 10;
		
	public DevicePolicyManager devicePolicyManager;
	public ComponentName mAdminName;
	public SmsManager sms;
	private LocationManager locationManager;
	private boolean isDriving;	
	
	public boolean  getIsDriving() {
		return isDriving;
	}

	/*********************************
	 * OnCreate
	 **********************************/

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
				
		getActionBar().setDisplayHomeAsUpEnabled(false);
		//Device Policy Manager require minSDK version 8
		devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminName = new ComponentName(this, MyAdmin.class);
		 if (!devicePolicyManager.isAdminActive(mAdminName)) {
     		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
     		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
     		startActivityForResult(intent, 1);
     	} 
		sms = SmsManager.getDefault();
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	    if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
	    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DISTANCE_CHANGE_FOR_UPDATES, MIN_TIME_BW_UPDATES, this);
	    }else{
	    	 final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
	    	           .setCancelable(false)
	    	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	               public void onClick(final DialogInterface dialog, final int id){ 
	    	            	   startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	    	               }
	    	           });
	    	    final AlertDialog alert = builder.create();
	    	    alert.show();
	    }
	    
	    ParseAnalytics.trackAppOpened(getIntent());
	    PushService.setDefaultPushCallback(this, MainActivity.class);
		
		if(install.getObjectId() != null)
			install.addUnique("channels", app.DEVICE_CHANNEL + install.getObjectId());
		
		if(currentSDKVersion >= 14){
			setContentView(R.layout.activity_main);
			  CompoundButton s = (Switch) findViewById(R.id.isDrivingSwitch);
		        if (s != null) {
		            s.setOnCheckedChangeListener(new isDrivingCheckedChagedListener());
		        }
		}else{
			setContentView(R.layout.activity_main_fallback);
			 CompoundButton s = (ToggleButton) findViewById(R.id.isDrivingToggleButton);
		        if (s != null) {
		            s.setOnCheckedChangeListener(new isDrivingCheckedChagedListener());
		            s.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) { /*ignore*/ }
		            });
		        }
		}
  	}
	
	/*********************************
	 * isDriving onCheckedChaged Listener
	 **********************************/
	private class isDrivingCheckedChagedListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			isDriving = (isChecked ? true : false);	
		}
	}
	
	
	/*********************************
	 * MainActivity Behavior
	 **********************************/
	
	@Override
	public void onProviderDisabled(String disabled) {
	}

	@Override
	public void onProviderEnabled(String enabled) {		
	}

	@Override
	public void onStatusChanged(String changed, int i, Bundle bundle) {		
	}
	
	/*********************************
	 * ActionBar MenuItems
	 **********************************/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main_menu_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_signin:
	        	Intent intent = new Intent(MainActivity.this, SignInActivity.class);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/*********************************
	 * Location Change Listener
	 **********************************/

	@Override
	public void onLocationChanged(Location l) {
		
		double speed =  l.getSpeed()*2.2369;
		
		if(speed < 20){
			Log.w("speed", "is moving at " + speed + " mph");
			JSONArray channels = install.getJSONArray("channels");
			for(int i = 0; i < channels.length(); i++){
				try {
					String channel = channels.getString(i);
					Log.w(channel, "channel: "+i);
					if(channel.startsWith(app.PARENT_CHANNEL)){
						sendNotificationsTo(channel);
					}
				} catch (JSONException e) {
					Log.w("Exception Caught", "Channels could not be retreived from install");
				}
			}
			if(isDriving)
				devicePolicyManager.lockNow();
		}
	}
	
	/*********************************
	 * Get Users 
	 **********************************/
	
	public void sendNotificationsTo(String channel){
		String objectId = channel.replace(app.PARENT_CHANNEL, "");
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.getInBackground(objectId, new GetCallback<ParseUser>() {
			public void done(ParseUser parseUser, ParseException e) {
				if (e == null) {
					sendNotificationsToCallback(parseUser);
			    }else{
			    	Log.w("ParseUser Exception", e.getLocalizedMessage());
			    }
			}
		});
	}
	
	/*********************************
	 * Send Notifications to Parents
	 **********************************/
	
	public void sendNotificationsToCallback(ParseUser parseUser){
		User user = (User) parseUser;
		String message = constructMessage();
	    if(user.getReceivePushNotifications()){
	    	if(user.getObjectId() != null){
	    		ParsePush push = new ParsePush();
				push.setChannel(app.PARENT_CHANNEL + user.getObjectId());
				push.setMessage(message);
				push.sendInBackground();
			}
		}
		if(user.getReceiveEmails()){
			//Send Email
		}
		if(user.getReceiveSMS()){
			if(user.getPhone() != null){
				sms.sendTextMessage(user.getPhone(), "", message, null, null);
			}
		}
		
	}
	
	/*********************************
	 * Construct Notification Message 
	 **********************************/

	public String constructMessage() {
		String message = "Your child is ";
		if(isDriving){
			message += "driving a vehicle.";
		}else{
			message += "a passenger in a moving vehicle";
		}
		return message;
	}
}
