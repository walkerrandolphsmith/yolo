package com.yolo.services;

import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.yolo.Application;

public class YoloService extends WakefulIntentService {

    public YoloService() {
        super("YoloService");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        boolean lock = intent.getBooleanExtra("lock", false);
        boolean expired = intent.getBooleanExtra("expired", false);
        Application app = (Application) getApplicationContext();

        if (expired) {
            app.setPassword("");
            app.getInstall().put("isLocked", false);
            app.getInstall().saveInBackground();
        } else if (lock) {
            String password = intent.getStringExtra("password");
            String expiration = intent.getStringExtra("reset");
            app.setPassword(password);
            app.setPasswordExpiration(Application.milli[Integer.valueOf(expiration)]);
            app.lock();
            app.getInstall().put("isLocked", true);
            app.getInstall().saveInBackground();
        }
    }
}
