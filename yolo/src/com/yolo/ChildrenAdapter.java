package com.yolo;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChildrenAdapter extends ArrayAdapter<String> {
	private JSONArray mChildren;

	public ChildrenAdapter(Context context, JSONArray children) {
		super(context, R.layout.each_child);
		this.mChildren = children;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.each_child, parent, false);
		}
		TextView userNameTextView = (TextView) convertView
				.findViewById(R.id.name);
		try {
			userNameTextView.setText(mChildren.getString(position));
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