package com.keep.alive.daemon.export;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.keep.alive.daemon.core.DaemonAlive;
import com.keep.alive.daemon.receiver.StateInfoReceiver;
import java.util.HashMap;
import org.jetbrains.annotations.Nullable;


public final class ExportService extends Service {
    private static final String TAG = "EXPORT_SERVICE";

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        HashMap map = new HashMap();
        map.put(DaemonAlive.START_TYPE, DaemonAlive.START_TYPE_FILE_LOCK);
        StateInfoReceiver.reportStateAction(map);
    }

    @Nullable
    public IBinder onBind(@Nullable Intent intent) {
        return null;
    }

}
