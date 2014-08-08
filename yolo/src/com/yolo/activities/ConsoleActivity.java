package com.yolo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

        adapter = new ListAdapterChildren(this);
        if(getIntent().getBooleanExtra("edited", false)){
           updateChild();
        }

        if(getIntent().getBooleanExtra("added", false)){
            addChild();
        }

        if(getIntent().getBooleanExtra("locked", false)){
            lockChild();
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
        initializeSwipeList(mListView);
	}

    /*********************************
     * Initialize Swipe List View
     **********************************/

    public void initializeSwipeList(SwipeListView mListView){
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


    /*********************************
	 * Send Notifications to Child 
	 **********************************/
	
	public void sendNotificationsTo(String deviceChannel, JSONObject data){
		ParsePush push = new ParsePush();
		push.setChannel(deviceChannel);
		push.setData(data); 
		push.sendInBackground();
	}

    public void lockChild(){
        String password = getIntent().getStringExtra("password");
        int expiration = getIntent().getIntExtra("expiration", 0);
        String channel = getIntent().getStringExtra("channel");
        int position = getIntent().getIntExtra("position", -1);

        Log.w("ConsoleActivity received lock with intent", "Lock sent as Push");
        Log.w("the password to the remote lock ", password);
        Log.w("the expiration or reset to the remote lock ", expiration+"");
        JSONObject data = null;
        try {
            data = new JSONObject(
                    "{"
                            + "\"action\": \"com.example.UPDATE_STATUS\","
                            +  "\"alert\": \"Your phone has been locked by Yolo. Contact Parent or Guardian.\","
                            + "\"password\": \"" + password + "\","
                            + "\"reset\": \"" + expiration + "\""
                            + "}"
            );
            sendNotificationsTo(channel, data);
            if(position > -1) {
                new LockTask().execute(new String[]{password, String.valueOf(position)});
            }
        } catch (JSONException e) {
            Log.w("JSON Exception", "The remote lock did not succeed.");
        }
    }

    /*********************************
     * Edit Children List
     **********************************/

    public void addChild(){

        new AddTask(this).execute();
    }

    public void updateChild(){
        String name = getIntent().getStringExtra("name");
        int position = getIntent().getIntExtra("position", 0);
        new UpdateTask(this).execute(new String[]{name, String.valueOf(position)});
    }

    /*********************************
     * Log Out
     **********************************/

    public void logOut(){
        if (currentUser != null) {
            ParseUser.logOut();
            Log.w("remove shared prefs", "remove loggedIn");
            SharedPreferences.Editor editor = getApp().getSharedPreferences().edit();
            editor.remove("loggedIn");
            editor.commit();
        }
        onBackPressed();
    }


    /*********************************
     * Async Task Update Child
     **********************************/

    private class LockTask extends AsyncTask<String , Void, Void> {

        @Override
        protected Void doInBackground(String ... config) {
            String password = config[0];
            int position = Integer.parseInt(config[1]);

            Log.w(password, config[1]);

            try {
                JSONObject obj = currentUser.getChildren().getJSONObject(position);
                obj.put("password", password);
                currentUser.getChildren().put(position,obj);
                currentUser.saveInBackground();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /*********************************
     * Async Task Add Child
     **********************************/

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
                obj.put("password", "walker");
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

    /*********************************
     * Async Task Update Child
     **********************************/

    private class UpdateTask extends AsyncTask<String , Void, JSONObject[]> {

        private ConsoleActivity activity;

        public UpdateTask(ConsoleActivity activity){
            this.activity = activity;
        }

        @Override
        protected JSONObject[] doInBackground(String ... config) {
            String name = config[0];
            int position = Integer.parseInt(config[1]);

            JSONObject obj = null;
            JSONObject loc = new JSONObject();
            try {
                obj = currentUser.getChildren().getJSONObject(position);
                obj.put("name", name);
                currentUser.getChildren().put(position,obj);
                currentUser.saveInBackground();
                loc.put("position", position);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new JSONObject[]{ obj, loc };

        }

        protected void onPostExecute(JSONObject[] objs){

            JSONObject obj = objs[0];
            JSONObject loc = objs[1];

            if(obj != null){
                try{
                    int position = loc.getInt("position");
                    activity.adapter.mChildren.put(position, obj);
                }catch (JSONException e){

                }
            }
            activity.adapter.notifyDataSetChanged();
        }


    }


}
