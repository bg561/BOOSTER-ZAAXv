
package com.zaax.booster;

import android.content.pm.PackageManager;

import rikka.shizuku.Shizuku;

public class ShizukuManager {

    private static final int REQUEST_CODE = 1001;

    public static boolean isAvailable() {
        return Shizuku.pingBinder();
    }

    public static boolean hasPermission() {
        if (!isAvailable()) {
            return false;
        }

        return Shizuku.checkSelfPermission()
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission() {
        if (isAvailable() && !hasPermission()) {
            Shizuku.requestPermission(REQUEST_CODE);
        }
    }

    public static String getStatus() {
        if (!isAvailable()) {
            return "SHIZUKU NOT CONNECTED";
        }

        if (!hasPermission()) {
            return "PERMISSION NOT GRANTED";
        }

        return "SHIZUKU CONNECTED";
    }
}
