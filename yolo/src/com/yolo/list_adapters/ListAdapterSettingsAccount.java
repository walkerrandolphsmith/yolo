package com.yolo.list_adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yolo.R;
import com.yolo.activities.SettingsActivity;

public class ListAdapterSettingsAccount extends ArrayAdapter<String> {

	private static final int USERNAME = 0;
	private static final int PASSWORD = 1;
    private static final int PHONE = 2;
    private static final int EMAIL = 3;

	private ViewHolder viewHolder;

	private SettingsActivity activity;
	private int textViewResourceId;
	private String[] settings;

	public ListAdapterSettingsAccount(final SettingsActivity activity, int textViewResourceId,
                                      String[] settings) {

		super(activity, textViewResourceId, settings);

		this.activity = activity;
		this.textViewResourceId = textViewResourceId;
		this.settings = settings;
	}


	private class ViewHolder {
		ImageView image;
		TextView preference_id, name;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(textViewResourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.image = (ImageView) convertView.findViewById(R.id.list_image);
			setImageView(position);
			viewHolder.preference_id = (TextView) convertView
					.findViewById(R.id.installation_id);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.preference_id.setText(settings[position]);
		viewHolder.name.setText(settings[position]);

		return convertView;
	}
	
	public void setImageView(int position){
		switch(position){
		case USERNAME:
			viewHolder.image.setImageResource(R.drawable.ic_account_username);
			break;
		case PASSWORD:
			viewHolder.image.setImageResource(R.drawable.ic_account_password);
			break;
        case PHONE:
            viewHolder.image.setImageResource(R.drawable.ic_action_add);
            break;
        case EMAIL:
            viewHolder.image.setImageResource(R.drawable.ic_settings_email);
            break;
		}
	}
}