package com.keep.alive.daemon.core;

import android.util.Log;

import com.keep.alive.KeepAliveContentProvider;
import com.keep.alive.daemon.process.AssistService;
import com.keep.alive.daemon.process.DaemonService;
import com.keep.alive.daemon.process.PartnerService;
import com.keep.alive.daemon.receiver.AssistStartReceiver;
import com.keep.alive.daemon.receiver.ServiceStartReceiver;
import com.keep.alive.daemon.receiver.StateInfoReceiver;
import com.keep.alive.daemon.utils.DaemonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DaemonAlive {
    private static final String TAG = "DAEMON_ALIVE";
    @NotNull
    public static final String START_TYPE = "type";
    @NotNull
    public static final String START_TYPE_FILE_LOCK = "file_lock_start";
    private static final String ID_DAEMON_SERVICE_ASSIST = "daemon_service_assist";
    private static final String ID_DAEMON_SERVICE_PARTNER = "daemon_service_partner";
    private static final String ID_DAEMON_NATIVE_ASSIST = "daemon_native_assist";
    private static final String ID_DAEMON_NATIVE_PARTNER = "daemon_native_partner";
    private static final String ID_ASSIST_SERVICE_DAEMON = "assist_service_daemon";
    private static final String ID_ASSIST_SERVICE_PARTNER = "assist_service_partner";
    private static final String ID_ASSIST_NATIVE_DAEMON = "assist_native_daemon";
    private static final String ID_ASSIST_NATIVE_PARTNER = "assist_native_partner";
    private static final String ID_PARTNER_SERVICE_DAEMON = "partner_service_daemon";
    private static final String ID_PARTNER_SERVICE_ASSIST = "partner_service_assist";
    private static final String ID_PARTNER_NATIVE_DAEMON = "partner_native_daemon";
    private static final String ID_PARTNER_NATIVE_ASSIST = "partner_native_assist";
    private static boolean hasInit=false;
    public static void keepAlive(){
        if (hasInit) {
            return;
        }

        hasInit = true;
        String processName = KeepAliveContentProvider.getCurrentProcessName();
        Log.d(TAG, "setup(), processName = "+processName);

        String packageName = KeepAliveContentProvider.context().getPackageName();
        if(processName.equals(packageName+":service")){
            initServiceProcess();
            return;
        }
        if(processName.equals(packageName+":daemon")){
            initDaemonProcess();
            return;
        }
        if(processName.equals(packageName+":assist")){
            initAssistProcess();
            return;
        }
        if(processName.equals(packageName+":partner")){
            initPartnerProcess();
            return;
        }
    }
    public static void reportStateInfo(Map<String,String> map){
        DaemonUtils.startBindService(DaemonService.class);
        DaemonUtils.startBindService(AssistService.class);
        DaemonUtils.startBindService(PartnerService.class);
    }
    private static void initServiceProcess(){
        AssistStartReceiver.register();
        StateInfoReceiver.register();
        ServiceStartReceiver.notifyServiceStart();
        DaemonUtils.startBindService(DaemonService.class);
        DaemonUtils.startBindService(AssistService.class);
        DaemonUtils.startBindService(PartnerService.class);
    }
    private static void initDaemonProcess(){
        DaemonOrder.asyncHoldFile(new String[]{ID_DAEMON_SERVICE_ASSIST, ID_DAEMON_SERVICE_PARTNER,
                ID_DAEMON_NATIVE_ASSIST, ID_DAEMON_NATIVE_PARTNER});
        DaemonOrder.asyncForkGuardProcess("daemon",
                new String[]{ID_ASSIST_NATIVE_DAEMON, ID_PARTNER_NATIVE_DAEMON});
        DaemonOrder.asyncGuardProcess(new String[]{ID_ASSIST_SERVICE_DAEMON,
                ID_PARTNER_SERVICE_DAEMON});
    }

    private static void initAssistProcess(){
        DaemonOrder.asyncHoldFile(new String[]{ID_ASSIST_SERVICE_DAEMON, ID_ASSIST_SERVICE_PARTNER,
                ID_ASSIST_NATIVE_DAEMON, ID_ASSIST_NATIVE_PARTNER});
        DaemonOrder.asyncForkGuardProcess("assist",
                new String[]{ID_DAEMON_NATIVE_ASSIST, ID_PARTNER_NATIVE_ASSIST});
        DaemonOrder.asyncGuardProcess(new String[]{ID_DAEMON_SERVICE_ASSIST,
                ID_PARTNER_SERVICE_ASSIST});
    }
    private static void initPartnerProcess(){
        DaemonOrder.asyncHoldFile(new String[]{ID_PARTNER_SERVICE_DAEMON, ID_PARTNER_SERVICE_ASSIST,
                ID_ASSIST_NATIVE_DAEMON, ID_PARTNER_NATIVE_ASSIST});
        DaemonOrder.asyncForkGuardProcess("partner",
                new String[]{ID_DAEMON_NATIVE_ASSIST, ID_ASSIST_NATIVE_PARTNER});
        DaemonOrder.asyncGuardProcess(new String[]{ID_DAEMON_SERVICE_ASSIST,
                ID_ASSIST_SERVICE_PARTNER});
    }
}
