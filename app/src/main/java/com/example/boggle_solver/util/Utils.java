package com.example.boggle_solver.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;

import static androidx.core.content.ContextCompat.startActivity;

public class Utils {

    public static void changeActivity(Context context, Class<?> c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    public static void makeAlert(String message, Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
