package com.keep.alive.daemon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;

import com.keep.alive.KeepAliveContentProvider;
import com.keep.alive.daemon.utils.DaemonUtils;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AssistStartReceiver extends BroadcastReceiver {
    private static final String ACTION_SERVICE_START = "com.oh.daemon.ACTION_SERVICE_START";
    private static final String EXTRA_KEY_PACKAGE_NAME = "package_name_key";
    private static final String EXTRA_KEY_SERVICE_NAME = "service_name_key";
    private static AssistStartReceiver receiver;

    public void onReceive(@Nullable Context context, @Nullable Intent intent) {
        if (context == null || intent == null)
            return;
        String packageName = intent.getStringExtra("package_name_key");
        String serviceName = intent.getStringExtra("service_name_key");
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(serviceName))
            return;
        DaemonUtils.bindService(packageName, serviceName);
    }


    public static synchronized void register() {
        if (receiver != null)
            return;
        receiver = new AssistStartReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_SERVICE_START);
        intentFilter.setPriority(1000);
        KeepAliveContentProvider.context().registerReceiver(receiver, intentFilter, DaemonUtils.getDaemonBroadcastPermission(), (Handler) null);
    }

    public static void requestBindAction(@NotNull String packageName, @NotNull String serviceName) {
        Context context = KeepAliveContentProvider.context();
        Intent intent = new Intent(ACTION_SERVICE_START);
        intent.putExtra(EXTRA_KEY_PACKAGE_NAME, packageName);
        intent.putExtra(EXTRA_KEY_SERVICE_NAME, serviceName);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent, DaemonUtils.getDaemonBroadcastPermission());
    }
}
