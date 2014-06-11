package com.yolo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.parse.ParseUser;
import com.yolo.fragments.SettingsFragment;
import com.yolo.fragments.UpdateUserFragment;
import com.yolo.models.User;

public class ConsoleActivity extends Activity {
	
	private String URL = "https://accounts.google.com/ServiceLogin?service=androidconsole&passive=3600&continue=https%3A%2F%2Fwww.google.com%2Fandroid%2Fdevicemanager&followup=https%3A%2F%2Fwww.google.com%2Fandroid%2Fdevicemanager";
	public User currentUser;
	private FragmentManager fragmentManager;
	public SharedPreferences prefs;
	WebView admWebView;
	
	/*********************************
	 * OnCreate
	 **********************************/

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

    	currentUser = (User) ParseUser.getCurrentUser();
		fragmentManager = getFragmentManager();
		prefs = getPreferences(MODE_PRIVATE);

		admWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = admWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		admWebView.setWebViewClient(new ADMWebViewClient(currentUser));
		admWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
		admWebView.loadUrl(URL);
	}
	
	public class ADMWebViewClient extends WebViewClient{
		String username;
		String password;
		public ADMWebViewClient(ParseUser user){
			this.username = user.getString("ghostUsername");
			this.password = user.getString("ghostPassword");
		}
		@Override
        public void onPageFinished(WebView view, String url) {
			StringBuilder sb = new StringBuilder();
			sb.append("javascript:");
			sb.append("document.getElementById('Email').value = '"+username+"';");
			sb.append("javascript:document.getElementById('Passwd').value = '"+password+"';");
			sb.append("if('" + username + "' != 'username'){ document.getElementById('signIn').click() }");
            view.loadUrl(sb.toString());
		}
	}
	
	public class WebAppInterface {
		public WebAppInterface(ConsoleActivity a){
			
		}
	}
	
	/*********************************
	 * Back Button
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
	public void onDestroy(){
		super.onDestroy();
		admWebView.removeAllViews(); 
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.console);
		layout.removeView(admWebView);
		admWebView.stopLoading();
		admWebView.setWebViewClient(null);
		admWebView.destroy();
		admWebView = null;
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
	    		SettingsFragment settingsFragment = new SettingsFragment();
				fragmentManager.beginTransaction()
	            .add(R.id.console,settingsFragment)
	            .addToBackStack(null)
	            .commit();
				return true;
	    	case R.id.action_update_user:
	    		UpdateUserFragment fragment = new UpdateUserFragment();
				fragmentManager.beginTransaction()
	            .add(R.id.console, fragment)
	            .addToBackStack(null)
	            .commit();
				return true;
	        case R.id.action_signout:
	    		logOut();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void logOut(){
		System.out.println("hey");
		if (currentUser != null) {
			MainActivity.parentId = currentUser.getObjectId();
		} 	
		ParseUser.logOut();
		onBackPressed();
	}
	
}
