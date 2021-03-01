package com.keep.alive.base;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Build.VERSION;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat.Builder;

import com.keep.alive.KeepAliveContentProvider;
import com.keep.alive.R;
import com.keep.alive.OhDaemon;
import com.keep.alive.IDaemonService.Stub;
import com.keep.alive.OhDaemon.NotificationCreator;
import com.keep.alive.grayservice.GrayService;
import com.keep.alive.jobscheduler.KLJobManager;
import com.keep.alive.syncaccount.SyncManager;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DaemonService extends Service {
    private final Handler channelHandler = new Handler(Looper.getMainLooper());
    private boolean isForeground;
    private final Stub service = (Stub) (new Stub() {
        public void keepAlive() {
            DaemonService.this.keepAlive();
        }

        public void refreshNotification() {
            DaemonService.this.switchNotification();
        }
    });
    private static final String TAG = "PERMANENT_SERVICE";
    public static final int PERMANENT_NOTIFICATION_ID = 91234;
    private static final String CHANNEL_NAME = "permanent_channel";

    public void onCreate() {
        super.onCreate();
        Log.d("PERMANENT_SERVICE", "onCreate()");
    }

    public int onStartCommand(@NotNull Intent intent, int flags, int startId) {
        Log.d("PERMANENT_SERVICE", "onStartCommand()");
        this.switchNotification();
        return START_STICKY;
    }

    @Nullable
    public IBinder onBind(@NotNull Intent intent) {
        Log.d("PERMANENT_SERVICE", "onBind()");
        return this.service.asBinder();
    }

    private final void keepAlive() {
        Log.d("PERMANENT_SERVICE", "keepAlive()");
        if (!this.isForeground) {
            this.switchNotification();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (OhDaemon.param.isEnableJobSchedule()) {
                KLJobManager.enable(true);
            } else {
                KLJobManager.enable(false);
            }
        }

        if (OhDaemon.param.isEnableSyncAccount()) {
            SyncManager.addPeriodicSync();
        }

    }

    private final void switchNotification() {
        Log.d("PERMANENT_SERVICE", "updateNotification()");
        boolean isSuccess = false;

        try {
            isSuccess = this.startToggleForeground();
            if (isSuccess)
                return;
            isSuccess = this.startGrayForeground();
            if (isSuccess)
                return;
            if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                isSuccess = this.startOreaForeground();
                if (isSuccess) {
                    return;
                }
            }
            this.stopForeground(true);
            Log.d("PERMANENT_SERVICE", "stopForeground()");
        } finally {
            this.isForeground = isSuccess;
        }
    }

    private final boolean startToggleForeground() {
        Log.d("PERMANENT_SERVICE", "startToggleForeground()");
        NotificationCreator creator = OhDaemon.param.getNotificationCreator();
        if (creator == null)
            return false;
        Notification notification = creator.getNotification();
        if (notification == null)
            return false;
        this.startForeground(creator.getNotificationId(), notification);
        return true;
    }

    private final boolean startGrayForeground() {
        Log.d("PERMANENT_SERVICE", "startGrayForeground()");
        if (VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            this.startForeground(PERMANENT_NOTIFICATION_ID, createNotification());
            this.startService(new Intent(this.getApplicationContext(), GrayService.class));
            return false;
        }
        return false;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private final boolean startOreaForeground() {
        Log.d("PERMANENT_SERVICE", "startOreaForeground()");
        this.channelHandler.removeCallbacksAndMessages((Object) null);
        this.startForeground(PERMANENT_NOTIFICATION_ID, createNotification());
        this.channelHandler.postDelayed((Runnable) (new Runnable() {
            public final void run() {
                Log.d("PERMANENT_SERVICE", "startPermanentForeground(), deleteChannel");
                try {
                    NotificationManager notificationManager = (NotificationManager) DaemonService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.deleteNotificationChannel("permanent_channel");
                } catch (Throwable throwable) {
                }

            }
        }), 500L);
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("PERMANENT_SERVICE", "onDestroy()");
        DaemonUtils.keepAlive();
    }

    @NotNull
    public static Notification createNotification() {
        Log.d("PERMANENT_SERVICE", "createNotification()");
        Context context = KeepAliveContentProvider.context();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.O) {
                try {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_NAME, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(channel);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return new Builder(context, CHANNEL_NAME)
                    .setSmallIcon(R.mipmap.ic_app_water_mark)
                    .setContentTitle((CharSequence) "Permanent")
                    .setContentText((CharSequence) "Checking...")
                    .setShowWhen(false)
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .build();
        }
        return new Builder(context, CHANNEL_NAME)
                .setSmallIcon(R.mipmap.ic_app_water_mark)
                .setContentTitle("Permanent")
                .setContentText("Checking...")
                .setShowWhen(false)
                .build();

    }

}
