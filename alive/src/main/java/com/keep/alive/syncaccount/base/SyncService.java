package com.keep.alive.syncaccount.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.keep.alive.syncaccount.adapter.SyncAdapter;
import com.keep.alive.syncaccount.adapter.SyncAdapterImpl;

public class SyncService extends Service {
    private SyncAdapter syncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        syncAdapter = new SyncAdapterImpl();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (syncAdapter == null)
            return null;
        return syncAdapter.getSyncAdapterBinder();
    }
}
