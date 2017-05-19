package com.techiespace.projects.musicalquotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class FullScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Inside Receiver", Toast.LENGTH_SHORT).show();
        Intent i1 = new Intent(context, MainActivity.class);    //MainActivity here to be later replaced by FullScreenActivity
        i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i1);
    }
}