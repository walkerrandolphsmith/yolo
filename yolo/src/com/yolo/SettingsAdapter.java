package com.yolo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.yolo.activities.SettingsActivity;

public class SettingsAdapter extends ArrayAdapter<String> {

	private final static int PUSH_NOTIFICATIONS = 0;
	private final static int SMS = 1;
	private final static int EMAIL = 2;

	boolean[] checkBoxState;

	boolean checkAll_flag = false;
	boolean checkItem_flag = false;

	ViewHolder viewHolder;
	SettingsActivity activity;
	String[] settings;

	public SettingsAdapter(SettingsActivity activity, int textViewResourceId,
			String[] settings, CheckBox selectAll) {

		super(activity, textViewResourceId, settings);
		this.activity = activity;
		this.settings = settings;
		checkBoxState = new boolean[settings.length];
		checkBoxState[PUSH_NOTIFICATIONS] = activity.currentUser
				.getReceivePushNotifications();
		checkBoxState[SMS] = activity.currentUser.getReceiveSMS();
		checkBoxState[EMAIL] = activity.currentUser.getReceiveEmails();

		selectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton selectAll,
					boolean isChecked) {
				allSelectedChange(isChecked);
				notifyDataSetChanged();
			}
		});
	}

	private void allSelectedChange(boolean isChecked) {
		for (int i = 0; i < checkBoxState.length; i++) {
			checkBoxState[i] = isChecked;
			viewHolder.checkBox.setChecked(checkBoxState[i]);
		}
		activity.currentUser.setReceivePushNotifications(isChecked);
		activity.currentUser.setReceiveSMS(isChecked);
		activity.currentUser.setReceiveEmails(isChecked);
		activity.currentUser.saveEventually();
	}

	private class ViewHolder {
		// ImageView photo;
		TextView place_id, name;
		CheckBox checkBox;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parnet) {
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.each_settings, null);
			viewHolder = new ViewHolder();
			// viewHolder.photo=(ImageView)
			// convertView.findViewById(R.id.photo);
			viewHolder.place_id = (TextView) convertView
					.findViewById(R.id.installation_id);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox);

			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		// int photoId=(Integer) places.get(position).get("photo");
		// viewHolder.photo.setImageDrawable(getResources().getDrawable(photoId));
		viewHolder.place_id.setText(settings[position]);
		viewHolder.name.setText(settings[position]);

		viewHolder.checkBox.setChecked(checkBoxState[position]);
		viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					checkBoxState[position] = true;
					setPreference(position, true);
				} else {
					checkBoxState[position] = false;
					setPreference(position, false);
				}
			}
		});
		return convertView;
	}

	public void setPreference(int position, boolean state) {
		switch (position) {
		case PUSH_NOTIFICATIONS:
			activity.currentUser.setReceivePushNotifications(state);
			activity.currentUser.saveEventually();
			return;
		case SMS:
			activity.currentUser.setReceiveSMS(state);
			activity.currentUser.saveEventually();
			return;
		case EMAIL:
			activity.currentUser.setReceiveEmails(state);
			activity.currentUser.saveEventually();
			return;
		}
	}

}