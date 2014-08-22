package com.yolo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.yolo.activities.MainActivity;
import com.yolo.models.User;
import com.yolo.services.YoloService;

public class Application extends android.app.Application {

    private static int second = 1000;
    private static int minute = 60000; //60 * second;
    private static int quarter_hour = 900000; //15 * minute
    private static int half_hour = 1800000; //30 * minute
    private static int hour = 3600000; //60 * minute;
    private static int day = 86400000; //24 * hour;
    private static int week = 604800000; //7 * day;


    private static int half_day = 4320000; //12 * hour
    public static final int[] milli = new int[]{quarter_hour, half_hour, hour, hour * 2, hour * 3, hour * 4, hour * 6, hour * 8, hour * 10, half_day, hour * 15, hour * 18, hour * 20, hour * 21, hour * 22, day, day * 2, day * 3, day * 5, week, 0};
    public static final String[] phrases = new String[]{"15 minutes", "30 minutes", "1 hour", "2 hours", "3 hours", "4 hours", "6 hours", "8 hours", "10 hours", "12 hours", "15 hours", "18 hours", "20 hours", "21 hours", "22 hours", "Day", "2 Days", "3 Days", "5 Days", "Week", "Unlimited"};

    public final String DEVICE_CHANNEL = "device_channel_";
    public final String PARENT_CHANNEL = "parent_channel_";

    public static class DeviceAdmin extends DeviceAdminReceiver {
    }

    private SharedPreferences sharedPreferences;
    private SmsManager smsManager;
    private AlarmManager alarmManager;
    private LocationManager locationManager;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName mAdminName;

    public static boolean isDriving;

    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public DevicePolicyManager getDevicePolicyManager() {
        return devicePolicyManager;
    }

    public ComponentName getAdminName() {
        return mAdminName;
    }

    public SmsManager getSmsManager() {
        return smsManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public ParseInstallation getInstall() {
        return ParseInstallation.getCurrentInstallation();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();
        ParseObject.registerSubclass(User.class);
        Parse.initialize(this, "yG0OKddCMctN5vtCj5ocUbDxrRJjlPuzZLXMOXA9", "FGdSTBZZgOlRTdMkMqSOWydTOG3hliqXigOqm2sk");
        PushService.setDefaultPushCallback(this, MainActivity.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Device Policy Manager require minSDK version 8
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, DeviceAdmin.class);

        smsManager = SmsManager.getDefault();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                getInstall().addUnique("channels", DEVICE_CHANNEL + getInstall().getObjectId());
                getInstall().put("isLocked", false);
                getInstall().put("f", 0);
                getInstall().saveInBackground();
            }
        });
    }

    public boolean isAdmin() {
        boolean result = false;
        if (devicePolicyManager != null) {
            result = devicePolicyManager.isAdminActive(mAdminName);
        }
        return result;
    }

    /**
     * ******************************
     * Lock Device
     * ********************************
     */

    public void lock() {
        if (devicePolicyManager != null) {
            devicePolicyManager.lockNow();
        }
    }

    public void setPassword(String password) {
        if (devicePolicyManager != null) {
            devicePolicyManager.resetPassword(password, 0);
        }
    }

    public void setPasswordExpiration(long expiration) {
        Intent intent = new Intent(this, YoloService.class);
        intent.putExtra("expired", true);
        PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + expiration, pi);
    }
}
