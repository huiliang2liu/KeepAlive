package com.keep.alive;//package com.keep.framework.app.base;

import android.app.Application;
import android.content.IntentFilter;

import java.lang.reflect.Method;

public class KeepAliveInit {
    private static boolean init = false;

    /**
     * 最好在application中主动调用
     */
    public static void init() {
        if (init)
            return;
        init = true;
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
        if (KeepAliveContentProvider.getCurrentProcessName().equals(KeepAliveContentProvider.context().getOpPackageName() + ":service")) {
            Application application = (Application) KeepAliveContentProvider.context().getApplicationContext();
            try {
                Class cls = application.getClass();
                Method method = cls.getDeclaredMethod("getBroadcastAction");
                method.setAccessible(true);
                String[] actions = (String[]) method.invoke(application);
                if (actions != null && actions.length > 0) {
                    IntentFilter intentFilter = new IntentFilter();
                    for (String action : actions) {
                        intentFilter.addAction(action);
                    }
                    application.registerReceiver(new ActionBroadcastReceiver(), intentFilter);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}