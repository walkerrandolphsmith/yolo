package com.yolo.models;

import com.parse.ParseUser;

import org.json.JSONArray;

public class User extends ParseUser {

    public User(String username, String email, String password) {
        this.setEmail(email);
        this.setUsername(username);
        this.setPassword(password);
        this.put("children", new JSONArray());
    }

    public User() {

    }

    public boolean getEmailVerified() {
        return getBoolean("emailVerified");
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setPhone(String phone) {
        this.put("phone", phone);
    }

    public JSONArray getChildren() {
        return getJSONArray("children");
    }

    public boolean getReceivePushNotifications() {
        return getBoolean("receivePushNotifications");
    }

    public void setReceivePushNotifications(boolean willReceive) {
        this.put("receivePushNotifications", willReceive);
    }

    public boolean getReceiveEmails() {
        return getBoolean("receiveEmails");
    }

    public void setReceiveEmails(boolean willReceive) {
        this.put("receiveEmails", willReceive);
    }

    public boolean getReceiveSMS() {
        return getBoolean("receiveSMS");
    }

    public void setReceiveSMS(boolean willReceive) {
        this.put("receiveSMS", willReceive);
    }

    public int getReminderFrequency() {
        return getInt("frequency");
    }

    public void setReminderFrequency(int frequency) {
        this.put("frequency", frequency);
    }

}
