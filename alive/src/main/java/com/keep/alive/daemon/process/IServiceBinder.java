package com.keep.alive.daemon.process;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import org.jetbrains.annotations.NotNull;

public interface IServiceBinder extends IInterface {

    abstract class Stub extends Binder implements IServiceBinder {
        private static final String DESCRIPTOR = IServiceBinder.class.getName();

        public final void init() {
            this.attachInterface((IInterface) this, DESCRIPTOR);
        }

        @NotNull
        public IBinder asBinder() {
            return (IBinder) this;
        }

        protected boolean onTransact(int code, @NotNull Parcel data, @NotNull Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                reply.writeNoException();
                reply.writeString(ServiceBinder.class.getSimpleName());
                return true;
            }
            if (code == IBinder.INTERFACE_TRANSACTION) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        public Stub() {
            this.init();
        }

    }
}
