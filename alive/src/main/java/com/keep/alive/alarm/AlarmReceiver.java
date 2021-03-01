package com.keep.alive.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.keep.alive.KeepAliveContentProvider;
import com.keep.alive.OhDaemon;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "ALARM_RECEIVER";
    private static final String ALARM_ACTION = "Permanent.AlarmAction";

    private static void register() {
        try {
            Context context = KeepAliveContentProvider.context();
            Intent intent = new Intent(ALARM_ACTION);
            intent.setPackage(context.getPackageName());
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1001, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                long trigger = SystemClock.elapsedRealtime() + OhDaemon.param.getAlarmInterval();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigger, pendingIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;
        if (!ALARM_ACTION.equals(intent.getAction()))
            return;
        register();
    }
}
