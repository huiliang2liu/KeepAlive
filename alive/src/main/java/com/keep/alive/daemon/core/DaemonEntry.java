package com.keep.alive.daemon.core;

import android.util.Log;

import com.keep.alive.daemon.utils.DaemonParcel;

import org.jetbrains.annotations.NotNull;

public class DaemonEntry {
    private static final String TAG = "DAEMON_ENTRY";

    public static final void main(@NotNull String[] argv) {
        Log.d(TAG, "main()");
        try {
            String arg = argv[0];
            if (arg == null || arg.isEmpty())
                return;
            DaemonParcel data = DaemonParcel.createInstance(arg);
            new DaemonMain(data).startGuard();
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }
}
