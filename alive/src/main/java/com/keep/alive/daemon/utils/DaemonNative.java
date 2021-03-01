package com.keep.alive.daemon.utils;

import android.content.Context;



import org.jetbrains.annotations.NotNull;

public final class DaemonNative {
    private static final DaemonNative NATIVE = new DaemonNative();

    public static boolean nativeHoldFile(@NotNull String path) {
        try {
            return NATIVE.nativeFun1(path) == 1;
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static boolean nativeWaitFile(@NotNull String path) {

        try {
            return NATIVE.nativeFun2(path) == 1;
        } catch (Throwable throwable) {
            return false;
        }
    }

    @NotNull
    public static String nativeExport1(@NotNull String path) {
        try {
            return NATIVE.nativeFun3(path);
        } catch (Throwable throwable) {
            return "";
        }
    }

    @NotNull
    public static String nativeExport2(@NotNull String path) {
        try {
            return NATIVE.nativeFun4(path);
        } catch (Throwable throwable) {
            return "";
        }
    }

    @NotNull
    public static String nativeExport3(@NotNull String path) {
        try {
            return NATIVE.nativeFun5(path);
        } catch (Throwable throwable) {
            return "";
        }
    }

    @NotNull
    public static String nativeFork(@NotNull String data, @NotNull String name) {
        try {
            return NATIVE.nativeFun6(data, name);
        } catch (Throwable throwable) {
            return "";
        }
    }

    private native int nativeFun1(String var1);

    private native int nativeFun2(String var1);

    private native String nativeFun3(String var1);

    private native String nativeFun4(String var1);

    private native String nativeFun5(String var1);

    private native String nativeFun6(String var1, String var2);

//    private native void nativeFun7(Context var1);


    static {
        try {
            System.loadLibrary("ohmoon");
        } catch (Throwable throwable) {
        }

    }
}
