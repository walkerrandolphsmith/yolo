package com.yolo;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ADMWebViewClient extends WebViewClient {

	public void onPageStarted(WebView view, String url, Bitmap favicon){
		super.onPageStarted(view, url, favicon);
	}
	
	public boolean shouldOverrideUrlLoading(WebView view, String url){
		return true;
	}
	
	public void onPageFinished(WebView view, String url){
		super.onPageFinished(view, url);

	}
	
	public void onRecieveError(WebView view, int errorCode, String description, String failingUrl){
		
	}
	
}
