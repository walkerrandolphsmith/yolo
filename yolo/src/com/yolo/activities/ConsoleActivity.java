package com.yolo.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.yolo.Application;
import com.yolo.ChildrenAdapter;
import com.yolo.R;
import com.yolo.models.User;

public class ConsoleActivity extends Activity {
	
	Application app;
	ParseInstallation install;
	public User currentUser;
	private FragmentManager fragmentManager;
	public SharedPreferences prefs;
	private ChildrenAdapter adapter;
		
	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		app = (Application)getApplication();
		install = ParseInstallation.getCurrentInstallation();
    	currentUser = (User) ParseUser.getCurrentUser();
		fragmentManager = getFragmentManager();
		prefs = getPreferences(MODE_PRIVATE);
		
		final JSONArray childrenList = currentUser.getChildren();
		final ListView mListView = (ListView)findViewById(android.R.id.list);
		adapter = new ChildrenAdapter(this, childrenList);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new remoteLockDeviceItemClickListener(childrenList)); 
	}
	
	/*********************************
	 * Remote Lock Device Item Click Listener
	 **********************************/
	
	public class remoteLockDeviceItemClickListener implements OnItemClickListener {
		public JSONArray childrenList;
		public remoteLockDeviceItemClickListener(JSONArray childrenList){
			this.childrenList = childrenList;
		}
		
		@Override
		public void onItemClick(AdapterView<?> adapterView, View v, int position, long l){
			//Parent send notification to child to remote lock
	        JSONObject data = null;
			try {
				data = new JSONObject(
					  "{"
					+ "\"action\": \"com.example.UPDATE_STATUS\","
					+  "\"alert\": \"Your phone has been locked by Yolo. Contact Parent or Guardian.\""
					+ "}"
					);
				
				try {
					Log.v("childrenList.getString(position)", childrenList.getString(position));
					sendNotificationsTo(childrenList.getString(position), data);
					
				} catch (JSONException e) {
					Log.w("exception", "Channel null");
				}
			} catch (JSONException e) {
				Log.w("exception", "JSONObject null");
			}
		}
		
	}
	
	/*********************************
	 * Send Notifications to Child 
	 **********************************/
	
	public void sendNotificationsTo(String deviceChannel, JSONObject data){
		ParsePush push = new ParsePush();
		push.setChannel(deviceChannel);
		push.setData(data); 
		push.sendInBackground();
	}
	
	/*********************************
	 * Console Activity Behavior
	 **********************************/
	
	@Override
	public void onBackPressed(){
		
	    if (fragmentManager.getBackStackEntryCount() > 0) {
	    	fragmentManager.popBackStack();
	    } else {
	        super.onBackPressed();
	    }	
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	/*********************************
	 * ActionBar MenuItems
	 **********************************/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_console_menu_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		logOut();
	    		return true;
		    case R.id.action_settings:
	    		//SettingsFragment settingsFragment = new SettingsFragment();
				//fragmentManager.beginTransaction()
	            //.add(R.id.console,settingsFragment)
	            //.addToBackStack(null)
	            //.commit();
				
				Intent intent = new Intent(ConsoleActivity.this, SettingsActivity.class);
	            startActivity(intent);
				return true;
		    case R.id.action_add_device:
		    	//Add child Installation to parent User
		    	String childChannel = app.DEVICE_CHANNEL + install.getObjectId();
		    	currentUser.addUnique("children",childChannel);
		    	currentUser.saveInBackground();
		    	adapter.add(childChannel);
		    	adapter.notifyDataSetChanged();
		    	return true;
	        case R.id.action_signout:
	    		logOut();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	//Add parent User to child/current Installation 
	public void logOut(){
		if (currentUser != null) {
			Log.w("Logging out: ", currentUser.getObjectId());
			String parentChannel = app.PARENT_CHANNEL + currentUser.getObjectId();
			install.addUnique("channels", parentChannel);
			install.saveInBackground();
			ParseUser.logOut();
		} 	
		onBackPressed();
	}
	
}
