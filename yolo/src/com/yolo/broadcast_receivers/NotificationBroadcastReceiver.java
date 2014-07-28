package com.yolo.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.yolo.services.YoloService;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(final Context context, Intent intent) {
        String password;
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            password = json.getString("password");
            Log.w("br sees pssw " , password);
            Intent i = new Intent(context, YoloService.class);
            i.putExtra("lock", true);
            i.putExtra("password", password);
            WakefulIntentService.sendWakefulWork(context, i);
        } catch (JSONException e) {
            e.printStackTrace();
        }




	}
}
