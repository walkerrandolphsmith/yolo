package com.yolo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.parse.PushService;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

	public interface CallBackFunction {
		public void call(String o);
	}
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		checkInternetConnection(context, new CallBackFunction(){
			@Override
			public void call(String o) {
				if(o.equals("true"))
					PushService.startServiceIfRequired(context);
			}
		});
	} 
	
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
	
	public boolean checkInternetConnection(Context context, final CallBackFunction callback){
		
		boolean response = checkInternetConnection(context);
	    
		if(response){
			new ParseAsyncTask(new CallBackFunction()
		    {

		        @Override
		        public void call(String obj)
		        {
		        	boolean result;
		            String url = "parse.com";
		            result = (url.equals("") ? false : true);
		          
		            callback.call(result + "");
		        }
		    }).execute((Void[])null);
		}else{
			callback.call(response+"");
		}
		
	    return response;
	}
	
	public class ParseAsyncTask extends AsyncTask<Void, Void, String>{

		private CallBackFunction callback;

		public ParseAsyncTask(CallBackFunction f) {
		    this.callback = f;
		}

		@Override
		protected String doInBackground(Void... params) {
			if(callback != null)
				callback.call("");
		    return null;
		}

	}
}
