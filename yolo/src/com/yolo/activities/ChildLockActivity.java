package com.yolo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yolo.R;

public class ChildLockActivity extends BaseActivity {

    private static int quarter_hour = 90000; //15 * minute
    private static int half_hour = 180000; //30 * minute
    private static int hour = 360000; //60 * minute;
    private static int day = 8640000; //24 * hour;
    private static int week = 60480000; //7 * day;
    private static final long[] milli = new long[]{ quarter_hour, half_hour, hour, hour*2, day, week, 0 } ;
    private static final String[] phrases = new String[]{ "15 minutes", "30 minutes", "1 hour", "2 hours", "1 Day", "1 Week", "No Expiration" } ;

    EditText mPassword;
    EditText mConfirmPassword;
    TextView mInterval;
    SeekBar slider;
    String channel;

    /**
     * ******************************
     * OnCreate
     * ********************************
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_lock);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        channel = bundle.getString("channel");
        mPassword = (EditText)findViewById(R.id.password);
        mConfirmPassword = (EditText)findViewById(R.id.confirm);

        mInterval = (TextView) findViewById(R.id.interval);

        slider = (SeekBar) findViewById(R.id.expiration_slider);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mInterval.setText(phrases[i]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final Button lockButton = (Button) findViewById(R.id.lock_btn);
        lockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lockDevice();
            }
        });
    }


    public void lockDevice() {

        // Store values at the time of the login attempt.
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        long expiration = milli[slider.getProgress()];

        boolean cancel = false;
        View focusView = null;

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

        if (TextUtils.isEmpty(confirmPassword))
        {
            mConfirmPassword.setError(getString(R.string.error_field_required));
            focusView = mConfirmPassword;
            cancel = true;
        }
        else if (mPassword != null && !confirmPassword.equals(password))
        {
            mPassword.setError(getString(R.string.error_invalid_confirm_password));
            focusView = mPassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Intent i = new Intent(ChildLockActivity.this, ConsoleActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("password", password);
            i.putExtra("expiration", expiration);
            i.putExtra("channel", channel);
            i.putExtra("locked", true);
            startActivity(i);
        }
    }

}



