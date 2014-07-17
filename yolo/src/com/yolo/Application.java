package com.yolo;

import android.content.ComponentName;
import android.content.Context;
import android.location.LocationManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.telephony.SmsManager;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;
import com.yolo.activities.MainActivity;
import com.yolo.models.User;

public class Application extends android.app.Application {

	public final String DEVICE_CHANNEL = "device_channel_";
	public final String PARENT_CHANNEL = "parent_channel_";

    public static class DeviceAdmin extends DeviceAdminReceiver { }

    private SmsManager smsManager;
    private LocationManager locationManager;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName mAdminName;
    private ParseInstallation install;

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

    public ParseInstallation getInstall() { return install; }

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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        install = ParseInstallation.getCurrentInstallation();
    }
}
