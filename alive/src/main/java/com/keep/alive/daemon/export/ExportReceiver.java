package com.keep.alive.daemon.export;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.keep.alive.daemon.core.DaemonAlive;
import com.keep.alive.daemon.receiver.StateInfoReceiver;

import java.util.HashMap;

public class ExportReceiver extends BroadcastReceiver {
    private static final String TAG = "EXPORT_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null)
            return;
        Log.d(TAG, "onReceive()");
        HashMap map = new HashMap();
        map.put(DaemonAlive.START_TYPE, DaemonAlive.START_TYPE_FILE_LOCK);
        StateInfoReceiver.reportStateAction(map);
    }
}
