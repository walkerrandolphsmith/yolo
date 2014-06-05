package com.yolo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;
import com.parse.PushService;

public class ConsoleActivity extends Activity {
	
	private String parentChannel;
	
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
		} else {
		  parentChannel = "DEAD";
		}
		
		MainActivity.channel = parentChannel;
		
		 final Button signOutButton = (Button) findViewById(R.id.signOut);
	     signOutButton.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 ParseUser.logOut();
	        	 finish();
	         }
	     });
	}
}
