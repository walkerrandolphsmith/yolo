package com.yolo.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent i = new Intent();
        i.setAction("com.yolo.action.LOCATIONCHANGECONFIRM");
        context.sendBroadcast(i);
    }
}
