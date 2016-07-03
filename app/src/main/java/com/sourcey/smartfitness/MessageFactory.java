package com.sourcey.smartfitness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class MessageFactory {
    private static final String TAG = "MessageFactory";

    public void CreateMsg(String title, String msg, Context context) {
        try {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
            dlgAlert.setTitle(title);
            dlgAlert.setMessage(msg);
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        } catch (Exception ex) {
            Log.e(TAG, "Error creating message : " + ex.toString());
        }
    }
}
