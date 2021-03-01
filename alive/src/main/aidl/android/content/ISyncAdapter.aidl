package android.content;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.content.ISyncContext;
import android.content.ISyncAdapterUnsyncableAccountCallback;

interface ISyncAdapter {
    IBinder getSyncAdapterBinder();

    void startSync(ISyncContext syncContext, String authority,
          in Account account, in Bundle extras);

    void cancelSync(ISyncContext syncContext);

    void onUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback callback);
}
