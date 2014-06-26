package com.yolo.models;

import java.util.ArrayList;

import org.json.JSONArray;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class User extends ParseUser{
		
	public User(String username, String email, String password){
		this.setEmail(email);
		this.setUsername(username);
		this.setPassword(password);
	}
	
	public User(){

	}
	public String getPhone(){
		return getString("phone");
	}
	
	public void setPhone(String phone){
		this.put("phone", phone);
	}
	
	public JSONArray getChildren(){
		return getJSONArray("children");
	}	
	
	public void setChildren(ArrayList<ParseObject> children) {
		this.put("children", children);
	}
	
	public boolean getReceivePushNotifications(){
		return getBoolean("receivePushNotifications");
	}
	
	public void setReceivePushNotifications(boolean willReceive){
		this.put("receivePushNotifications", willReceive);
	}
	
	public boolean getReceiveEmails(){
		return getBoolean("receiveReceiveEmails");
	}
	
	public void setReceiveEmails(boolean willReceive){
		this.put("receiveReceiveEmails", willReceive);
	}
	
	public boolean getReceiveSMS(){
		return getBoolean("receiveSMS");
	}
	
	public void setReceiveSMS(boolean willReceive){
		this.put("receiveSMS", willReceive);
	}
	
	public void setAllNotificationPrefrences(boolean isactive){
		this.put("receivePushNotifications", isactive);
		this.put("receiveSMS", isactive);
		this.put("receiveReceiveEmails", isactive);
	}
}
