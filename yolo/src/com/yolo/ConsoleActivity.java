package com.yolo;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

public class ConsoleActivity extends Activity {
	
	Switch switchPushNotification;
	Switch switchEmail;
	Switch switchSMS;
	
	SharedPreferences prefs;
	
	ChildrenAdapter adapter;
	List<ParseObject> children; 
	
	
	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);
		
		PushService.unsubscribe(this, "PC"); //unsubscribe from the public channel
		
		prefs = getPreferences(MODE_PRIVATE);
		
		 switchPushNotification = (Switch) findViewById(R.id.receivePushNotification);
	     if (switchPushNotification != null) {
	 		boolean isRecevingPushNotifications = prefs.getBoolean("receivePushNotifications", true);
	      	switchPushNotification.setChecked(isRecevingPushNotifications);
	     }
	        
	    switchEmail = (Switch) findViewById(R.id.receiveEmail);
	    if (switchEmail != null) {
	 		boolean isReceivingEmails = prefs.getBoolean("receiveEmails", true);
	 		switchEmail.setChecked(isReceivingEmails);
	    }
	        
	    switchSMS = (Switch) findViewById(R.id.receiveSMS);
	    if (switchSMS != null) {
	 		boolean isReceivingSMS = prefs.getBoolean("receiveSMS", true);
	 		switchSMS.setChecked(isReceivingSMS);
	    }
	    
	    Button addChildButton = (Button) findViewById(R.id.remoteManager);
	    addChildButton.setOnClickListener(new AddChildButtonListener(this));
	    
	    children = new ArrayList<ParseObject>();
	    ParseQuery<ParseObject> query = ParseQuery.getQuery("Child");
	    query.whereEqualTo("parent", ParseUser.getCurrentUser().getObjectId());
	    query.findInBackground(new FindCallback<ParseObject>() {
	        public void done(List<ParseObject> scoreList, ParseException e) {
	            if (e == null) {
	                for(ParseObject child : scoreList){
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
              public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
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
	 * Add Child's Device
	 **********************************/
	
	public class AddChildButtonListener implements OnClickListener {
	
		ConsoleActivity ca;
    	
    	public AddChildButtonListener(ConsoleActivity ca){
    		this.ca = ca;
    	}
		@Override
		public void onClick(View v) {
	
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) {
			    Account[] accounts=AccountManager.get(ca).getAccountsByType("com.google");
			    for(Account account: accounts)
			    {
			        String email = account.name;
			        //AccountManager manager = AccountManager.get(ca);
	                //String password = manager.getPassword(account);			        
			        int index = email.lastIndexOf("@");
			        String username = email.substring(0, index);	        
			        
			        ParseObject child = new ParseObject("Child");
			        child.put("username", username);
			        child.put("password", "snoodles");
			        child.put("email", email);
			        child.put("parent", currentUser.getObjectId());
			        child.saveInBackground();
			        children.add(child);
			    }
			}
	        adapter.notifyDataSetChanged();
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
	       	 
	        	 SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
	        	 
	        	 boolean isPush = (switchPushNotification.isChecked() ? true : false);
	        	 boolean isEmail = (switchEmail.isChecked() ? true : false);
	        	 boolean isSMS = (switchSMS.isChecked() ? true : false);
	        	 
	 			 editor.putBoolean("receivePushNotifications", isPush);
	 			 editor.putBoolean("receiveEmails", isEmail);
	 			 editor.putBoolean("receiveSMS", isSMS);
	 			 editor.apply();
	 			 
	 			 MainActivity.notificationTypes[0] = isPush;
	 			 MainActivity.notificationTypes[1] = isEmail;
	 			 MainActivity.notificationTypes[2] = isSMS;
	        	 
	        	finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}
