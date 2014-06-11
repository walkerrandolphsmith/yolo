package com.yolo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {
	
	public static boolean checkInternetConnection(Context context){
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo i = conMgr.getActiveNetworkInfo();
	    
		boolean response = false;
	    if (i == null)
	        response = false;
	    else if (!i.isConnected())
	        response = false;
	    else if (!i.isAvailable())
	        response = false;
	    else
	        response = true;
	    return response;
	}

}
