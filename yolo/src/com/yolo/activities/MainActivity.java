package com.yolo.activities;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.yolo.R;
import com.yolo.dialogs.NoGpsDialog;
import com.yolo.models.User;

import org.json.JSONArray;
import org.json.JSONException;


public class MainActivity extends BaseActivity {
		
	public boolean isDriving;

    private BroadcastReceiver locationChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.w("responded to location intent", "intent");
            MainActivity.this.locationChanged();
        }
    };

    private BroadcastReceiver remoteLockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.w("responded to remote lock intent", "intent");
            MainActivity.this.lock();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(locationChangedReceiver, new IntentFilter("com.yolo.action.LOCATIONCHANGECONFIRM"));
        registerReceiver(remoteLockReceiver, new IntentFilter("com.yolo.action.REMOTELOCKCONFIRM"));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //unregisterReceiver(remoteLockReceiver);
        //unregisterReceiver(locationChangedReceiver);
    }


    /*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().hide();
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, MainActivity.class);

		 if (!app.getDevicePolicyManager().isAdminActive(app.getAdminName())) {
     		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
     		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, app.getAdminName());
     		startActivityForResult(intent, 1);
     	} 
	    if(app.getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent location_intent = new Intent("com.yolo.action.LOCATIONCHANGE");
            PendingIntent launchIntent = PendingIntent.getBroadcast(this, 0, location_intent, 0);
            //provider string, min time between, min distance change, intent
            app.getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, launchIntent);
        }else{
           new NoGpsDialog(this).show();
	    }
		
		install.addUnique("channels", app.DEVICE_CHANNEL + install.getObjectId());
		
		if(currentSDKVersion >= 14){
			setContentView(R.layout.activity_main);
			  CompoundButton s = (Switch) findViewById(R.id.isDrivingSwitch);
		        if (s != null) {
		            s.setOnCheckedChangeListener(new isDrivingCheckedChangedListener());
		        }
		}else{
			setContentView(R.layout.activity_main_fallback);
			 CompoundButton s = (ToggleButton) findViewById(R.id.isDrivingToggleButton);
		        if (s != null) {
		            s.setOnCheckedChangeListener(new isDrivingCheckedChangedListener());
		            s.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) { /*ignore*/ }
		            });
		        }
		}

        TextView signIn = (TextView) findViewById(R.id.signIn);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
  	}

	/*********************************
	 * isDriving onCheckedChaged Listener
	 **********************************/
	private class isDrivingCheckedChangedListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			isDriving = (isChecked ? true : false);	
		}
	}

    /*********************************
     * locationChanged
     **********************************/

    public void locationChanged() {
        Location location = app.getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            double speed = location.getSpeed() * 2.2369;

            if (speed < 20) {
                Log.w("speed", "is moving at " + speed + " mph");
                JSONArray channels = install.getJSONArray("channels");
                for (int i = 0; i < channels.length(); i++) {
                    try {
                        String channel = channels.getString(i);
                        Log.w(channel, "channel: " + i);
                        if (channel.startsWith(app.PARENT_CHANNEL)) {
                            sendNotificationsTo(channel);
                        }
                    } catch (JSONException e) {
                        Log.w("Exception Caught", "Channels could not be retrieved from install");
                    }
                }
                if (isDriving)
                   lock();
            }
        }
    }

    /*********************************
     * Lock Device
     **********************************/

    public void lock() {
        if(app.getDevicePolicyManager() != null) {
            app.getDevicePolicyManager().lockNow();
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
	 * Determine which type of Notifications to send Parents
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
				app.getSmsManager().sendTextMessage(user.getPhone(), "", message, null, null);
			}
		}

	}

	/*********************************
	 * Construct Notification Message
	 **********************************/

	public String constructMessage() {
		String message;
		if(isDriving){
            message = getResources().getString(R.string.isDriverNotification);
		}else{
            message = getResources().getString(R.string.isPassengerNotification);
		}
		return message;
	}
}
