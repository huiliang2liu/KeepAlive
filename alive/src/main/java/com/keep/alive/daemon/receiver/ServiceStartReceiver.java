package com.keep.alive.daemon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.keep.alive.KeepAliveContentProvider;
import com.keep.alive.daemon.utils.DaemonUtils;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class ServiceStartReceiver extends BroadcastReceiver {
    private ServiceStartReceiver.ServiceStartListener serviceStartListener;
    private static final String ACTION_MAIN_PROCESS_START = "com.oh.daemon.ACTION_MAIN_PROCESS_START";
    private static ServiceStartReceiver receiver;

    public void onReceive(@Nullable Context context, @Nullable Intent intent) {
        if (serviceStartListener != null) {
            serviceStartListener.onServiceStart(context);
        }
    }

    public interface ServiceStartListener {
        void onServiceStart(@NotNull Context var1);
    }


    public static void register(@NotNull ServiceStartReceiver.ServiceStartListener serviceStartListener) {
        if (receiver == null)
            return;
        receiver = new ServiceStartReceiver();
        receiver.serviceStartListener = serviceStartListener;
        IntentFilter intentFilter = new IntentFilter(ACTION_MAIN_PROCESS_START);
        intentFilter.setPriority(1000);
        KeepAliveContentProvider.context().registerReceiver(receiver, intentFilter, DaemonUtils.getDaemonBroadcastPermission(), (Handler) null);
    }

    public static void notifyServiceStart() {
        Intent intent = new Intent(ACTION_MAIN_PROCESS_START);
        Context context = KeepAliveContentProvider.context();
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent, DaemonUtils.getDaemonBroadcastPermission());
    }

}
