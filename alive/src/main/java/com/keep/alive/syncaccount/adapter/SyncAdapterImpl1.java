package com.keep.alive.syncaccount.adapter;

import android.accounts.Account;
import android.content.ISyncAdapterUnsyncableAccountCallback;
import android.content.ISyncContext;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

public class SyncAdapterImpl1 extends SyncAdapterImpl {
    private static final String TAG = "SYNC_ADAPTER_IMPL1";

    @Override
    public void cancelSync(ISyncContext syncContext) throws RemoteException {
//        super.cancelSync(syncContext);
        Log.d("SYNC_ADAPTER_IMPL1", "cancelSync()");
    }

    @Override
    public void startSync(ISyncContext syncContext, String authority, Account account, Bundle extras) throws RemoteException {
        Log.d("SYNC_ADAPTER_IMPL1", "startSync()");
        try {
            if (syncContext != null) {
                syncContext.onFinished(new SyncResult());
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback callback) {
        try {
            callback.onUnsyncableAccountDone(false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
