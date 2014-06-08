package com.yolo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class ADMActivity extends Activity {
	
	public String URL = "https://accounts.google.com/ServiceLogin?service=androidconsole&passive=3600&continue=https%3A%2F%2Fwww.google.com%2Fandroid%2Fdevicemanager&followup=https%3A%2F%2Fwww.google.com%2Fandroid%2Fdevicemanager";
	
	/*********************************
	 * OnCreate
	 **********************************/

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adm);
		String username = "";
		String password = ""; 
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		     username = extras.getString("EXTRA_USERNAME");
		     password = extras.getString("EXTRA_PASSWORD");
		}
		
		System.out.println(username);
		System.out.println(password);
		
		WebView admWebView = (WebView) findViewById(R.id.webview);
		
		
		WebSettings webSettings = admWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		admWebView.setWebViewClient(new ADMWebViewClient(username, password));
		
		//Web View Automatically makes  "Android" interface available 
		admWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

		admWebView.loadUrl(URL);
	}
	
	public class ADMWebViewClient extends WebViewClient{
		String username;
		String password;
		public ADMWebViewClient(String username, String password){
			this.username = username;
			this.password = password;
		}
		@Override
        public void onPageFinished(WebView view, String url) {
			StringBuilder sb = new StringBuilder();
			sb.append("javascript:");
			sb.append("document.getElementById('Email').value = '"+username+"';");
			sb.append("javascript:document.getElementById('Passwd').value = '"+password+"';");
			sb.append("document.getElementById('signIn').click()");
            view.loadUrl(sb.toString());
		}
	}
	
	public class WebAppInterface {
		public WebAppInterface(ADMActivity a){
			
		}
	}
}
