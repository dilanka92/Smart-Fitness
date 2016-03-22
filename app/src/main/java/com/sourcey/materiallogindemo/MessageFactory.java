package com.sourcey.materiallogindemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MessageFactory {

    public void CreateMsg(String title, String msg, Context window) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(window);
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(msg);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}
