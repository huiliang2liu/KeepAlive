package com.keep.alive.syncaccount.adapter;

import android.content.ISyncAdapter;
import android.os.IBinder;

public abstract class SyncAdapter extends ISyncAdapter.Stub {
    @Override
    public IBinder getSyncAdapterBinder() {
        try {
            return asBinder();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
