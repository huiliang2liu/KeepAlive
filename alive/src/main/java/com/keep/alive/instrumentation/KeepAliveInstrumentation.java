package com.keep.alive.instrumentation;

import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;
import android.util.Log;

import com.keep.alive.OhDaemon;

public class KeepAliveInstrumentation extends Instrumentation {
    private static final String TAG = "EXPORT_INSTRUMENTATION";


    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        Log.d(TAG, "onCreate()");
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        super.callApplicationOnCreate(app);
        Log.d(TAG, "callApplicationOnCreate()");
    }
}
