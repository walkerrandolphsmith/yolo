package com.yolo.util;

import com.fortysevendeg.swipelistview.SwipeListView;

public class SettingsManager {

    private int swipeMode = SwipeListView.SWIPE_MODE_LEFT;
    private boolean swipeOpenOnLongPress = true;
    private boolean swipeCloseAllItemsWhenMoveList = true;
    private long swipeAnimationTime = 0;
    private float swipeOffsetLeft = 0;
    private float swipeOffsetRight = 0;
    private int swipeActionLeft = SwipeListView.SWIPE_ACTION_REVEAL;
    private int swipeActionRight = SwipeListView.SWIPE_ACTION_REVEAL;

    private static SettingsManager settingsManager = new SettingsManager();

    public static SettingsManager getInstance() {
        return settingsManager;
    }

    public static SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public static void setSettingsManager(SettingsManager settingsManager) {
        SettingsManager.settingsManager = settingsManager;
    }

    public long getSwipeAnimationTime() {
        return swipeAnimationTime;
    }

    public void setSwipeAnimationTime(long swipeAnimationTime) {
        this.swipeAnimationTime = swipeAnimationTime;
    }

    public boolean isSwipeCloseAllItemsWhenMoveList() {
        return swipeCloseAllItemsWhenMoveList;
    }

    public void setSwipeCloseAllItemsWhenMoveList(boolean swipeCloseAllItemsWhenMoveList) {
        this.swipeCloseAllItemsWhenMoveList = swipeCloseAllItemsWhenMoveList;
    }

    public int getSwipeMode() {
        return swipeMode;
    }

    public void setSwipeMode(int swipeMode) {
        this.swipeMode = swipeMode;
    }

    public float getSwipeOffsetLeft() {
        return swipeOffsetLeft;
    }

    public void setSwipeOffsetLeft(float swipeOffsetLeft) {
        this.swipeOffsetLeft = swipeOffsetLeft;
    }

    public float getSwipeOffsetRight() {
        return swipeOffsetRight;
    }

    public void setSwipeOffsetRight(float swipeOffsetRight) {
        this.swipeOffsetRight = swipeOffsetRight;
    }

    public boolean isSwipeOpenOnLongPress() {
        return swipeOpenOnLongPress;
    }

    public void setSwipeOpenOnLongPress(boolean swipeOpenOnLongPress) {
        this.swipeOpenOnLongPress = swipeOpenOnLongPress;
    }

    public int getSwipeActionLeft() {
        return swipeActionLeft;
    }

    public void setSwipeActionLeft(int swipeActionLeft) {
        this.swipeActionLeft = swipeActionLeft;
    }

    public int getSwipeActionRight() {
        return swipeActionRight;
    }

    public void setSwipeActionRight(int swipeActionRight) {
        this.swipeActionRight = swipeActionRight;
    }
}