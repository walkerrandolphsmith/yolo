package com.yolo.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
	
	public ListAdapterChildren adapter;
		
	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);

		currentUser = (User) ParseUser.getCurrentUser();

        adapter = new ListAdapterChildren(this, currentUser.getChildren());
        if(getIntent().getBooleanExtra("edited", false)){
           updateChild();
        }

        if(getIntent().getBooleanExtra("added", false)){
            addChild();
        }

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
        //Configure the Swipe List View
        SettingsManager settings = SettingsManager.getInstance();
        mListView.setSwipeMode(settings.getSwipeMode());
        mListView.setSwipeActionLeft(settings.getSwipeActionLeft());
        mListView.setSwipeActionRight(settings.getSwipeActionRight());
        mListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
        mListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
        mListView.setAnimationTime(settings.getSwipeAnimationTime());
        mListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
	}

    private int convertDpToPixel(float dp) {
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

    public void addChild(){
        new AddTask(this).execute();
    }

    public void updateChild(){
        String name = getIntent().getStringExtra("name");
        int editedPosition = getIntent().getIntExtra("position", 0);

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

    public void logOut(){
        if (currentUser != null) {
            ParseUser.logOut();
        }
        onBackPressed();
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
                intent = new Intent(ConsoleActivity.this, ChildAddActivity.class);
                startActivity(intent);
		    	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    private class AddTask extends AsyncTask<Void, Void, JSONObject> {

        private ConsoleActivity activity;

        public AddTask(ConsoleActivity activity){
            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            //Add Parent to Child Device
            String parentChannel = getApp().PARENT_CHANNEL + currentUser.getObjectId();
            getApp().getInstall().addUnique("channels", parentChannel);
            getApp().getInstall().saveInBackground();

            //Add Child to Parent's List of Children
            String channel = getApp().DEVICE_CHANNEL + getApp().getInstall().getObjectId();
            String name = getIntent().getStringExtra("name");

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
            return obj;
        }

        protected void onPostExecute(JSONObject obj){
            if(obj != null) {
                try {
                    activity.adapter.mChildren.put(adapter.mChildren.length(), obj);
                } catch (JSONException e){

                }
            }
            activity.adapter.notifyDataSetChanged();
        }


    }


}
