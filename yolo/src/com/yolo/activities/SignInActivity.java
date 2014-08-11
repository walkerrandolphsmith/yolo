package com.yolo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.yolo.R;

public class SignInActivity extends BaseActivity{
	
	EditText mUserName;
	EditText mPassword;

	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mUserName = (EditText)findViewById(R.id.username);
	    mPassword = (EditText)findViewById(R.id.password);
		
		final Button signUpButton = (Button) findViewById(R.id.signUp);
		signUpButton.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
		         startActivity(intent);
	         }
	     });
	     
	     final Button signInButton = (Button) findViewById(R.id.signIn);
	     signInButton.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 attemptLogin();
	         }
	     });
	}
	
	public void attemptLogin() {

        // Store values at the time of the login attempt.
        String username = mUserName.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
        	mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        } else if (password.length() < 4) {
        	mPassword.setError(getString(R.string.error_invalid_password));
            focusView =mPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
        	mUserName.setError(getString(R.string.error_field_required));
            focusView = mUserName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
        	ParseUser.logInInBackground(username, password, new LogInCallback() {
        		  public void done(ParseUser user, ParseException e) {
        		    if (user != null) {
                        //PendingIntent launchIntent = PendingIntent.getBroadcast(SignInActivity.this, 0, new Intent("com.yolo.action.LOCATIONCHANGE"), 0);
                        //getApp().getLocationManager().removeUpdates(launchIntent);
                        //Log.w("Parse Login ","location manager removes updates given a pedning intent");
                        //Log.w("Pedning intent: ", launchIntent.toString());

                       // Log.w("Share prefs ", "added loggedIn== true");

                        SharedPreferences.Editor editor = getApp().getSharedPreferences().edit();
                        editor.putBoolean("loggedIn", true);
                        editor.commit();
        		    	Intent i = new Intent(SignInActivity.this, ConsoleActivity.class);
      	 	            startActivity(i); 
      	 	            finish();
        		    } else {
        		    	switch (e.getCode()) {
        				case ParseException.OBJECT_NOT_FOUND:
        					mPassword.setError(getString(R.string.error_incorrect_password));
        					mPassword.requestFocus();
        					break;
        				default:
        					
        					break;
        				}
        		    }
        		  }
        		});
        }
    }
}
