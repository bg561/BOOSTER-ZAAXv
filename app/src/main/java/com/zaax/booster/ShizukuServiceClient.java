
package com.zaax.booster;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;

import rikka.shizuku.Shizuku;

public final class ShizukuServiceClient {

    private static IUserService userService;
    private static boolean binding = false;
    private static Shizuku.UserServiceArgs serviceArgs;

    private ShizukuServiceClient() {
    }

    private static final ServiceConnection CONNECTION =
            new ServiceConnection() {

        @Override
        public void onServiceConnected(
                ComponentName name, IBinder binder) {

            userService = IUserService.Stub.asInterface(binder);
            binding = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            userService = null;
            binding = false;
        }
    };

    public static boolean bind(Context context) {

        if (!Shizuku.pingBinder()) return false;

        if (Shizuku.checkSelfPermission()
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        if (userService != null || binding) return true;

        serviceArgs = new Shizuku.UserServiceArgs(
                new ComponentName(
                        context.getApplicationContext(),
                        ZaaxUserService.class
                )
        )
                .processNameSuffix("zaax")
                .tag("zaax_graphics")
                .daemon(false)
                .debuggable(true)
                .version(1);

        try {
            binding = true;
            Shizuku.bindUserService(serviceArgs, CONNECTION);
            return true;
        } catch (RuntimeException e) {
            binding = false;
            return false;
        }
    }

    public static boolean isConnected() {
        return userService != null;
    }

    public static String getServiceInfo() {

        if (userService == null) {
            return "USER SERVICE NOT CONNECTED";
        }

        try {
            return userService.getServiceInfo();
        } catch (RemoteException e) {
            return "USER SERVICE ERROR";
        }
    }

    public static void unbind() {

        if (serviceArgs == null) return;

        try {
            Shizuku.unbindUserService(
                    serviceArgs, CONNECTION, true
            );
        } catch (RuntimeException ignored) {
        }

        userService = null;
        binding = false;
        serviceArgs = null;
    }
}
