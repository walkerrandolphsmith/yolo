package com.yolo;

import org.json.JSONException;
import org.json.JSONObject;

import com.yolo.activities.MainActivity;
import com.yolo.util.ConnectionManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		boolean hasInternetConnection = ConnectionManager.checkInternetConnection(context);

		if (hasInternetConnection) {
			try {
				String action = intent.getAction();
				String channel = intent.getExtras().getString(
						"com.parse.Channel");
				JSONObject json = new JSONObject(intent.getExtras().getString(
						"com.parse.Data"));
				Log.d("NotificationBroadcastReceivers",
						"action: " + action + "\n channel: " + channel
								+ "\n json: " + json.toString());
			} catch (JSONException e) {
				Log.d("NotificationBroadcastReceivers",
						"JSONException: " + e.getMessage());
			}
			Intent i = new Intent(context,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("remote", true);
            context.startActivity(i);
		}	
	}
}
