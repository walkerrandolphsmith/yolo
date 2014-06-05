package com.yolo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.parse.ParseUser;
import com.parse.PushService;

public class ConsoleActivity extends Activity {
	
	private String parentChannel;
	private String parentEmail;
	private String parentSMS;
	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);
		
		PushService.unsubscribe(this, "PC"); //unsubscribe from the public channel
		
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
		  parentChannel = currentUser.getObjectId();
		  parentEmail = currentUser.getEmail();
		  parentSMS = currentUser.getString("phone");
		  
		} else {
		  parentChannel = "DEAD";
		}
		
		MainActivity.channel = parentChannel;
		MainActivity.email = parentEmail;
		MainActivity.phone = parentSMS;
		
		 Switch switchPushNotification = (Switch) findViewById(R.id.receivePushNotification);
	     if (switchPushNotification != null) {
	    	SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
	 		boolean isRecevingPushNotifications = prefs.getBoolean("receivePushNotifications", true);
	      	switchPushNotification.setOnCheckedChangeListener(new PushNotificationSwitchListener());
	      	switchPushNotification.setChecked(isRecevingPushNotifications);
	      	MainActivity.notificationTypes[0] = switchPushNotification.isChecked();
	     }
	        
	    Switch switchEmail = (Switch) findViewById(R.id.receiveEmail);
	    if (switchEmail != null) {
	    	SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
	 		boolean isReceivingEmails = prefs.getBoolean("receiveEmails", true);
	 		switchEmail.setOnCheckedChangeListener(new EmailSwitchListener());
	 		switchEmail.setChecked(isReceivingEmails);
	       	MainActivity.notificationTypes[1] = switchEmail.isChecked();
	    }
	        
	    Switch switchSMS = (Switch) findViewById(R.id.receiveSMS);
	    if (switchSMS != null) {
	    	SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
	 		boolean isReceivingSMS = prefs.getBoolean("receiveSMS", true);
	 		switchSMS.setOnCheckedChangeListener(new SMSSwitchListener());
	 		switchSMS.setChecked(isReceivingSMS);
	       	MainActivity.notificationTypes[2] = switchSMS.isChecked();
	    }
		
		 final Button signOutButton = (Button) findViewById(R.id.signOut);
	     signOutButton.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 ParseUser.logOut();
	        	 finish();
	         }
	     });
	}
	
	public class PushNotificationSwitchListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
			editor.putBoolean("receivePushNotifications", (isChecked ? true : false));
			editor.apply();
			MainActivity.notificationTypes[0] = (isChecked ? true : false);
		}
	}
	
	public class EmailSwitchListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
			editor.putBoolean("receiveEmails", (isChecked ? true : false));
			editor.apply();
			MainActivity.notificationTypes[1] = (isChecked ? true : false);
		}
	}
	
	public class SMSSwitchListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
			editor.putBoolean("receiveSMS", (isChecked ? true : false));
			editor.apply();
			MainActivity.notificationTypes[2] = (isChecked ? true : false);
		}
	}

	
}
