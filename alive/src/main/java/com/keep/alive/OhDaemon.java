package com.keep.alive;

import android.app.Notification;
import android.os.Build;

import com.keep.alive.base.DaemonUtils;
import com.keep.alive.daemon.core.DaemonAlive;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OhDaemon {
    private static final long DEFAULT_ALARM_INTERVAL = 15 * 60 * 1000L;
    private static final long DEFAULT_JOB_SCHEDULE_INTERVAL = 15 * 60 * 1000L;
    private static long DEFAULT_SYNC_ACCOUNT_INTERVAL = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? 900 : 3600) * 1000L;
    @NotNull
    public static OhDaemon.Param param = new OhDaemon.Param();


    public static void keepAlive(@NotNull OhDaemon.Param param) {
        if (param == null)
            throw new NullPointerException("param is null");
        OhDaemon.param = param;
        if (param.isEnableDaemon()) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    setPriority(MAX_PRIORITY);
                    DaemonAlive.keepAlive();
                }
            }.start();
        }

        DaemonUtils.keepAlive();
    }

    public interface NotificationCreator {
        @Nullable
        Notification getNotification();

        int getNotificationId();
    }

    public static final class Param {
        private boolean isEnableAlarm = false;
        private boolean isEnableSyncAccount = false;
        private boolean isEnableJobSchedule = false;
        private boolean isEnableDaemon = false;
        private long alarmInterval = DEFAULT_ALARM_INTERVAL;
        private long jobScheduleInterval = DEFAULT_JOB_SCHEDULE_INTERVAL;
        private long syncAccountInterval = DEFAULT_SYNC_ACCOUNT_INTERVAL;
        @Nullable
        private OhDaemon.NotificationCreator notificationCreator;

        public final boolean isEnableAlarm() {
            return this.isEnableAlarm;
        }

        public final void setEnableAlarm(boolean var1) {
            this.isEnableAlarm = var1;
        }

        public final boolean isEnableSyncAccount() {
            return this.isEnableSyncAccount;
        }

        public final void setEnableSyncAccount(boolean var1) {
            this.isEnableSyncAccount = var1;
        }

        public final boolean isEnableJobSchedule() {
            return this.isEnableJobSchedule;
        }

        public final void setEnableJobSchedule(boolean var1) {
            this.isEnableJobSchedule = var1;
        }

        public final boolean isEnableDaemon() {
            return this.isEnableDaemon;
        }

        public final void setEnableDaemon(boolean var1) {
            this.isEnableDaemon = var1;
        }

        public final long getAlarmInterval() {
            return this.alarmInterval;
        }

        public final void setAlarmInterval(long var1) {
            this.alarmInterval = var1;
        }

        public final long getJobScheduleInterval() {
            return this.jobScheduleInterval;
        }

        public final void setJobScheduleInterval(long var1) {
            this.jobScheduleInterval = var1;
        }

        public final long getSyncAccountInterval() {
            return this.syncAccountInterval;
        }

        public final void setSyncAccountInterval(long var1) {
            this.syncAccountInterval = var1;
        }

        @Nullable
        public final OhDaemon.NotificationCreator getNotificationCreator() {
            return this.notificationCreator;
        }

        public final void setNotificationCreator(@Nullable OhDaemon.NotificationCreator var1) {
            this.notificationCreator = var1;
        }

        public Param() {
        }
    }

    public static final class Builder {
        private final OhDaemon.Param param = new OhDaemon.Param();

        @NotNull
        public final OhDaemon.Builder setAlarmEnable(boolean enable) {
            this.param.setEnableAlarm(enable);
            return this;
        }

        @NotNull
        public final OhDaemon.Builder setSyncAccountEnable(boolean enable) {
            this.param.setEnableSyncAccount(enable);
            return this;
        }

        @NotNull
        public final OhDaemon.Builder setJobScheduleEnable(boolean enable) {
            this.param.setEnableJobSchedule(enable);
            return this;
        }

        @NotNull
        public final OhDaemon.Builder setDaemonEnable(boolean enable) {
            this.param.setEnableDaemon(enable);
            return this;
        }

        @NotNull
        public final OhDaemon.Builder setAlarmInterval(long interval) {
            if (interval > DEFAULT_ALARM_INTERVAL) {
                this.param.setAlarmInterval(interval);
            }

            return this;
        }

        @NotNull
        public final OhDaemon.Builder setJobScheduleInterval(long interval) {
            if (interval > DEFAULT_JOB_SCHEDULE_INTERVAL) {
                this.param.setJobScheduleInterval(interval);
            }

            return this;
        }

        @NotNull
        public final OhDaemon.Builder setSyncAccountInterval(long interval) {
            if (interval > DEFAULT_SYNC_ACCOUNT_INTERVAL) {
                this.param.setSyncAccountInterval(interval);
            }

            return this;
        }

        @NotNull
        public final OhDaemon.Builder withNotificationCreator(@NotNull OhDaemon.NotificationCreator notificationCreator) {
            this.param.setNotificationCreator(notificationCreator);
            return this;
        }

        @NotNull
        public final OhDaemon.Param build() {
            return this.param;
        }
    }
}
