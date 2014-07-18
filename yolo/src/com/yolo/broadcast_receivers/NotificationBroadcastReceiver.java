package com.yolo.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.yolo.services.YoloService;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(final Context context, Intent intent) {
        Intent i = new Intent(context, YoloService.class);
        i.putExtra("lock", true);
        WakefulIntentService.sendWakefulWork(context, i);
	}
}
