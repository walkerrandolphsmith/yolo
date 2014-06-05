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

	public void setPhone(String phone){
		this.put("phone", phone);
	}
}
