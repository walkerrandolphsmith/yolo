package com.yolo.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.yolo.services.YoloService;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String password;
        String expiration;
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            password = json.getString("password");
            expiration = json.getString("reset");

            if (!(password.isEmpty() || expiration.isEmpty())) {
                Intent i = new Intent(context, YoloService.class);
                i.putExtra("lock", true);
                i.putExtra("password", password);
                i.putExtra("reset", expiration);
                WakefulIntentService.sendWakefulWork(context, i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
