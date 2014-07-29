package com.yolo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;
import com.yolo.activities.MainActivity;
import com.yolo.models.User;
import com.yolo.services.YoloService;

public class Application extends android.app.Application {

	public final String DEVICE_CHANNEL = "device_channel_";
	public final String PARENT_CHANNEL = "parent_channel_";

    public static class DeviceAdmin extends DeviceAdminReceiver { }

    private SmsManager smsManager;
    private AlarmManager alarmManager;
    private LocationManager locationManager;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName mAdminName;

    public static boolean isDriving;

    private Context context;

    public Context getContext(){
        return context;
    }

    public void setContext(Context context){
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

    public ParseInstallation getInstall() { return ParseInstallation.getCurrentInstallation(); }

    @Override
	public void onCreate() {
		super.onCreate();

        this.context = getApplicationContext();
        ParseObject.registerSubclass(User.class);
		Parse.initialize(this, "yG0OKddCMctN5vtCj5ocUbDxrRJjlPuzZLXMOXA9", "FGdSTBZZgOlRTdMkMqSOWydTOG3hliqXigOqm2sk");
		PushService.setDefaultPushCallback(this, MainActivity.class);

        //Device Policy Manager require minSDK version 8
        devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, DeviceAdmin.class);

        smsManager = SmsManager.getDefault();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public boolean isAdmin(){
        boolean result = false;
        if(devicePolicyManager != null) {
            result = devicePolicyManager.isAdminActive(mAdminName);
        }
        return result;
    }



    /*********************************
     * Lock Device
     **********************************/

    public void lock() {
        if(devicePolicyManager != null) {
            devicePolicyManager.lockNow();
        }
    }

    public void setPassword(String password) {
        if(devicePolicyManager != null) {
            devicePolicyManager.resetPassword(password, 0);
        }
    }

    public void setPasswordExpiration(long expiration){
        Log.w("Application", "set the password expiration");
        Intent intent = new Intent(this, YoloService.class);
        intent.putExtra("expired", true);
        PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+(1000*20), pi);
    }
}
