package com.yolo;

import android.app.admin.DeviceAdminReceiver;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.PushService;
import com.yolo.activities.MainActivity;
import com.yolo.models.User;

public class Application extends android.app.Application {

	public final String DEVICE_CHANNEL = "device_channel_";
	public final String PARENT_CHANNEL = "parent_channel_";
	
	public static class MyAdmin extends DeviceAdminReceiver {
		public void onEnable(){
			System.out.println("onEnable");
		}
		
		public void onDisable(){
			System.out.println("onDisable");
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
        ParseObject.registerSubclass(User.class);
		Parse.initialize(this, "yG0OKddCMctN5vtCj5ocUbDxrRJjlPuzZLXMOXA9", "FGdSTBZZgOlRTdMkMqSOWydTOG3hliqXigOqm2sk");
		PushService.setDefaultPushCallback(this, MainActivity.class);
		
	}
}