package com.yolo.models;

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
	
	public String getGhostUsername(){
		return getString("ghostUsername");
	}
	
	public void setGhostUsername(String username){
		this.put("ghostUsername", username);
	}
	
	public String getGhostPassword(){
		return getString("ghostPassword");
	}
	
	public void setGhostPassword(String password){
		this.put("ghostPassword", password);
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
}
