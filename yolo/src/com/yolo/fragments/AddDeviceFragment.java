package com.yolo.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yolo.ConsoleActivity;
import com.yolo.R;

public class AddDeviceFragment extends Fragment {
	
	Activity activity;
	
	EditText mName;
	EditText mPassword;
	Button addChildButton;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_device, container, false);
        mName = (EditText) v.findViewById(R.id.name);
		mPassword = (EditText) v.findViewById(R.id.password);
        addChildButton = (Button)v.findViewById(R.id.add_device);
        addChildButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				InputMethodManager keyboard = (InputMethodManager)activity.getSystemService(
					      Context.INPUT_METHOD_SERVICE);
				keyboard.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
					
				String name = mName.getText().toString();
				String password = mPassword.getText().toString();
				
				ParseUser currentUser = ParseUser.getCurrentUser();
				if (currentUser != null) {
				    Account[] accounts=AccountManager.get(activity).getAccountsByType("com.google");
				    for(Account account: accounts)
				    {
				        String email = account.name;		        
				        int index = email.lastIndexOf("@");
				        String username = email.substring(0, index);	        
				        
				        ParseObject child = new ParseObject("Child");
				        child.put("name", name);
				        child.put("username", username);
				        child.put("password", password);
				        child.put("email", email);
				        child.put("parent", currentUser.getObjectId());
				        child.saveInBackground();
				        ConsoleActivity ca = (ConsoleActivity) activity;
				        ca.children.add(child);
				    }
				}
				activity.onBackPressed();	
			}
	    });
        return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		activity = getActivity();
		
	}
}
