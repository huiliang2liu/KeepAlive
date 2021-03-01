package com.keep.alive;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Method;

public class ActionBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null)
            return;
        try {
            Application application = (Application) context.getApplicationContext();
            Class cls = application.getClass();
            Method method = cls.getDeclaredMethod("onReceiveBroadcastAction", Intent.class);
            method.setAccessible(true);
            method.invoke(application, intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
