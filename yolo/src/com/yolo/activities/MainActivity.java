package com.yolo.activities;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseCloud;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity {
		
	public boolean isDriving;
    private static ArrayList<Long> timeStamps = new ArrayList<Long>();

    private BroadcastReceiver locationChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            //MainActivity.this.locationChanged();
            Send sender = new Send();
            sender.execute(new String[]{"Yolo"});
        }
    };

    private BroadcastReceiver remoteLockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
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
        unregisterReceiver(remoteLockReceiver);
        unregisterReceiver(locationChangedReceiver);
    }


    /*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().hide();
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, MainActivity.class);
        app.getInstall().addUnique("channels", app.DEVICE_CHANNEL + app.getInstall().getObjectId());
        app.getInstall().saveInBackground();

		 if (!app.getDevicePolicyManager().isAdminActive(app.getAdminName())) {
     		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
     		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, app.getAdminName());
     		startActivityForResult(intent, 1);
     	} 
	    if(app.getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent location_intent = new Intent("com.yolo.action.LOCATIONCHANGE");
            PendingIntent launchIntent = PendingIntent.getBroadcast(this, 0, location_intent, 0);
            //provider string, min time between, min distance change, intent
            app.getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 10, launchIntent);
        }else{
           new NoGpsDialog(this).show();
	    }
        TextView status = (TextView) findViewById(R.id.status);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setOnClickListener(new isDrivingCheckedChangedListener(logo, status));

        Button signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
  	}

    /*********************************
     * isDriving onCheckedChanged Listener
     **********************************/

    public class isDrivingCheckedChangedListener implements OnClickListener {

        public ImageView logo;
        public TextView status;

        public isDrivingCheckedChangedListener(ImageView logo, TextView status) {
            this.logo = logo;
            this.status = status;
        }
        @Override
        public void onClick(View view) {
            if(isDriving) {
                logo.setImageResource(R.drawable.ic_launcher);
                status.setText(getResources().getString(R.string.passenger));
            }else{
                logo.setImageResource(R.drawable.ic_message);
                status.setText(getResources().getString(R.string.driving));
            }
            isDriving = !isDriving;
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
     * locationChanged
     **********************************/

    public void locationChanged() {

        Time now = new Time();
        now.setToNow();
        long mili = now.toMillis(true);
        timeStamps.add(mili);
        long diff = TimeUnit.MILLISECONDS.toSeconds(mili-timeStamps.get(0));
        if(timeStamps.size()>1)
            timeStamps.remove(0);
        Log.w("TIMESTAMP: ", diff+"");

        Location location = app.getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            double speed = location.getSpeed() * 2.2369;

            if (speed < 20) {
                //Log.w("speed", "is moving at " + speed + " mph");
                JSONArray channels = app.getInstall().getJSONArray("channels");
                for (int i = 0; i < channels.length(); i++) {
                    try {
                        String channel = channels.getString(i);
                        //Log.w(channel, "channel: " + i);
                        if (channel.startsWith(app.PARENT_CHANNEL)) {
                            sendNotificationsTo(channel.replace(app.PARENT_CHANNEL, ""));
                        }
                    } catch (JSONException e) {
                        //Log.w("Exception Caught", "Channels could not be retrieved from install");
                    }
                }
                if (isDriving)
                   lock();
            }
        }
    }

	/*********************************
	 * Get Users 
	 **********************************/
	
	public void sendNotificationsTo(String channel){
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.getInBackground(channel, new GetCallback<ParseUser>() {
			public void done(ParseUser parseUser, ParseException e) {
				if (e == null) {
					sendNotificationsToCallback(parseUser);
			    }else{
			    	//Log.w("ParseUser Exception", e.getLocalizedMessage());
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
            if(user.getEmailVerified()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("email", user.getEmail());
                map.put("message", message);
                ParseCloud.callFunctionInBackground("sendEmail", map, new FunctionCallback<String>() {
                    public void done(String result, ParseException e) {
                        if (e == null) {
                            //Log.w("result is", result);
                        }
                    }
                });
            }
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

    /*********************************
     * Async Task that is unused
     **********************************/

    private class Send extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String ...urls) {
            String response = "";
            locationChanged();
            return response;
        }

        @Override
        protected void onPostExecute(String result){
            Log.w("onPostExecute", "async");
        }
    }
}
