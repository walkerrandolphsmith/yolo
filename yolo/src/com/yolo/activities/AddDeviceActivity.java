package com.yolo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yolo.R;

public class AddDeviceActivity extends BaseActivity{
	
	EditText mUserName;

	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mUserName = (EditText)findViewById(R.id.name);
	     
	     final Button signInButton = (Button) findViewById(R.id.addDevice);
	     signInButton.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 addDevice();
	         }
	     });
	}
	
	public void addDevice() {

        // Store values at the time of the login attempt.
        String username = mUserName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(username)) {
        	mUserName.setError(getString(R.string.error_field_required));
            focusView = mUserName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Intent i = new Intent();
            i.putExtra("name", username);
            i.setAction("com.yolo.action.ADDDEVICE");
            sendBroadcast(i);
            onBackPressed();
        }
    }
}
