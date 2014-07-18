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
        super("LocationChange");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        Log.w("Wakeful","Service");

        boolean location = intent.getBooleanExtra("location", false);
        boolean lock = intent.getBooleanExtra("lock", false);

        app = (Application)getApplicationContext();

        if(location) {
            locationChanged();
        }else if(lock){
            app.lock();
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
                if (Application.isDriving)
                    app.lock();
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
        String message = constructMessage(Application.isDriving);

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
