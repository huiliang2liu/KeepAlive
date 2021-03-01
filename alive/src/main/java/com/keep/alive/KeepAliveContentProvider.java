package com.keep.alive;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class KeepAliveContentProvider extends ContentProvider {
    private static Context context;
    private static String processName;

    @Override
    public boolean onCreate() {
        KeepAliveInit.init();
        Intent intent=new Intent(KeepAliveContentProvider.context(),KeepAliveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        KeepAliveContentProvider.context().startActivity(intent);
        return false;
    }


    public static Context context() {
        if (context == null) {
            context = field2Application();
            if (context == null)
                context = method2Application();
        }
        return context;
    }

    private static Application field2Application() {
        Application application = null;
        try {
            Class actvivtyThreadClass = Class
                    .forName("android.app.ActivityThread");
            Field mInitialApplicationField = actvivtyThreadClass.getDeclaredField("mInitialApplication");
            mInitialApplicationField.setAccessible(true);
            Method currentActivityThreadMethod = actvivtyThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object activityThread = currentActivityThreadMethod.invoke(null);
            application = (Application) mInitialApplicationField.get(activityThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return application;
    }


    private static Application method2Application() {
        Application application = null;
        try {
            Class activityThreadClass = Class
                    .forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object activityThread = currentActivityThreadMethod.invoke(null);
            Method getApplicationMethod = activityThreadClass.getDeclaredMethod("getApplication");
            getApplicationMethod.setAccessible(true);
            application = (Application) getApplicationMethod
                    .invoke(activityThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return application;
    }

    public static String getCurrentProcessName() {
        if (processName != null) {
            return processName;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            processName = Application.getProcessName();
            return processName;
        }

        FileInputStream in = null;
        try {
            String fn = "/proc/self/cmdline";
            in = new FileInputStream(fn);
            byte[] buffer = new byte[256];
            int len = 0;
            int b;
            while ((b = in.read()) > 0 && len < buffer.length) {
                buffer[len++] = (byte) b;
            }
            if (len > 0) {
                processName = new String(buffer, 0, len, "UTF-8");
                return processName;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        processName = "";
        return processName;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
