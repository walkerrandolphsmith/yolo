package com.yolo.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yolo.ListAdapterChildren;
import com.yolo.R;
import com.yolo.models.User;

public class ConsoleActivity extends BaseActivity {
	
	private ListAdapterChildren adapter;

    private BroadcastReceiver remoteLockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.w("add device intent", "intent");
            String channel = app.DEVICE_CHANNEL + install.getObjectId();
            String name = intent.getStringExtra("name");

            JSONObject obj = new JSONObject();
            boolean isUnique = true;

            try {
                obj.put("channel", channel);
                obj.put("name", name);
                JSONArray children = currentUser.getChildren();
                for(int i = 0; i < children.length(); i++){
                    JSONObject child = children.getJSONObject(i);
                    String childChannel = (String) child.get("channel");
                    if(childChannel.equalsIgnoreCase(channel)){
                        isUnique = false;
                    }
                }
                if(isUnique){
                    currentUser.addUnique("children", obj);
                    currentUser.saveInBackground();
                }
            }
            catch (JSONException e){

            }
            try {
                if(isUnique) {
                    adapter.mChildren.put(adapter.mChildren.length(), obj);
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e){

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(remoteLockReceiver, new IntentFilter("com.yolo.action.ADDDEVICE"));
    }
    @Override
    public void onPause()
    {
        unregisterReceiver(remoteLockReceiver);
    }
		
	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);

		currentUser = (User) ParseUser.getCurrentUser();

		final ListView mListView = (ListView)findViewById(android.R.id.list);
		adapter = new ListAdapterChildren(this);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new remoteLockDeviceItemClickListener()); 
	}
	
	/*********************************
	 * Remote Lock Device Item Click Listener
	 **********************************/
	
	public class remoteLockDeviceItemClickListener implements OnItemClickListener {
		public JSONArray childrenList;
		public remoteLockDeviceItemClickListener(){
			this.childrenList = currentUser.getChildren();
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
                    JSONObject child = childrenList.getJSONObject(position);
					sendNotificationsTo(child.getString("channel"), data);
					
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
				Intent intent = new Intent(ConsoleActivity.this, SettingsActivity.class);
	            startActivity(intent);
				return true;
		    case R.id.action_add_device:
                intent = new Intent(ConsoleActivity.this, AddDeviceActivity.class);
                startActivity(intent);
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
