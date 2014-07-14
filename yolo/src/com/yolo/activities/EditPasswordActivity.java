package com.yolo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yolo.R;

public class EditPasswordActivity extends BaseActivity{
	
	EditText mPassword;
    EditText mPasswordConfirm;
    EditText mEmail;
    EditText mPhone;

	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_edit);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);


        mPassword = (EditText)findViewById(R.id.name);
        mPasswordConfirm = (EditText) findViewById(R.id.confirm);
        mEmail = (EditText) findViewById(R.id.email);
        mPhone = (EditText) findViewById(R.id.phone);

        mPassword.setHint("New Password.");
        mPasswordConfirm.setVisibility(View.VISIBLE);
        mPasswordConfirm.setHint("Confirm New Password.");
	     final Button editDevice = (Button) findViewById(R.id.device_button);
        editDevice.setText("Update Password");
        editDevice.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 updatePassword();
	         }
	     });
	}
	
	public void updatePassword() {

        // Store values at the time of the login attempt.
        String password = mPassword.getText().toString();
        String passwordConfirm = mPasswordConfirm.getText().toString();
        String email = mEmail.getText().toString();
        String phone = mPhone.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!password.isEmpty() && TextUtils.isEmpty(passwordConfirm))
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


        else if (!password.isEmpty() && password.length() < 4)
        {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        else if (!email.isEmpty() && !email.contains("@"))
        {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if(!phone.isEmpty() && (phone.length() < 7 || phone.length() > 10)){
            mPhone.setError("This is not a valid phone number.");
            focusView = mPhone;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Intent i = new Intent();
            i.putExtra("password", password);
            i.putExtra("email", email);
            i.putExtra("phone", phone);
            i.setAction("com.yolo.action.EDITACCOUNT");
            sendBroadcast(i);
            onBackPressed();
        }
    }
}
