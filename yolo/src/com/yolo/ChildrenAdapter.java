package com.yolo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

public class ChildrenAdapter extends ArrayAdapter<ParseObject>{
	  private Context mContext;
	  private List<ParseObject> mChildren;

	  public ChildrenAdapter(Context context, List<ParseObject> children) {
	      super(context, R.layout.each_child, children);
	      this.mContext = context;
	      this.mChildren = children;
	  }
	  
	  @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	       ParseObject user = getItem(position);    
	       if (convertView == null) {
	          convertView = LayoutInflater.from(getContext()).inflate(R.layout.each_child, parent, false);
	       }
	       TextView userNameTextView = (TextView) convertView.findViewById(R.id.childUsername);
	       TextView passwordTextView = (TextView) convertView.findViewById(R.id.childPassword);
	       userNameTextView.setText(user.getString("username"));
	       passwordTextView.setText(user.getString("password"));
	       return convertView;
	   }
	  
	  @Override
	  public int getCount() {
	  return mChildren.size();
	  }

}
