package com.yolo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.yolo.models.User;

public class SignUpActivity extends Activity {
	
	protected static final String EXTRA_USERNAME = "USERNAME";
    protected static final String EXTRA_PASSWORD = "PASSWORD";
    protected static final String EXTRA_PASSWORD_CONFIRM = "PASSWORD_CONFIRM";
    protected static final String EXTRA_EMAIL = "EMAIL";
    protected static final String EXTRA_PHONE = "PHONE";
	
	EditText mUserName;
	EditText mPassword;
	EditText mPasswordConfirm;
	EditText mEmail;
	EditText mPhone;
	
	String userName;
	String password;
	String passwordConfirm;
	String email;
	String phone;
	
	User user;

	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	    mUserName = (EditText)findViewById(R.id.username);
	    mPassword = (EditText)findViewById(R.id.password);
	    mPasswordConfirm = (EditText)findViewById(R.id.passwordConfirm);
	    mEmail = (EditText)findViewById(R.id.email);
	    mPhone = (EditText)findViewById(R.id.phone);
	    
		if(savedInstanceState != null){
			mUserName.setText(savedInstanceState.getString(EXTRA_USERNAME));
			mPassword.setText(savedInstanceState.getString(EXTRA_PASSWORD));
			mPasswordConfirm.setText(savedInstanceState.getString(EXTRA_PASSWORD_CONFIRM));
			mEmail.setText(savedInstanceState.getString(EXTRA_EMAIL));
			mPhone.setText(savedInstanceState.getString(EXTRA_PHONE));
		}
	    
	    userName = mUserName.getText().toString();
	    password = mPassword.getText().toString();
	    passwordConfirm = mPasswordConfirm.getText().toString();
	    email = mEmail.getText().toString();
	    phone = mPhone.getText().toString();
		
		final Button signUpButton = (Button) findViewById(R.id.signUp);
		signUpButton.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	validateSignUp();
	         }
	     });
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(EXTRA_USERNAME, mUserName.getText().toString());
		outState.putString(EXTRA_PASSWORD, mPassword.getText().toString());
		outState.putString(EXTRA_PASSWORD_CONFIRM, mPasswordConfirm.getText().toString());
		outState.putString(EXTRA_EMAIL, mEmail.getText().toString());
		outState.putString(EXTRA_PHONE, mPhone.getText().toString());
	}
	
	private void removeErrors()
	{ 
		mUserName.setError(null);
		mPassword.setError(null);
		mPasswordConfirm.setError(null);
		mEmail.setError(null);
		mPhone.setError(null);
	}
	
	public void validateSignUp(){
		removeErrors();
		boolean cancel = false;
		View focusView = null;
		userName = mUserName.getText().toString();
		password = mPassword.getText().toString();
		passwordConfirm = mPasswordConfirm.getText().toString();
	    email = mEmail.getText().toString();
	    phone = mPhone.getText().toString();
		
	    if (TextUtils.isEmpty(passwordConfirm)) 
	    {
        	mPasswordConfirm.setError(getString(R.string.error_field_required));
        	focusView = mPasswordConfirm;
        	cancel = true;
        } 
	    else if (mPassword != null && !passwordConfirm.equals(password)) 
        {
        	mPassword.setError(getString(R.string.error_invalid_confirm_password));
        	focusView = mPassword;
        	cancel = true;
        }

	    if (TextUtils.isEmpty(password)) 
	    {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        } 
	    else if (password.length() < 4) 
        {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }
        
        if (TextUtils.isEmpty(email)) 
        {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } 
        else if (!email.contains("@")) 
        {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }
        
        if(cancel)
        {
        	focusView.requestFocus();
        }
        else
        {
        	user = new User(userName, email, password);
        	user.setPhone(phone);
        	
        	user.signUpInBackground(new SignUpCallback() {
  	 		  public void done(ParseException e) {
  	 		    if (e == null) {
  	 		    	Intent i = new Intent(SignUpActivity.this, MainActivity.class);
  	 	            startActivity(i); 
  	 		    } else {
  	 		    	removeErrors();
  	 		    	switch (e.getCode()) {
  	 					case ParseException.INVALID_EMAIL_ADDRESS:
  	 						mEmail.setError(getString(R.string.error_invalid_email));
  	 						mEmail.requestFocus();
  	 						break;
  	 					case ParseException.EMAIL_TAKEN:
  	 						mEmail.setError(getString(R.string.error_duplicate_email));
  	 						mEmail.requestFocus();
  	 						break;
  	 					case ParseException.USERNAME_TAKEN:
  	 						mUserName.setError(getString(R.string.error_duplicate_username));
  	 						mUserName.requestFocus();
  	 						break;
  	 					default:
  	 						Log.w("Error", "Unknown");
  	 						break;
  	 		    	}
  	 		    }
  	 		  }
  	 		});
        }
	}
	
	/*********************************
	 * SignUpActivity Behavior
	 **********************************/

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}	
}
