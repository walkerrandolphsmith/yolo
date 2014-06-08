package com.yolo.models;

import java.util.ArrayList;

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
	
	public String getChildren(){
		return getString("children");
	}
	
	public void setChildren(ArrayList<ParseObject> children){
		this.put("children", children);
	}
}
