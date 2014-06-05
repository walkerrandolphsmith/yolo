package com.yolo;

import android.app.Activity;
import android.content.Intent;
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
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		PushService.unsubscribe(this, "PC"); //unsubscribe from the public channel
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
	      	switchPushNotification.setOnCheckedChangeListener(new PushNotificationSwitchListener());
	      	MainActivity.notificationTypes[0] = switchPushNotification.isChecked();
	     }
	        
	    Switch switchEmail = (Switch) findViewById(R.id.receiveEmail);
	    if (switchEmail != null) {
	    	switchEmail.setOnCheckedChangeListener(new EmailSwitchListener());
	       	MainActivity.notificationTypes[1] = switchEmail.isChecked();
	    }
	        
	    Switch switchSMS = (Switch) findViewById(R.id.receiveSMS);
	    if (switchSMS != null) {
	    	switchSMS.setOnCheckedChangeListener(new SMSSwitchListener());
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
			MainActivity.notificationTypes[0] = (isChecked ? true : false);
		}
	}
	
	public class EmailSwitchListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			MainActivity.notificationTypes[1] = (isChecked ? true : false);
		}
	}
	
	public class SMSSwitchListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			MainActivity.notificationTypes[2] = (isChecked ? true : false);
		}
	}

	
}
