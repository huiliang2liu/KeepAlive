package com.keep.alive.daemon.utils;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import kotlin.jvm.JvmField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DaemonParcel implements Parcelable {
    @Nullable
    private String[] paths;
    @Nullable
    private String processName;
    @Nullable
    private Intent exportServiceIntent;
    @Nullable
    private Intent exportBroadcastIntent;
    @Nullable
    private Intent exportInstrumentationIntent;
    @JvmField
    @NotNull
    public static final Parcelable.Creator<DaemonParcel> CREATOR = new Parcelable.Creator<DaemonParcel>() {
        @NotNull
        public DaemonParcel createFromParcel(@NotNull Parcel parcel) {
            return new DaemonParcel(parcel);
        }


        @NotNull
        public DaemonParcel[] newArray(int size) {
            return new DaemonParcel[size];
        }

    };

    @Nullable
    public final String[] getPaths() {
        return this.paths;
    }

    public final void setPaths(@Nullable String[] var1) {
        this.paths = var1;
    }

    @Nullable
    public final String getProcessName() {
        return this.processName;
    }

    public final void setProcessName(@Nullable String var1) {
        this.processName = var1;
    }

    @Nullable
    public final Intent getExportServiceIntent() {
        return this.exportServiceIntent;
    }

    public final void setExportServiceIntent(@Nullable Intent var1) {
        this.exportServiceIntent = var1;
    }

    @Nullable
    public final Intent getExportBroadcastIntent() {
        return this.exportBroadcastIntent;
    }

    public final void setExportBroadcastIntent(@Nullable Intent var1) {
        this.exportBroadcastIntent = var1;
    }

    @Nullable
    public final Intent getExportInstrumentationIntent() {
        return this.exportInstrumentationIntent;
    }

    public final void setExportInstrumentationIntent(@Nullable Intent var1) {
        this.exportInstrumentationIntent = var1;
    }

    public void writeToParcel(@NotNull Parcel parcel, int flags) {
        parcel.writeStringArray(this.paths);
        parcel.writeString(this.processName);
        parcel.writeParcelable((Parcelable) this.exportServiceIntent, flags);
        parcel.writeParcelable((Parcelable) this.exportBroadcastIntent, flags);
        parcel.writeParcelable((Parcelable) this.exportInstrumentationIntent, flags);
    }

    public int describeContents() {
        return 0;
    }

    @NotNull
    public String toString() {
        Parcel parcel = Parcel.obtain();
        this.writeToParcel(parcel, 0);
        String str = Base64.encodeToString(parcel.marshall(), Base64.NO_WRAP);
        parcel.recycle();
        return str;
    }

    public DaemonParcel() {
    }

    public DaemonParcel(@NotNull Parcel parcel) {
        this();
        this.paths = parcel.createStringArray();
        this.processName = parcel.readString();
        this.exportServiceIntent = (Intent) parcel.readParcelable(Intent.class.getClassLoader());
        this.exportBroadcastIntent = (Intent) parcel.readParcelable(Intent.class.getClassLoader());
        this.exportInstrumentationIntent = (Intent) parcel.readParcelable(Intent.class.getClassLoader());
    }


    @NotNull
    public static  DaemonParcel createInstance(@NotNull String path) {
        Parcel parcel = Parcel.obtain();
        byte[] bytes = Base64.decode(path, Base64.NO_WRAP);
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        DaemonParcel data = CREATOR.createFromParcel(parcel);
        parcel.recycle();
        return data;
    }

}
