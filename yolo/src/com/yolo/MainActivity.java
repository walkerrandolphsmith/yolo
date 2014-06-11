package com.yolo;


import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
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
import android.widget.CompoundButton;
import android.widget.Switch;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.yolo.db.ParentDatabaseHandler;
import com.yolo.models.User;


public class MainActivity extends Activity implements LocationListener, CompoundButton.OnCheckedChangeListener {

	/*********************************
	 * Constants and Class members
	 **********************************/
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 5000;
	
	private ParentDatabaseHandler db;
	
	protected LocationManager locationManager;
	protected SmsManager sms;
	protected DevicePolicyManager devicePolicyManager;
	private ComponentName mAdminName;
	
	public boolean isDriving;
	public static String parentId;
	
	public static class MyAdmin extends DeviceAdminReceiver {
		public void onEnable(){
			System.out.println("onEnable");
		}
		
		public void onDisable(){
			System.out.println("onDisable");
		}
	}

	/*********************************
	 * OnCreate
	 **********************************/

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		db = new ParentDatabaseHandler(this);
		
		devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminName = new ComponentName(this, MyAdmin.class);
		
		 if (!devicePolicyManager.isAdminActive(mAdminName)) {
     		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
     		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mAdminName);
     		startActivityForResult(intent, 1);
     	} 
		
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
	    
	    sms = SmsManager.getDefault();
	    
	    if(NotificationBroadcastReceiver.checkInternetConnection(this)){
        	ParseObject.registerSubclass(User.class);
	    	 Parse.initialize(this, "yG0OKddCMctN5vtCj5ocUbDxrRJjlPuzZLXMOXA9","FGdSTBZZgOlRTdMkMqSOWydTOG3hliqXigOqm2sk");
	         PushService.setDefaultPushCallback(this, MainActivity.class);
	         ParseInstallation install = ParseInstallation.getCurrentInstallation();
	     	 install.put("channels", db.getParents(db.read()));
	         install.saveInBackground(); 
        }
	    
	    Switch s = (Switch) findViewById(R.id.isDrivingSwitch);
        if (s != null) {
            s.setOnCheckedChangeListener(this);
        }
  	}
	
	/*********************************
	 * MainActivity Behavior
	 **********************************/

	@Override
	public void onBackPressed(){
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		//New User signs in to ConsoleActivity and signs out 
		if(parentId != null){
				db.create(parentId);
				parentId = null;
		}
		Log.w("DATABASE: ", db.toString());		
	}
	
	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {		
	}
	
	@Override public void onDestroy(){
		
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
	 * isDriving onCheckedChage Listener
	 **********************************/

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		isDriving = (isChecked ? true : false);	
	}

	/*********************************
	 * Location Change Listener
	 **********************************/

	@Override
	public void onLocationChanged(Location l) {
		
		double speed =  l.getSpeed()*2.2369;
		
		if(isDriving && speed < 20){
			sendNotificationsTo(db.getParents(db.read()));
		}
	}
	
	/*********************************
	 * Get Users &
	 * Send Notifications
	 **********************************/
	
	public void sendNotificationsTo(List<String> parents){
		for(String parent : parents){
			sendNotificationsTo(parent);
		}
	}
	
	public void sendNotificationsTo(String objectId){
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.getInBackground(objectId, new GetCallback<ParseUser>() {
			public void done(ParseUser parseUser, ParseException e) {
				if (e == null) {
					User user = (User) parseUser;
				    if(user.getReceivePushNotifications()){
				    	if(user.getObjectId() != null){
				    		ParsePush push = new ParsePush();
							push.setChannel(user.getObjectId());
							push.setMessage("Yolo Notify via Push Notification.");
							push.sendInBackground();
						}
					}
					if(user.getReceiveEmails()){
						//Send Email
					}
					if(user.getReceiveSMS()){
						if(user.getPhone() != null){
						    sms.sendTextMessage(user.getPhone(), null, "Yolo Notify via Text Message.", null, null);
						}
					}
					devicePolicyManager.lockNow();
			    } 
			}
		});
	}
	
}
