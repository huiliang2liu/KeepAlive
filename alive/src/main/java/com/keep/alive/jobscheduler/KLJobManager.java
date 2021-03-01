package com.keep.alive.jobscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.keep.alive.KeepAliveContentProvider;
import com.keep.alive.OhDaemon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class KLJobManager {
    private static final String TAG = "OH_JOB_SCHEDULER";
    private static final int JOB_ID_1 = 901;
    private static final int JOB_ID_2 = 902;
    private static final int JOB_ID_3 = 903;
    private static final int JOB_ID_4 = 904;
    private static final List<String> jobServiceList = new ArrayList();
    private static final AtomicBoolean enabled = new AtomicBoolean(false);

    static {
        jobServiceList.add(KLJobService.class.getName());
    }

    public static void enable(boolean isEnable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        Context context = KeepAliveContentProvider.context();
        if (isEnable) {
            if (enabled.compareAndSet(false, true)) {
                String jobCls = jobServiceList.get(0);
                try {
                    setComponentEnable(context, true, jobCls);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                try {
                    schedule(context, jobCls);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        } else {
            if (enabled.compareAndSet(true, false)) {
                for (String cls : jobServiceList) {
                    setComponentEnable(context, false, cls);
                }
                try {
                    JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    if (jobScheduler == null)
                        return;
                    jobScheduler.cancel(JOB_ID_1);
                    jobScheduler.cancel(JOB_ID_2);
                    jobScheduler.cancel(JOB_ID_3);
                    jobScheduler.cancel(JOB_ID_4);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }

    private static void setComponentEnable(Context context, boolean isEnable, String jobCls) {
        Log.d(TAG, String.format("setComponentEnableSetting(), isEnable = %s, cls = %s", isEnable, jobCls));
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            ComponentName component = new ComponentName(context.getPackageName(), jobCls);
            int state = isEnable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            packageManager.setComponentEnabledSetting(component, state, PackageManager.DONT_KILL_APP);
        }
    }

    private static void schedule(Context context, String jobCls) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        schedule(context, JOB_ID_1, jobCls);
        schedule(context, JOB_ID_2, jobCls);
        schedule(context, JOB_ID_3, jobCls);
        schedule(context, JOB_ID_4, jobCls);
    }

    private static void schedule(Context context, int jobId, String jobCls) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        Log.d(TAG, String.format("schedule(), jobId = %s, cls = %s", jobId, jobCls));
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null)
            return;
        JobInfo.Builder builder = new JobInfo.Builder(jobId, new ComponentName(context.getPackageName(), jobCls));
        long time = OhDaemon.param.getJobScheduleInterval();
        if (jobId == JOB_ID_1) {
            builder.setPeriodic(time);
        } else {
            builder.setMinimumLatency(time);
            if (jobId == JOB_ID_2) {
                builder.setRequiresCharging(true);
            } else if (jobId == JOB_ID_3) {
                builder.setRequiresDeviceIdle(true);
            } else {
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
            }
        }

        builder.setPersisted(true);
        int ret = jobScheduler.schedule(builder.build());
        Log.d(TAG, "schedule(), ret = " + ret);
    }
}
