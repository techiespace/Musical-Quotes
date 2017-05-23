package com.techiespace.projects.musicalquotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FullScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i1 = new Intent(context, FullscreenLyricsActivity.class);    //MainActivity here to be later replaced by FullScreenActivity
        i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i1);
    }
}