package com.keep.alive.daemon.process;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.keep.alive.daemon.receiver.AssistStartReceiver;
import com.keep.alive.daemon.receiver.ServiceStartReceiver;
import com.keep.alive.daemon.receiver.ServiceStartReceiver.ServiceStartListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DaemonService extends Service {
    private final ServiceBinder fakeServiceBinder = new ServiceBinder();

    public void onCreate() {
        super.onCreate();
        AssistStartReceiver.requestBindAction(getPackageName(), DaemonService.class.getName());
        ServiceStartReceiver.register(new ServiceStartListener() {
            public void onServiceStart(@NotNull Context context) {
                AssistStartReceiver.requestBindAction(context.getPackageName(), DaemonService.class.getName());
            }
        });
        ServiceStartReceiver.notifyServiceStart();
    }

    @Nullable
    public IBinder onBind(@Nullable Intent intent) {
        return this.fakeServiceBinder;
    }

    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }
}
