package com.yolo.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.text.format.Time;

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

import java.util.HashMap;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    private Application app;
    private static final int averageDrivingMPH = 25;

    @Override
    public void onReceive(final Context context, Intent intent) {

        app = (Application)context.getApplicationContext();
        Location location = (Location) intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);

        Time time = new Time();
        time.setToNow();
        long now = time.toMillis(true);

        if(location != null) {
            double speed = location.getSpeed() * 2.2369;
            if (speed < averageDrivingMPH && !app.getInstall().getBoolean("isLocked")) {
                JSONArray channels = app.getInstall().getJSONArray("channels");
                String message = constructMessage(Application.isDriving);

                getParents(channels, message, now);
                if (Application.isDriving) {
                    app.lock();
                }
            }
        }
    }

    /*********************************
     * getParents
     **********************************/
    public void getParents(JSONArray channels, final String message, final long now) {
        for (int i = 0; i < channels.length(); i++) {
            try {
                String channel = channels.getString(i);
                //Log.w(channel, "channel: " + i);
                if (channel.startsWith(app.PARENT_CHANNEL)) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.getInBackground(channel.replace(app.PARENT_CHANNEL, ""), new GetCallback<ParseUser>() {
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                sendNotificationsTo((User) parseUser, message, now);
                            }
                        }
                    });
                }
            } catch (JSONException e) {

            }
        }
    }


    /*********************************
     * Construct Notification Message
     **********************************/

    public String constructMessage(boolean isDriving) {
        String message;
        if(isDriving){
            message = app.getResources().getString(R.string.isDriverNotification);
        }else{
            message = app.getResources().getString(R.string.isPassengerNotification);
        }
        return message;
    }



    public void sendNotificationsTo(User user, String message,long now){
        long pref = Application.milli[user.getReminderFrequency()];
        if(now > app.getInstall().getLong("f")) {
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
            app.getInstall().put("f", now+pref);
            app.getInstall().saveInBackground();
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

                }
            }
        });
    }


}
