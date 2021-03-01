package com.keep.alive;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KeepAliveInit.init();
    }
}
