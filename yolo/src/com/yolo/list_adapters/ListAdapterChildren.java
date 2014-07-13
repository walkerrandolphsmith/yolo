package com.yolo.list_adapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yolo.R;
import com.yolo.activities.ConsoleActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListAdapterChildren extends ArrayAdapter<String> {
	private ViewHolder viewHolder;
	private ConsoleActivity activity;
	public JSONArray mChildren;

	public ListAdapterChildren(ConsoleActivity activity) {
		super(activity, R.layout.each_child);
		this.activity = activity;
		this.mChildren = activity.currentUser.getChildren();
	}
	
	private class ViewHolder {
		TextView device_channel, name;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.each_child, null);
			viewHolder = new ViewHolder();
			viewHolder.device_channel = (TextView) convertView
					.findViewById(R.id.device_channel_id);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		try {
            JSONObject child = mChildren.getJSONObject(position);
			viewHolder.device_channel.setText(child.getString("channel"));
			viewHolder.name.setText(child.getString("name"));
		} catch (JSONException e) {
			Log.w("exception", "no getString");
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return mChildren.length();
	}

}