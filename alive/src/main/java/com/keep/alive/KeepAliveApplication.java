package com.keep.alive;//package com.keep.framework.app.base;

import android.app.Application;

public class KeepAliveApplication extends Application {

    public static void init() {
        OhDaemon.keepAlive(new OhDaemon.Builder()
                .setAlarmEnable(true)
                .setJobScheduleEnable(true)
                .setSyncAccountEnable(true)
                .setDaemonEnable(true)
//                .withNotificationCreator(new OhDaemon.NotificationCreator() {
//            @Nullable
//            public Notification getNotification() {
//                return ToggleManager.INSTANCE.getNotification();
//            }
//
//            public int getNotificationId() {
//                return ToggleManager.INSTANCE.getNotificationId();
//            }
//        })
                .build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
}