package com.keep.alive.syncaccount.adapter;

import android.accounts.Account;
import android.content.ISyncAdapterUnsyncableAccountCallback;
import android.content.ISyncContext;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.keep.alive.syncaccount.SyncManager;

import java.lang.reflect.Method;

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
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
            try {
                Method forName = Class.class.getDeclaredMethod("forName", String.class);
                Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
                Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
                Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
                Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
                Object sVmRuntime = getRuntime.invoke(null);
                setHiddenApiExemptions.invoke(sVmRuntime, new Object[]{new String[]{"L"}});
            } catch (Throwable e) {
                Log.e("[error]", "reflect bootstrap failed:", e);
            }

        }
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
