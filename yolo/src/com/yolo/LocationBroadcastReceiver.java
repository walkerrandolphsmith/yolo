package com.yolo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yolo.activities.MainActivity;
import com.yolo.util.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction("RESPOND_LOCATION");
        context.sendBroadcast(i);
    }
}
