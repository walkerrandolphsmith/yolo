package com.yolo.services;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.text.format.Time;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yolo.Application;
import com.yolo.R;
import com.yolo.models.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class YoloService extends WakefulIntentService {

    private Application app;
    private static ArrayList<Long> timeStamps = new ArrayList<Long>();


    public YoloService(){
        super("YoloService");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        boolean location = intent.getBooleanExtra("location", false);
        boolean lock = intent.getBooleanExtra("lock", false);
        boolean expired = intent.getBooleanExtra("expired", false);
        app = (Application)getApplicationContext();

        if(expired){
            app.setPassword("");
        }
        else if(lock){
            String password = intent.getStringExtra("password");
            String expiration = intent.getStringExtra("reset");

            app.setPassword(password);
            app.setPasswordExpiration(Application.milli[Integer.valueOf(expiration)]);
            app.lock();
        }else if(location){
            Time now = new Time();
            now.setToNow();

            long mili = now.toMillis(true);
            timeStamps.add(mili);
            long diff = TimeUnit.MILLISECONDS.toSeconds(mili-timeStamps.get(0));
            if(timeStamps.size()>1)
                timeStamps.remove(0);
            Log.w("TIMESTAMP: ", diff + "");
            if(diff < 9000 ){
                locationChanged(diff);
            }
        }
    }

    /*********************************
     * locationChanged
     **********************************/

    public void locationChanged(long diff) {
        Location location = app.getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            double speed = location.getSpeed() * 2.2369;
            Log.w("speed", "is moving at " + speed + " mph");
            JSONArray channels = app.getInstall().getJSONArray("channels");
            if (speed < 20) {
                sendNotificationsToParentChannels(channels, diff);
                if (Application.isDriving) {
                    app.lock();
                }
            }
        }
    }

    /*********************************
     * sendNotificationsToParentChannels
     **********************************/
    public void sendNotificationsToParentChannels(JSONArray channels, long diff) {
        for (int i = 0; i < channels.length(); i++) {
            try {
                String channel = channels.getString(i);
                //Log.w(channel, "channel: " + i);
                if (channel.startsWith(app.PARENT_CHANNEL)) {
                    sendNotificationsToParentChannel(channel.replace(app.PARENT_CHANNEL, ""), diff);
                }
            } catch (JSONException e) {
                //Log.w("Exception Caught", "Channels could not be retrieved from install");
            }
        }
    }

    /*********************************
     * sendNotificationsToParentChannel
     **********************************/

    public void sendNotificationsToParentChannel(String channel, final long diff){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(channel, new GetCallback<ParseUser>() {
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    String message = constructMessage(Application.isDriving);
                    sendNotificationsToCallback(parseUser, message, diff);
                }else{
                    //Log.w("ParseUser Exception", e.getLocalizedMessage());
                }
            }
        });
    }

    /*********************************
     * Filter Notifications by User Preferences
     **********************************/

    public void sendNotificationsToCallback(ParseUser parseUser, String message, long diff) {
        User user = (User) parseUser;
        long pref = TimeUnit.MILLISECONDS.toSeconds(Application.milli[user.getReminderFrequency()]);
        if(pref > diff) {
            if (user.getReceivePushNotifications()) {
                if (user.getObjectId() != null) {
                    sendPushNotification(user, message);
                }
            }
            if (user.getReceiveEmails()) {
                if (user.getEmailVerified()) {
                    sendEmail(user, message);
                }
            }
            if (user.getReceiveSMS()) {
                if (user.getPhone() != null) {
                    app.getSmsManager().sendTextMessage(user.getPhone(), "", message, null, null);
                }
            }
        }
    }

    /*********************************
     * Helper Methods for Sending
     **********************************/

    public void sendPushNotification(User user, String message){
        ParsePush push = new ParsePush();
        push.setChannel(user.getUsername() + user.getObjectId());
        push.setMessage(message);
        push.sendInBackground();
    }

    public void sendEmail(User user, String message){
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

    /*********************************
     * Construct Notification Message
     **********************************/

    public String constructMessage(boolean isDriving) {
        String message;
        if(isDriving){
            message = getResources().getString(R.string.isDriverNotification);
        }else{
            message = getResources().getString(R.string.isPassengerNotification);
        }
        return message;
    }
}
