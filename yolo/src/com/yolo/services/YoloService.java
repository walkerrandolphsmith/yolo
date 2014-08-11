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
import com.parse.SaveCallback;
import com.yolo.Application;
import com.yolo.R;
import com.yolo.models.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class YoloService extends WakefulIntentService {

    private Application app;
    private static final int averageDrivingMPH = 25;


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
            app.getInstall().put("isLocked", false);
            app.getInstall().saveInBackground();
        }
        else if(lock){
            String password = intent.getStringExtra("password");
            String expiration = intent.getStringExtra("reset");
            app.setPassword(password);
            app.setPasswordExpiration(Application.milli[Integer.valueOf(expiration)]);
            app.lock();
            app.getInstall().put("isLocked", true);
            app.getInstall().saveInBackground();
        }else if(location){
            Time now = new Time();
            now.setToNow();
            Log.w("now: ", now.toString());

            app.getInstall().put("t2",now.toMillis(true));
            app.getInstall().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    long diff = app.getInstall().getLong("t2") - app.getInstall().getLong("t1");
                    locationChanged(diff);
                    Log.w("t1", app.getInstall().getLong("t1")+"");
                    Log.w("t2", app.getInstall().getLong("t2")+"");
                    Log.w("save callback -> diff", diff+"");
                }
            });
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
            if (speed < averageDrivingMPH && !app.getInstall().getBoolean("isLocked")) {
                JSONArray channels = app.getInstall().getJSONArray("channels");
                getParents(channels, constructMessage(Application.isDriving), diff);
                if (Application.isDriving) {
                    app.lock();
                }
            }
        }
    }

    /*********************************
     * getParents
     **********************************/
    public ArrayList<User> getParents(JSONArray channels, final String message, final long diff) {
        Log.w("Get parents", "funct");

        final ArrayList<User> parents = new ArrayList<User>();
        for (int i = 0; i < channels.length(); i++) {
            try {
                String channel = channels.getString(i);
                //Log.w(channel, "channel: " + i);
                if (channel.startsWith(app.PARENT_CHANNEL)) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.getInBackground(channel.replace(app.PARENT_CHANNEL, ""), new GetCallback<ParseUser>() {
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                sendNotificationsTo((User)parseUser, message, diff);
                            }else{
                                Log.w("ParseUser Exception", e.getLocalizedMessage());
                            }
                        }
                    });
                }else {
                    Log.w("Not a parent channel", "Skip");
                }
            } catch (JSONException e) {
                Log.w("Exception Caught", "Channels could not be retrieved from install");
            }
        }
        return parents;
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



    public void sendNotificationsTo(User user, String message, long diff){
        long pref = Application.milli[user.getReminderFrequency()];
        if(diff > pref) {
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
            app.getInstall().put("t1", app.getInstall().getLong("t2"));
            app.getInstall().saveInBackground();

            Log.w("Reminder Frequency message block.", "Notifications sent.");
            Log.w("t1", app.getInstall().getLong("t1")+"");
            Log.w("t2", app.getInstall().getLong("t2")+"");
        }
        else{
            Log.w("Reminder Frequency message block.", "The frequency reminder is greater than the elasped time since the last message. No notifications sent.");
            Log.w("t1", app.getInstall().getLong("t1")+"");
            Log.w("t2", app.getInstall().getLong("t2")+"");
        }
    }

    /*********************************
     * Helper Methods for Sending
     **********************************/

    public void sendPushNotification(User user, String message){
        ParsePush push = new ParsePush();
        push.setChannel(app.PARENT_CHANNEL + user.getObjectId());
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

}
