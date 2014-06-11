package com.yolo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.yolo.ConsoleActivity;
import com.yolo.R;

public class SettingsFragment extends Fragment {
	
	ConsoleActivity activity;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        Switch switchPushNotification = (Switch) v.findViewById(R.id.receivePushNotification);
	     if (switchPushNotification != null) {
	 		boolean isRecevingPushNotifications = activity.currentUser.getReceivePushNotifications();
	      	switchPushNotification.setChecked(isRecevingPushNotifications);
	      	switchPushNotification.setOnCheckedChangeListener(new PushNotificationSwitchListener());

	     }
	        
	    Switch switchEmail = (Switch) v.findViewById(R.id.receiveEmail);
	    if (switchEmail != null) {
	 		boolean isReceivingEmails = activity.currentUser.getReceiveEmails();
	 		switchEmail.setChecked(isReceivingEmails);
	 		switchEmail.setOnCheckedChangeListener(new EmailSwitchListener());
	    }
	        
	    Switch switchSMS = (Switch) v.findViewById(R.id.receiveSMS);
	    if (switchSMS != null) {
	 		boolean isReceivingSMS = activity.currentUser.getReceiveSMS();
	 		switchSMS.setChecked(isReceivingSMS);
	 		switchSMS.setOnCheckedChangeListener(new SMSSwitchListener());
	    }
        return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		activity = (ConsoleActivity) getActivity();
	}
	
	public class PushNotificationSwitchListener implements CompoundButton.OnCheckedChangeListener {
 		@Override
 		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
 			activity.currentUser.setReceivePushNotifications((isChecked ? true : false));
 			activity.currentUser.saveEventually();
 		}
	}
	
	public class EmailSwitchListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			activity.currentUser.setReceiveEmails((isChecked ? true : false));
			activity.currentUser.saveEventually();
		}
	}
	
	public class SMSSwitchListener implements CompoundButton.OnCheckedChangeListener {
 		@Override
 		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
 			activity.currentUser.setReceiveSMS((isChecked ? true : false));
 			activity.currentUser.saveEventually();
 		}
	}
	
}
