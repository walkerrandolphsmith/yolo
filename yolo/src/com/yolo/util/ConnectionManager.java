package com.yolo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.yolo.Application;


public class ConnectionManager {
	
	 private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	public static boolean checkInternetConnection(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
	
	public static boolean servicesConnected(Context context) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if(CONNECTION_FAILURE_RESOLUTION_REQUEST == resultCode){
			
		}
		return true;
	}
	
}
