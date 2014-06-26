package com.yolo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.yolo.activities.SettingsActivity;

public class ListAdapterSettings extends ArrayAdapter<String> {

	private static final int NOTIFY_PUSH = 0;
	private static final int NOTIFY_SMS = 1;
	private static final int NOTIFY_EMAIL = 2;

	private ViewHolder viewHolder;
	private boolean[] cButtonState;
	private boolean flag = false;

	private SettingsActivity activity;
	private int textViewResourceId;
	private String[] settings;
	private CompoundButton selectAll;
	private boolean isFallback;
	
	public ListAdapterSettings(final SettingsActivity activity, int textViewResourceId,
			String[] settings, CompoundButton selectAll, boolean isFallback) {

		super(activity, textViewResourceId, settings);

		this.activity = activity;
		this.textViewResourceId = textViewResourceId;
		this.settings = settings;
		this.selectAll = selectAll;
		this.isFallback = isFallback;
		
		cButtonState = new boolean[] {
									activity.currentUser.getReceivePushNotifications(),
		                 			activity.currentUser.getReceiveSMS(),
		                            activity.currentUser.getReceiveEmails()
		                            };
		
		selectAll.setChecked(areAllChecked());
		selectAll.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!flag){
					for (int i = 0; i < cButtonState.length; i++) {
						cButtonState[i] = isChecked;
						viewHolder.cbutton.setChecked(cButtonState[i]);
					}
					activity.currentUser.setAllNotificationPrefrences(isChecked);
					activity.currentUser.saveEventually();
					notifyDataSetChanged();
				}
			}
			
		});
	}


	private class ViewHolder {
		TextView preference_id, name;
		CompoundButton cbutton;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parnet) {
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(textViewResourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.preference_id = (TextView) convertView
					.findViewById(R.id.installation_id);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			if (!isFallback) {
				viewHolder.cbutton = (Switch) convertView
						.findViewById(R.id.notificationSwitch);
			} else {
				viewHolder.cbutton = (CheckBox) convertView
						.findViewById(R.id.checkBox);
			}
			convertView.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.preference_id.setText(settings[position]);
		viewHolder.name.setText(settings[position]);

		viewHolder.cbutton.setChecked(cButtonState[position]);
		viewHolder.cbutton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				if (((CompoundButton) v).isChecked()) {
					cButtonState[position] = true;
					setPreference(position, true);
					flag = true;
					selectAll.setChecked(areAllChecked());
					flag = false;
				} else {
					cButtonState[position] = false;
					setPreference(position, false);
					flag = true;
					selectAll.setChecked(false);
					flag = false;
				}
			}
		});
		return convertView;
	}
	
	public void setPreference(int position, boolean state) {
		switch (position) {
		case NOTIFY_PUSH:
			activity.currentUser.setReceivePushNotifications(state);
			return;
		case NOTIFY_SMS:
			activity.currentUser.setReceiveSMS(state);
			return;
		case NOTIFY_EMAIL:
			activity.currentUser.setReceiveEmails(state);
			return;
		}
		activity.currentUser.saveEventually();
	}
	
	public boolean areAllChecked(){
		boolean result = true;
		for (int i = 0; i < cButtonState.length; i++) {
			if (cButtonState[i] != true) {
				result = false;
			}
		}
		return result;
	}
}