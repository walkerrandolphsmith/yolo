package com.yolo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yolo.R;

public class ChildEditActivity extends BaseActivity{
	
	EditText mUserName;
    int position;

	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_action);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
		position = bundle.getInt("position");
		mUserName = (EditText)findViewById(R.id.name);
	     final Button editDevice = (Button) findViewById(R.id.update_account_btn);
        editDevice.setText("Rename Device");
        editDevice.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 editDevice();
	         }
	     });
	}
	
	public void editDevice() {

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
            Intent i = new Intent(ChildEditActivity.this, ConsoleActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("name", username);
            i.putExtra("position", position);
            i.putExtra("edited", true);
            startActivity(i);
        }
    }
}
