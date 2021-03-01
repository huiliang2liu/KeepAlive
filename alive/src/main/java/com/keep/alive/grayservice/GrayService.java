package com.keep.alive.grayservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.keep.alive.base.DaemonService;

public class GrayService extends Service {
    private static final String TAG = "GRAY_SERVICE";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            startForeground(DaemonService.PERMANENT_NOTIFICATION_ID, DaemonService.createNotification());
            stopForeground(true);
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }
}
