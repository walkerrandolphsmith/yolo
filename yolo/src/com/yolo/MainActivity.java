package com.yolo;


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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.PushService;
import com.yolo.models.User;


public class MainActivity extends Activity implements LocationListener, CompoundButton.OnCheckedChangeListener {

	/*********************************
	 * Constants and Class members
	 **********************************/
	protected DevicePolicyManager devicePolicyManager;
	private ComponentName mAdminName;
	
	protected boolean isGPSEnabled;
	protected LocationManager locationManager;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 2;
	
	public boolean isDriving;
	
	public static String channel = "PC"; //Public Channel
	
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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
	    	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id){ 
	    	            	   isGPSEnabled = true;
	    	            	   startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	    	               }
	    	           })
	    	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	    	               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	    	            	   isGPSEnabled = false;
	    	                   dialog.cancel();
	    	               }
	    	           });
	    	    final AlertDialog alert = builder.create();
	    	    alert.show();
	    }
	    
	    Switch s = (Switch) findViewById(R.id.isDrivingSwitch);
        if (s != null) {
            s.setOnCheckedChangeListener(this);
        }
        new ParseAsync(this).execute();
  	}
	
	
	
	/*********************************
	 * ActionBar MenuItems
	 **********************************/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_actions, menu);
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
	 * MainActivity Behavior
	 **********************************/

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
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

	/*********************************
	 * Retrieve Location
	 **********************************/

	@Override
	public void onLocationChanged(Location l) {
		
		double speed =  l.getSpeed()*2.2369;
		
		if(speed < 20  && isDriving){		
			if(channel != null){
				ParsePush push = new ParsePush();
				push.setChannel(channel);
				push.setMessage("Notify ME!.");
				push.sendInBackground();
			}
			devicePolicyManager.lockNow();
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
	 * Parse Initialize Async
	 **********************************/
	
	class ParseAsync extends AsyncTask<String, Void, Void> {
	     MainActivity ma;

	     public ParseAsync (MainActivity ma){
	         this.ma= ma;
	     }

	     @Override
	     protected void onPreExecute() {
	    	 super.onPreExecute();
	     }
	     @Override
	     protected Void doInBackground(String... params) {
	    	 Parse.initialize(ma, "yG0OKddCMctN5vtCj5ocUbDxrRJjlPuzZLXMOXA9","FGdSTBZZgOlRTdMkMqSOWydTOG3hliqXigOqm2sk");
	         ParseObject.registerSubclass(User.class);
	         PushService.setDefaultPushCallback(ma, MainActivity.class);
	         ParseInstallation.getCurrentInstallation().saveInBackground();
	         PushService.subscribe(ma, channel, MainActivity.class);
	         return null;
	     }

	     @Override
	     protected void onPostExecute(Void result) {
	    	 super.onPostExecute(result);
	     }
	}
}
