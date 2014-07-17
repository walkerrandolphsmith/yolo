package com.yolo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.yolo.R;
import com.yolo.list_adapters.ListAdapterChildren;
import com.yolo.models.User;
import com.yolo.util.SettingsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                    if(obj != null)
                    adapter.mChildren.put(adapter.mChildren.length(), obj);
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e){

            }
        }
    };

    private BroadcastReceiver editDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String name = intent.getStringExtra("name");
            int editedPosition = intent.getIntExtra("position", 0);

            try {
                JSONObject ob = adapter.mChildren.getJSONObject(editedPosition);
                ob.put("name", name);
                adapter.mChildren.put(editedPosition,ob);
                adapter.notifyDataSetChanged();
                currentUser.getChildren().put(editedPosition,ob);
                currentUser.saveInBackground();
            }catch (JSONException e){

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(remoteLockReceiver, new IntentFilter("com.yolo.action.ADDDEVICE"));
        registerReceiver(editDeviceReceiver, new IntentFilter("com.yolo.action.EDITDEVICE"));
    }
    @Override
    public void onPause()
    {
        super.onPause();
        //unregisterReceiver(remoteLockReceiver);
        //unregisterReceiver(editDeviceReceiver);

    }
		
	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);

		currentUser = (User) ParseUser.getCurrentUser();

        adapter = new ListAdapterChildren(this, currentUser.getChildren());
        final SwipeListView mListView = (SwipeListView)findViewById(R.id.swipelist);
        mListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {

            }

            @Override
            public void onClosed(int position, boolean fromRight) {

            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(final int position, int action, boolean right) {

            }

            @Override
            public void onStartClose(int position, boolean right) {

            }

            @Override
            public void onClickFrontView(int position) {

            }

            @Override
            public void onClickBackView(int position) {

            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                }
                adapter.notifyDataSetChanged();
            }

        });
		mListView.setAdapter(adapter);
        load(mListView);
	}

    public void load (SwipeListView mListView) {
        SettingsManager settings = SettingsManager.getInstance();
        mListView.setSwipeMode(settings.getSwipeMode());
        mListView.setSwipeActionLeft(settings.getSwipeActionLeft());
        mListView.setSwipeActionRight(settings.getSwipeActionRight());
        mListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
        mListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
        mListView.setAnimationTime(settings.getSwipeAnimationTime());
        mListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
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

    /*********************************
     * Remote Lock Device Item Click Listener
     **********************************/

    public class remoteLockListener implements View.OnClickListener {
        public int position;
        public remoteLockListener (int position) {this.position = position;}
        @Override
        public void onClick(View view) {
            JSONObject data = null;
            try {
                data = new JSONObject(
                        "{"
                                + "\"action\": \"com.example.UPDATE_STATUS\","
                                +  "\"alert\": \"Your phone has been locked by Yolo. Contact Parent or Guardian.\""
                                + "}"
                );
                try {
                    Log.v("childrenList.getString(position)", adapter.mChildren.getJSONObject(position).toString());

                    JSONObject child = adapter.mChildren.getJSONObject(position);
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
     * Remote Lock Device Item Click Listener
     **********************************/

    public class deleteChildListener implements View.OnClickListener {
        public int position;
        public deleteChildListener (int position) {this.position = position;}
        @Override
        public void onClick(View view) {
            adapter.mChildren.remove(position);
            currentUser.getChildren().remove(position);
            currentUser.saveInBackground();
            adapter.notifyDataSetChanged();
        }
    }

}
