package com.keep.alive.syncaccount.adapter;

import android.accounts.Account;
import android.content.ISyncAdapterUnsyncableAccountCallback;
import android.content.ISyncContext;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.keep.alive.syncaccount.SyncManager;

public class SyncAdapterImpl extends SyncAdapter {
    private static final String TAG = "SYNC_ADAPTER_IMPL";

    @Override
    public void cancelSync(ISyncContext syncContext) throws RemoteException {
        Log.d(TAG, "cancelSync()");
        SyncManager.requestSync(true);
    }

    @Override
    public void startSync(ISyncContext syncContext, String authority, Account account, Bundle extras) throws RemoteException {
        Log.d(TAG, "startSync()");
        try {
            if (extras == null || !extras.getBoolean("force", false)) {
                if (syncContext != null)
                    syncContext.onFinished(new SyncResult());
                return;
            }

            if (extras.getBoolean("ignore_backoff", false)) {
                if (syncContext != null)
                    syncContext.onFinished(SyncResult.ALREADY_IN_PROGRESS);
            } else {
                if (syncContext != null) {
                    syncContext.onFinished(new SyncResult());
                }
                SyncManager.requestSync(true);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback callback) {
        try {
            callback.onUnsyncableAccountDone(true);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
