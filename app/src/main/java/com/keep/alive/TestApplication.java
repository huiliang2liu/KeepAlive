package com.keep.alive;

import android.app.Application;
import android.content.Intent;
import android.util.Log;


import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import kotlin.jvm.internal.Intrinsics;

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KeepAliveApplication.init();
    }

    @NotNull
    public final String[] getBroadcastAction() {
        return new String[]{"android.intent.action.SCREEN_OFF", "android.intent.action.SCREEN_ON", "android.intent.action.USER_PRESENT"};
    }

    public final void onReceiveBroadcastAction(@NotNull Intent intent) {
        String action = intent.getAction();
        if (Intrinsics.areEqual("android.intent.action.SCREEN_OFF", action)) {
            Log.e("TestApplication", "黑屏");
        } else if (Intrinsics.areEqual("android.intent.action.SCREEN_ON", action)) {
            Log.e("TestApplication", "亮屏");
        } else if (Intrinsics.areEqual("android.intent.action.USER_PRESENT", action)) {
            Log.e("TestApplication", "解锁");
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(11);
            Log.e("TestApplication",""+hour);
            if (hour > 8 && hour < 10 || hour > 19 && hour < 21) {
                double[] doubles = LocationUtils.getInstance().getLocations(this);
                if (doubles == null){
                    Log.e("TestApplication"," return;");
                    return;
                }
                double dx = gps2m(doubles[0], doubles[1]);
                Log.e("TestApplication", "" + dx);
                if (dx > 300)
                    return;
                Intent intent1 = this.getPackageManager().getLaunchIntentForPackage("com.tencent.wework");
//            Intent intent1=new Intent(this,MainActivity.class);
                if (intent1 != null) {
                    Log.e("TestApplication", "dadadadadad");
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    this.startActivity(intent1);
                } else
                    Log.e("TestApplication", "intent1 is null");
//            Log.e("TestApplication", "解锁");
            }
        }
    }

    private final double EARTH_RADIUS = 6378137.0;
    private final double lat_b = 39.9704;
    private final double lng_b = 116.3226;

    private double gps2m(double lat_a, double lng_a) {

        double radLat1 = (lat_a * Math.PI / 180.0);

        double radLat2 = (lat_b * Math.PI / 180.0);

        double a = radLat1 - radLat2;

        double b = (lng_a - lng_b) * Math.PI / 180.0;

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)

                + Math.cos(radLat1) * Math.cos(radLat2)

                * Math.pow(Math.sin(b / 2), 2)));

        s = s * EARTH_RADIUS;

        s = Math.round(s * 10000) / 10000;
        return s;

    }

}
