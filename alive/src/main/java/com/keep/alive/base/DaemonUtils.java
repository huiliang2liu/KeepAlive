package com.keep.alive.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

import com.keep.alive.IDaemonService;
import com.keep.alive.IDaemonService.Stub;
import com.keep.alive.KeepAliveContentProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class DaemonUtils {
    private static final String TAG = "PERMANENT_PROXY";
    private static IDaemonService permanentService;

    public static void keepAlive() {
        IDaemonService service = permanentService;
        if (service != null) {
            try {
                service.keepAlive();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            bindService(new DaemonUtils.BindCallBack() {
                public void onFinished() {
                    IDaemonService curService = permanentService;
                    if (curService != null) {
                        try {
                            curService.keepAlive();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }

    }

    public static void refreshNotification() {
        IDaemonService service = permanentService;
        if (service != null) {
            try {
                service.refreshNotification();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            bindService(new DaemonUtils.BindCallBack() {
                public void onFinished() {
                    IDaemonService curService = permanentService;
                    if (curService != null) {
                        try {
                            curService.refreshNotification();
                        } catch (RemoteException var3) {
                            var3.printStackTrace();
                        }
                    }

                }
            });
        }

    }

    private static void bindService(final DaemonUtils.BindCallBack callBack) {
        Context context = KeepAliveContentProvider.context();
        Intent intent = new Intent(context, DaemonService.class);
        try {
            boolean isSuccess = context.bindService(intent, new ServiceConnection() {
                public void onServiceConnected(@Nullable ComponentName name, @Nullable IBinder service) {
                    if (service != null) {
                        permanentService = Stub.asInterface(service);
                    }

                    callBack.onFinished();
                }

                public void onServiceDisconnected(@NotNull ComponentName name) {
                    permanentService = null;
                }
            }, Context.BIND_AUTO_CREATE);
            if (!isSuccess) {
                try {
                    context.startService(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private interface BindCallBack {
        void onFinished();
    }
}
