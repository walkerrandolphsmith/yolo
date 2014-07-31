package com.yolo.list_adapters;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.yolo.R;
import com.yolo.activities.ChildEditActivity;
import com.yolo.activities.ChildLockActivity;
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
        ImageView delete, edit, lock;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.each_child, null);
			viewHolder = new ViewHolder();
			viewHolder.device_channel = (TextView) convertView
					.findViewById(R.id.device_channel_id);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete);
            viewHolder.edit = (ImageView) convertView.findViewById(R.id.edit);
            viewHolder.lock = (ImageView) convertView.findViewById(R.id.lock);
			convertView.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

        ((SwipeListView) parent).recycle(convertView, position);
		try {
            JSONObject child = activity.currentUser.getChildren().getJSONObject(position);
			viewHolder.device_channel.setText(child.getString("channel"));
			viewHolder.name.setText(child.getString("name"));
		} catch (JSONException e) {
			Log.w("exception", "no getString");
		}
        viewHolder.lock.setOnClickListener(new lockListener(position));
        viewHolder.delete.setOnClickListener(new deleteListener(position));
        viewHolder.edit.setOnClickListener(new editListener(position));

        return convertView;
	}

	@Override
	public int getCount() {
		return mChildren.length();
	}


    public class deleteListener implements View.OnClickListener {

        public int position;

        public deleteListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v){
            new DeleteTask().execute(new Integer[]{ position });
        }
    }

    public class editListener implements View.OnClickListener {

        public int position;

        public editListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v){
            Intent intent = new Intent(activity, ChildEditActivity.class);
            intent.putExtra("position", position);
            activity.startActivity(intent);
        }
    }

    public class lockListener implements View.OnClickListener {

        public int position;

        public lockListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v){
            String channel = "";
            try{
                JSONObject child = mChildren.getJSONObject(position);
                channel = child.getString("channel");
            }catch (JSONException e){
                Log.w("JSON Exception", "Error retrieving child from list.");
            }
            Intent intent = new Intent(activity, ChildLockActivity.class);
            intent.putExtra("channel", channel);
            intent.putExtra("position", position);
            activity.startActivity(intent);
        }
    }

    /*********************************
     * Async Task Init Parse
     **********************************/

    private class DeleteTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... config) {

            int position = config[0];
            activity.currentUser.getChildren().remove(position);
            activity.currentUser.saveInBackground();
            return position;
        }

        protected void onPostExecute(Integer position){
            mChildren.remove(position);
            notifyDataSetChanged();
        }
    }

}