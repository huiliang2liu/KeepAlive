package com.keep.alive.daemon.core;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.util.Log;

import com.keep.alive.daemon.utils.DaemonNative;
import com.keep.alive.daemon.utils.DaemonParcel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import kotlin.TypeCastException;

import org.jetbrains.annotations.NotNull;

public final class DaemonMain {
    private int codeStartService;
    private int codeBroadcastIntent;
    private int codeStartInstrument;
    private IBinder binder;
    private Parcel parcelStartInstrument;
    private Parcel parcelStartService;
    private Parcel parcelBroadcastIntent;
    private final DaemonParcel daemonData;
    private static final String TAG = "DAEMON_THREAD";
    private static final String DESCRIPTOR = "android.app.IActivityManager";

    public final void startGuard() {
        try {
            String[] paths = this.daemonData.getPaths();
            if (paths == null || paths.length <= 0)
                return;
            prepareData();
            int i = 1;
            for (int len = paths.length; i < len; ++i) {
                new DaemonMain.DeputyThread(paths[i]).start();
            }

            if (DaemonNative.nativeWaitFile(paths[0])) {
                startExportProcess();
                Process.killProcess(Process.myPid());
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @SuppressLint({"PrivateApi", "Recycle", "DiscouragedPrivateApi"})
    private final void prepareData() throws Exception {
        Class smCls = Class.forName("android.os.ServiceManager");
        Method getServiceMethod = smCls.getMethod("getService", String.class);
        binder = (IBinder) getServiceMethod.invoke((Object) null, "activity");
        if (binder == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.os.IBinder");
        } else {
            this.codeStartService = this.getFieldValue("TRANSACTION_startService", "START_SERVICE_TRANSACTION");
            this.codeBroadcastIntent = this.getFieldValue("TRANSACTION_broadcastIntent", "BROADCAST_INTENT_TRANSACTION");
            this.codeStartInstrument = this.getFieldValue("TRANSACTION_startInstrumentation", "START_INSTRUMENTATION_TRANSACTION");
            Parcel parcel1 = Parcel.obtain();
            parcel1.writeInterfaceToken(DESCRIPTOR);
            parcel1.writeStrongBinder((IBinder) null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                parcel1.writeInt(1);
            }

            Intent intent = this.daemonData.getExportServiceIntent();
            if (intent == null) {
                throw new NullPointerException("intent is null");
            }

            intent.writeToParcel(parcel1, 0);
            parcel1.writeString((String) null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                parcel1.writeInt(0);
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = this.daemonData.getExportServiceIntent();
                if (intent == null) {
                    throw new NullPointerException("intent is null");
                }

                ComponentName component = intent.getComponent();
                parcel1.writeString(component != null ? component.getPackageName() : null);
            }

            parcel1.writeInt(0);
            this.parcelStartService = parcel1;
            Parcel parcel2 = Parcel.obtain();
            parcel2.writeInterfaceToken(DESCRIPTOR);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                parcel2.writeInt(1);
            }

            intent = this.daemonData.getExportInstrumentationIntent();
            if (intent == null) {
                throw new NullPointerException("intent is null");
            }

            ComponentName component = intent.getComponent();
            if (component == null) {
                throw new NullPointerException("component is null");
            }

            component.writeToParcel(parcel2, 0);
            parcel2.writeString((String) null);
            parcel2.writeInt(0);
            parcel2.writeInt(0);
            parcel2.writeStrongBinder((IBinder) null);
            parcel2.writeStrongBinder((IBinder) null);
            parcel2.writeInt(0);
            parcel2.writeString((String) null);
            this.parcelStartInstrument = parcel2;
            intent = this.daemonData.getExportBroadcastIntent();
            if (intent == null) {
                throw new NullPointerException("intent is null");
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
            Parcel parcel3 = Parcel.obtain();
            parcel3.writeInterfaceToken(DESCRIPTOR);
            parcel3.writeStrongBinder((IBinder) null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                parcel3.writeInt(1);
            }

            intent = this.daemonData.getExportBroadcastIntent();
            if (intent == null) {
                throw new NullPointerException("intent is null");
            }

            intent.writeToParcel(parcel3, 0);
            parcel3.writeString((String) null);
            parcel3.writeStrongBinder((IBinder) null);
            parcel3.writeInt(-1);
            parcel3.writeString((String) null);
            parcel3.writeInt(0);
            parcel3.writeStringArray((String[]) null);
            parcel3.writeInt(-1);
            parcel3.writeInt(0);
            parcel3.writeInt(0);
            parcel3.writeInt(0);
            parcel3.writeInt(0);
            this.parcelBroadcastIntent = parcel3;
            Class pCls = Process.class;
            Method method = pCls.getDeclaredMethod("setArgV0", String.class);
            method.invoke((Object) null, this.daemonData.getProcessName());
        }
    }

    @SuppressLint({"PrivateApi"})
    private final int getFieldValue(String field1, String field2) {
        try {
            Class cls = Class.forName("android.app.IActivityManager$Stub");
            Field declaredField = cls.getDeclaredField(field1);
            if (declaredField == null)
                throw new NullPointerException("declaredField is null");
            declaredField.setAccessible(true);
            return declaredField.getInt(cls);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            try {
                Class cls2 = Class.forName("android.app.IActivityManager");
                Field declaredField2 = cls2.getDeclaredField(field2);
                if (declaredField2 == null)
                    throw new NullPointerException("declaredField2 is null");
                declaredField2.setAccessible(true);
                return declaredField2.getInt(cls2);
            } catch (Throwable throwable1) {
                throwable1.printStackTrace();
                return -1;
            }
        }
    }

    private final void startExportProcess() {
        this.performStartInstrument();
        this.performStartService();
        this.performBroadcastIntent();
    }

    private final void performStartInstrument() {
        Log.d("DAEMON_THREAD", "startInstrument()");
        Parcel parcel = this.parcelStartInstrument;
        if (parcel == null)
            return;
        IBinder iBinder = this.binder;
        if (iBinder == null)
            return;
        try {
            boolean ret = iBinder.transact(this.codeStartInstrument, parcel, (Parcel) null, Binder.FLAG_ONEWAY);
            Log.d("DAEMON_THREAD", "startInstrumentation(), ret = " + ret);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private final void performStartService() {
        Log.d("DAEMON_THREAD", "startService()");
        Parcel parcel = this.parcelStartService;
        if (parcel == null)
            return;
        IBinder iBinder = this.binder;
        if (iBinder == null)
            return;
        try {
            boolean ret = iBinder.transact(this.codeStartService, parcel, (Parcel) null, Binder.FLAG_ONEWAY);
            Log.d("DAEMON_THREAD", "startService(), ret = " + ret);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private final void performBroadcastIntent() {
        Log.d("DAEMON_THREAD", "broadcastIntent()");
        Parcel parcel = this.parcelBroadcastIntent;
        if (parcel == null)
            return;
        IBinder iBinder = this.binder;
        if (iBinder == null)
            return;
        try {
            iBinder.transact(this.codeBroadcastIntent, parcel, (Parcel) null, 1);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public DaemonMain(@NotNull DaemonParcel daemonData) {
        super();
        if (daemonData == null)
            throw new NullPointerException("daemonData is null");
        this.daemonData = daemonData;
    }

    private final class DeputyThread extends Thread {
        private final String path;

        public void run() {
            super.run();
            this.setPriority(MAX_PRIORITY);
            if (DaemonNative.nativeWaitFile(this.path)) {
                DaemonMain.this.startExportProcess();
                Process.killProcess(Process.myPid());
            }
        }

        public DeputyThread(@NotNull String path) {
            super();
            if (path == null)
                throw new NullPointerException("path is null");
            this.path = path;
        }
    }

}
