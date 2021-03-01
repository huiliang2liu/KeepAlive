package com.keep.alive.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DaemonReceiver extends BroadcastReceiver {
    private static final String TAG = "DAEMON_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceiver(), action = " + (intent == null ? null : intent.getAction()));
    }
}
