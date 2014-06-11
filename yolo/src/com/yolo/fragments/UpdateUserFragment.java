package com.yolo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yolo.R;

public class UpdateUserFragment extends Fragment {
	
	Activity activity;
	
	EditText mUserName;
	EditText mEmail;
	EditText mPhone;
	EditText mGhostUsername;
	EditText mGhostPassword;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update_user, container, false);
        mUserName = (EditText)v.findViewById(R.id.username);
	    mEmail = (EditText)v.findViewById(R.id.email);
	    mPhone = (EditText)v.findViewById(R.id.phone);
	    mGhostUsername = (EditText)v.findViewById(R.id.ghostUsername);
	    mGhostPassword = (EditText)v.findViewById(R.id.ghostPassword); 
	    
	    return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		activity = getActivity();
	}
}
