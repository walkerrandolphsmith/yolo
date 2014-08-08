package com.yolo.activities;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.PushService;
import com.yolo.Application;
import com.yolo.R;
import com.yolo.dialogs.NoGpsDialog;

public class MainActivity extends BaseActivity {

    /*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();

        new ParseTask(this).execute();

        boolean isLoggedIn = getApp().getSharedPreferences().getBoolean("loggedIn", false);
        if(!isLoggedIn) {
            Log.w("You are not currently logged in so your phone will be treated as a child's device.", "isLoggedIn == false");
            if (!getApp().isAdmin()) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, getApp().getAdminName());
                startActivityForResult(intent, 1);
            }
            if (getApp().getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                PendingIntent launchIntent = PendingIntent.getBroadcast(this, 0, new Intent("com.yolo.action.LOCATIONCHANGE"), 0);
                getApp().getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 50/*time*/, 10/*distance*/, launchIntent);
            } else {
                new NoGpsDialog(this).show();
            }
        }else{
            Log.w("User is logged in", "phone is treated as a parent device.");
            Intent i = new Intent(MainActivity.this, ConsoleActivity.class);
            startActivity(i);
        }
        TextView status = (TextView) findViewById(R.id.status);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        setDrivingState(logo, status);
        logo.setOnClickListener(new isDrivingCheckedChangedListener(logo, status));

        Button signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
  	}

    /*********************************
     * isDriving onCheckedChanged Listener
     **********************************/

    public class isDrivingCheckedChangedListener implements OnClickListener {

        public ImageView logo;
        public TextView status;

        public isDrivingCheckedChangedListener(ImageView logo, TextView status) {
            this.logo = logo;
            this.status = status;
        }
        @Override
        public void onClick(View view) {
            Application.isDriving = !Application.isDriving;
            setDrivingState(logo, status);
        }
    }

    public void setDrivingState(ImageView logo, TextView status){
        if(Application.isDriving) {
            logo.setImageResource(R.drawable.ic_message);
            status.setText(getResources().getString(R.string.driving));
        }else{
            logo.setImageResource(R.drawable.ic_launcher);
            status.setText(getResources().getString(R.string.passenger));
        }
    }

    /*********************************
     * Async Task Init Parse
     **********************************/

    private class ParseTask extends AsyncTask<Void, Void, Void> {

        private MainActivity activity;

        public ParseTask(MainActivity activity){
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ParseAnalytics.trackAppOpened(getIntent());
            PushService.setDefaultPushCallback(activity, MainActivity.class);
            getApp().getInstall().addUnique("channels", getApp().DEVICE_CHANNEL + getApp().getInstall().getObjectId());
            getApp().getInstall().saveInBackground();
            return null;
        }
    }

}
