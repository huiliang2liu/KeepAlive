package com.keep.alive.daemon.core;

import com.keep.alive.KeepAliveContentProvider;
import com.keep.alive.instrumentation.KeepAliveInstrumentation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.keep.alive.daemon.export.ExportService;
import com.keep.alive.daemon.utils.DaemonNative;
import com.keep.alive.daemon.utils.DaemonParcel;
import com.keep.alive.daemon.utils.DaemonUtils;

import java.io.File;

import org.jetbrains.annotations.NotNull;

public final class DaemonOrder {
    private static final String TAG = "DAEMON_ORDER";
    private static final String FILE_DIR = "TmpDir";
    private static final String ACTION_EXPORT_BROADCAST = "com.oh.utility.broadcast.EXPORT_BROADCAST";
    private static final File temDir;
    private static  String nativeLibraryDir;
    private static  String publicSourceDir;
    private static final Intent exportServiceIntent;
    private static final Intent exportInstrumentIntent;
    private static final Intent exportBroadcastIntent;
    private static boolean hasForkedProcess;
    private static boolean hasGuardProcess;

    static {
        Context context = KeepAliveContentProvider.context();
        temDir = new File(context.getDir(FILE_DIR, Context.MODE_PRIVATE).getAbsolutePath());
        if (!temDir.exists()) {
            temDir.mkdirs();
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            nativeLibraryDir = packageInfo.applicationInfo.nativeLibraryDir;
            publicSourceDir = packageInfo.applicationInfo.publicSourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            nativeLibraryDir=null;
            publicSourceDir=null;
        }
        exportInstrumentIntent = new Intent();
        exportInstrumentIntent.setComponent(new ComponentName(context.getPackageName(), KeepAliveInstrumentation.class.getName()));
        exportInstrumentIntent.putExtras(new Bundle());
        exportServiceIntent = new Intent();
        exportServiceIntent.setClassName(context.getPackageName(), ExportService.class.getName());
        exportServiceIntent.putExtras(new Bundle());
        exportBroadcastIntent = new Intent(ACTION_EXPORT_BROADCAST);
        exportBroadcastIntent.setPackage(context.getPackageName());
        exportBroadcastIntent.putExtras(new Bundle());
    }

    public static void asyncHoldFile(@NotNull String[] paths) {
        if (paths == null)
            throw new NullPointerException("paths is null");
        try {
            for (String path : paths) {
                final File subFile = new File(temDir, path);
                if (!subFile.exists()) {
                    subFile.createNewFile();
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        DaemonNative.nativeHoldFile(subFile.getAbsolutePath());
                    }
                }.start();
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    public static void asyncForkGuardProcess(@NotNull String processName, @NotNull String[] paths) {
        if (processName == null || paths == null)
            throw new NullPointerException("processName or paths is null");
        if (hasForkedProcess)
            return;
        try {
            int len = paths.length;
            String pathArgs[] = new String[len];
            for (int i = 0; i < len; i++) {
                String path = paths[i];
                File subFile = new File(temDir, path);
                if (!subFile.exists()) {
                    subFile.createNewFile();
                }
                pathArgs[i] = subFile.getAbsolutePath();
            }
            hasForkedProcess = true;
            new DaemonOrder.ForkThread(pathArgs, processName).start();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void asyncGuardProcess(@NotNull String[] paths) {
        if (paths == null)
            throw new NullPointerException("paths is null");
        if (hasGuardProcess)
            return;
        try {
            int len = paths.length;
            String[] pathArgs =new String[len];
            for (int i = 0; i < len; i++) {
                String path = paths[i];
                File subFile = new File(temDir, path);
                if (!subFile.exists()) {
                    subFile.createNewFile();
                }
                pathArgs[i]=subFile.getAbsolutePath();
            }
            hasGuardProcess = true;
            new DaemonOrder.GuardThread(pathArgs).start();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    public static final class ForkThread extends Thread {
        private final String[] paths;
        private final String processName;
        public void run() {
            super.run();
            this.setPriority(MAX_PRIORITY);

            try {
                DaemonParcel data = new DaemonParcel();
                data.setPaths(paths);
                data.setProcessName(processName);
                data.setExportBroadcastIntent(exportBroadcastIntent);
                data.setExportInstrumentationIntent(exportInstrumentIntent);
                data.setExportServiceIntent(exportServiceIntent);
                DaemonUtils.exeShell(new File("/"),
                                new String[]{DaemonNative.nativeExport1(publicSourceDir),
                                DaemonNative.nativeExport2(nativeLibraryDir),
                                DaemonNative.nativeExport3(nativeLibraryDir),
                                DaemonNative.nativeFork(data.toString(),processName)});
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            hasForkedProcess=false;
        }

        public ForkThread(@NotNull String[] paths, @NotNull String processName) {
            super();
            this.paths = paths;
            this.processName = processName;
        }
    }

    public static final class GuardThread extends Thread {
        private final String[] paths;

        public void run() {
            super.run();
            this.setPriority(MAX_PRIORITY);

            try {
                DaemonParcel data = new DaemonParcel();
                data.setPaths(this.paths);
                data.setExportBroadcastIntent(exportBroadcastIntent);
                data.setExportInstrumentationIntent(exportInstrumentIntent);
                data.setExportServiceIntent(exportServiceIntent);
                data.setProcessName(KeepAliveContentProvider.getCurrentProcessName());
                DaemonEntry.main(new String[]{data.toString()});
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            hasGuardProcess=false;
        }

        public GuardThread(@NotNull String[] paths) {
            super();
            this.paths = paths;
        }
    }
}
