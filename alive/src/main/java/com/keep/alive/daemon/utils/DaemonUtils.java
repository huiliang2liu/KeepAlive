package com.keep.alive.daemon.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.IBinder;
import android.util.Log;


import com.keep.alive.KeepAliveContentProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import kotlin.text.StringsKt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DaemonUtils {
    private static final String TAG="DaemonUtils";
    @NotNull
    private static String daemonBroadcastPermission;

    @NotNull
    public static String getDaemonBroadcastPermission() {
        return daemonBroadcastPermission;
    }

    public static void startBindService(@NotNull Class serviceName) {
        Context context = KeepAliveContentProvider.context();
        try {
            context.startService(new Intent(context, serviceName));
        } catch (Throwable throwable) {
        }
        try {
            context.getApplicationContext().bindService(new Intent(context, serviceName), new ServiceConnection() {
                public void onServiceDisconnected(@Nullable ComponentName name) {
                }

                public void onServiceConnected(@Nullable ComponentName name, @Nullable IBinder service) {
                }
            }, Context.BIND_AUTO_CREATE);
        } catch (Throwable throwable) {
        }

    }

    public static void bindService(@NotNull String packageName, @NotNull String serviceName) {
        Context context = KeepAliveContentProvider.context();
        if (StringsKt.equals(context.getPackageName(), packageName, true)) {
            Intent intent = new Intent();
            intent.setClassName(packageName, serviceName);
            context.getApplicationContext().bindService(intent, new ServiceConnection() {
                public void onServiceDisconnected(@Nullable ComponentName name) {
                }

                public void onServiceConnected(@Nullable ComponentName name, @Nullable IBinder service) {
                }
            }, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        }
    }

    public static void exeShell(@NotNull File envFile, @NotNull String... argv) throws Exception {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null || pathEnv.isEmpty())
            return;
        String shPath = null;
        for (String dir : pathEnv.split(":")) {
            File shFile = new File(dir, "sh");
            if (shFile.exists()) {
                shPath = shFile.getPath();
                break;
            }
        }
        if (shPath == null || shPath.isEmpty())
            return;
        ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
        processBuilder.command(shPath)
                .redirectErrorStream(true)
                .directory(envFile)
                .environment()
                .putAll(System.getenv());
        Process process = processBuilder.start();
        OutputStream outputStream = process.getOutputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "utf-8");
        new BufferedReader((Reader) inputStreamReader);
        InputStreamReader errorStreamReader = new InputStreamReader(process.getErrorStream(), "utf-8");
        new BufferedReader((Reader) errorStreamReader);
        for (String arg : argv) {
            Log.e(TAG,arg);
            if (arg.endsWith("\n")) {
                outputStream.write(arg.getBytes("UTF-8"));
            } else {
                outputStream.write(String.format("%s\n", arg).getBytes("UTF-8"));
            }
            outputStream.flush();
        }
        outputStream.write("exit 156\n".getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
        process.waitFor();
    }

    static {
        daemonBroadcastPermission = "";
        Context context = KeepAliveContentProvider.context();
        PackageManager packageManager = context.getPackageManager();
        try {
            PermissionInfo[] permissions = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).permissions;
            for (PermissionInfo permission : permissions) {
                if (permission.name == null || permission.name.isEmpty())
                    continue;
                if (permission.name.endsWith(".DAEMON_BROADCAST_PERMISSIONS")) {
                    daemonBroadcastPermission = permission.name;
                    break;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
