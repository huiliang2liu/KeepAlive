package com.keep.alive.syncaccount;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.PeriodicSync;
import android.os.Bundle;
import android.util.Log;

import com.keep.alive.KeepAliveContentProvider;
import com.keep.alive.R;
import com.keep.alive.OhDaemon;
import com.keep.alive.syncaccount.base.AccountProvider;

import java.util.List;

public class SyncManager {
    private static final String TAG = "SYNC_MANAGER";
    private static final Account account;
    private static final String authority = AccountProvider.getAuthority();

    static {
        Context context = KeepAliveContentProvider.context();
        String name = context.getString(R.string.app_name);
        String type = context.getString(R.string.account_authenticator_type);
        account = new Account(name, type);
    }

    public static void addPeriodicSync() {
        Log.d(TAG, "addPeriodicSync()");
        try {
            Context context = KeepAliveContentProvider.context();
            String type = context.getString(R.string.account_authenticator_type);
            AccountManager accountManager = AccountManager.get(context);
            Account[] accounts = accountManager.getAccountsByType(type);
            if (accounts == null || accounts.length <= 0) {
                accountManager.addAccountExplicitly(account, null, Bundle.EMPTY);
                ContentResolver.setIsSyncable(account, authority, 1);
                ContentResolver.setSyncAutomatically(account, authority, true);
                ContentResolver.setMasterSyncAutomatically(true);
            }
            if (!ContentResolver.isSyncPending(account, authority)) {
                requestSync(true);
            }
            List<PeriodicSync> periodicSyncs = ContentResolver.getPeriodicSyncs(account, authority);
            if (periodicSyncs == null || periodicSyncs.isEmpty()) {
                ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY,
                        OhDaemon.param.getSyncAccountInterval() / 1000);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void requestSync(boolean enabled) {
        Log.d(TAG, "requestSync()");
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean("force", true);
            if (enabled) {
                bundle.putBoolean("require_charging", true);
            }
            ContentResolver.requestSync(account, authority, bundle);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
