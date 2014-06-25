package com.yolo.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.Switch;
import com.yolo.R;

import com.yolo.activities.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity mMainActivity;
	private ImageView mImageView;
	private Switch mSwitch;
	
	public MainActivityTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mMainActivity = getActivity();
		mImageView = (ImageView) mMainActivity.findViewById(R.id.logo);
		mSwitch = (Switch) mMainActivity.findViewById(R.id.isDrivingSwitch);
		
	}
	
	
	public void testPreConditions(){
		assertNotNull("mMainActivity is null", mMainActivity);
		assertNotNull("mImageView is null", mImageView);
		assertNotNull("mSwitch is null", mSwitch);
	}
	
	//All test cases come after testPreConditions()
	
	public void testMainSwitch_getTextOff() {
		final String expected = mMainActivity.getString(R.string.off);
		final String actual = mSwitch.getTextOff().toString();
		
		assertEquals(expected, actual);
	}
	
	public void testMainSwitch_getTextOn() {
		final String expected = mMainActivity.getString(R.string.on);
		final String actual = mSwitch.getTextOn().toString();
		
		assertEquals(expected, actual);
	}
	
	public void testMainSwitch_getIsDriving() {
		boolean expected = mSwitch.isChecked();
		boolean actual = mMainActivity.getIsDriving();
		
		assertEquals(expected, actual);
	}
	
	public void testMainSwitch_getIsDrivingOn() {
		mMainActivity.runOnUiThread(
			new Runnable() {
				public void run(){
					boolean expected = true;
					mSwitch.setChecked(expected);
					boolean actual = mMainActivity.getIsDriving();
						
					assertEquals(expected, actual);
				}
			});
	}
	
	public void testMainSwitch_getIsDrivingOff() {
		mMainActivity.runOnUiThread(
			new Runnable() {
				public void run(){
					boolean expected = false;
					mSwitch.setChecked(expected);
					boolean actual = mMainActivity.getIsDriving();
					
					assertEquals(expected, actual);
				}
			});
	}
	
}
