package com.yolo.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

public class NoGpsDialog extends AlertDialog {

    private long tag;

    public NoGpsDialog(final Context context) {
        super(context);
        setCancelable(false);
        setMessage("Your GPS seems to be disabled, please enable it.");
        setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id){
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
    }

    public NoGpsDialog(final Context context, final int theme) {
        super(context, theme);
    }

    public NoGpsDialog(final Context context, final boolean cancelable, final OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public long getTag() {
        return tag;
    }

    public void setTag(final long tag) {
        this.tag = tag;
    }
}
