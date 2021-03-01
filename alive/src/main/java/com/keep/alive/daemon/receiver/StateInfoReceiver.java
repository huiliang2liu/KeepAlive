package com.keep.alive.daemon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.keep.alive.KeepAliveContentProvider;
import com.keep.alive.daemon.core.DaemonAlive;
import com.keep.alive.daemon.utils.DaemonUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


import org.jetbrains.annotations.Nullable;

public final class StateInfoReceiver extends BroadcastReceiver {
    private static final String ACTION_REPORT_START_INFO = "com.oh.daemon.ACTION_REPORT_STATE_INFO";
    private static final String EXTRA_KEY_INFO = "report_stat_info_key";
    private static StateInfoReceiver infoReceiver;

    public void onReceive(@Nullable Context context, @Nullable Intent intent) {
        HashMap map = new HashMap();
        Serializable obj = intent.getSerializableExtra(EXTRA_KEY_INFO);
        if (obj != null && obj instanceof Map) {
            Map map1 = (Map) obj;
            for (Object key : map1.keySet()) {
                Object value = map1.get(key);
                if (value != null && value instanceof String) {
                    map.put(key, value);
                }
            }
        }
        DaemonAlive.reportStateInfo(map);
    }


    public static synchronized void register() {
        if (infoReceiver != null)
            return;
        infoReceiver = new StateInfoReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_REPORT_START_INFO);
        intentFilter.setPriority(1000);
        KeepAliveContentProvider.context().registerReceiver(infoReceiver, intentFilter, DaemonUtils.getDaemonBroadcastPermission(), (Handler) null);
    }

    public static void reportStateAction(@Nullable HashMap hashMap) {
        Context context = KeepAliveContentProvider.context();
        Intent intent = new Intent(ACTION_REPORT_START_INFO);
        intent.putExtra(EXTRA_KEY_INFO, (Serializable) hashMap);
        intent.setPackage(context.getPackageName());
        context.sendOrderedBroadcast(intent, DaemonUtils.getDaemonBroadcastPermission());
    }


}
