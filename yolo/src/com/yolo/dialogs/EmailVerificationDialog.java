package com.yolo.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.yolo.activities.SettingsActivity;

public class EmailVerificationDialog extends AlertDialog {

    private long tag;

    public EmailVerificationDialog(final SettingsActivity activity) {
        super(activity);
        setMessage("You must verify email before updating account. Would you like to resend a confirmation email?");
        setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id){
               activity.currentUser.setEmail(activity.currentUser.getEmail());
                activity.currentUser.saveInBackground();
            }
        });
        setButton(AlertDialog.BUTTON_NEGATIVE, "No", new OnClickListener(){
            public void onClick(final DialogInterface dialog, final int id){
                cancel();
            }
        });
    }

    public EmailVerificationDialog(final Context context, final int theme) {
        super(context, theme);
    }

    public EmailVerificationDialog(final Context context, final boolean cancelable, final OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public long getTag() {
        return tag;
    }

    public void setTag(final long tag) {
        this.tag = tag;
    }
}
