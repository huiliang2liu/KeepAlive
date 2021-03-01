package com.keep.alive.syncaccount.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.keep.alive.syncaccount.adapter.SyncAdapter;
import com.keep.alive.syncaccount.adapter.SyncAdapterImpl1;

public class SyncService1 extends Service {
    private SyncAdapter syncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        syncAdapter = new SyncAdapterImpl1();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (syncAdapter == null)
            return null;
        return syncAdapter.getSyncAdapterBinder();
    }
}
