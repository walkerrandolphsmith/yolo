package com.yolo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.yolo.fragments.AddDeviceFragment;
import com.yolo.fragments.SettingsFragment;

public class ConsoleActivity extends Activity {

	private ChildrenAdapter adapter;
	public List<ParseObject> children; 
	
	private FragmentManager fragmentManager;
	public SharedPreferences prefs;
	
	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);
		
		fragmentManager = getFragmentManager();
		prefs = getPreferences(MODE_PRIVATE);
		
		//Remove the public channel
		PushService.unsubscribe(this, "PC"); 

	    children = new ArrayList<ParseObject>();
	    ParseQuery<ParseObject> query = ParseQuery.getQuery("Child");
	    query.whereEqualTo("parent", ParseUser.getCurrentUser().getObjectId());
	    query.findInBackground(new FindCallback<ParseObject>() {
	        public void done(List<ParseObject> childrenList, ParseException e) {
	            if (e == null) {
	                for(ParseObject child : childrenList){
	                	children.add(child);
	                }
                	adapter.notifyDataSetChanged();
	            }
	        }
	    });
	    final ListView childList = (ListView)findViewById(android.R.id.list);
	    adapter = new ChildrenAdapter(this, children);
        childList.setAdapter(adapter);
        
        childList.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
              @Override
              public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) 
              {
                     ParseObject child = (ParseObject) childList.getItemAtPosition(position);
                     String username = child.getString("username");
                     String password = child.getString("password");
                     
                     //Open WebView passing the username and password.
                     Intent intent = new Intent(ConsoleActivity.this, ADMActivity.class);
                     intent.putExtra("EXTRA_USERNAME", username);
                     intent.putExtra("EXTRA_PASSWORD", password);
   	 	             startActivity(intent); 
                     //Change visibility in ADM -> play.google.com/settings
                     //ADM -> google.com/android/devicemanager
              }
        }); 
    
	}
	
	/*********************************
	 * Back Button
	 **********************************/
	
	@Override
	public void onBackPressed(){
		
	    if (fragmentManager.getBackStackEntryCount() > 0) {
	    	fragmentManager.popBackStack();
	        adapter.notifyDataSetChanged();
	    } else {
	        super.onBackPressed();
	    }	
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
		    case R.id.action_settings:
	    		SettingsFragment settingsFragment = new SettingsFragment();
				fragmentManager.beginTransaction()
	            .add(R.id.console,settingsFragment)
	            .addToBackStack(null)
	            .commit();
				return true;
	    	case R.id.action_add_device:
	    		AddDeviceFragment fragment = new AddDeviceFragment();
				fragmentManager.beginTransaction()
	            .add(R.id.console, fragment)
	            .addToBackStack(null)
	            .commit();
				return true;
	        case R.id.action_signout:
	        	ParseUser currentUser = ParseUser.getCurrentUser();
	    		if (currentUser != null) {
	    		  MainActivity.channel = currentUser.getObjectId();
	    		  MainActivity.email = currentUser.getEmail();
	    		  MainActivity.phone = currentUser.getString("phone");
	    		 } else {
	    			 MainActivity.channel = "PC";
	    		 }	
	        	ParseUser.logOut();
	        	finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}
