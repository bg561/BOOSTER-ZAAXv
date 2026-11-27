
package com.zaax.booster;

import android.app.Activity;

import rikka.shizuku.Shizuku;

public class ShizukuHelper {

    private static final int REQUEST_CODE = 1001;

    public static boolean isAvailable() {
        return Shizuku.pingBinder();
    }

    public static boolean hasPermission() {
        if (!isAvailable()) {
            return false;
        }

        return Shizuku.checkSelfPermission()
                == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity) {
        if (!isAvailable()) {
            return;
        }

        if (!hasPermission()) {
            Shizuku.requestPermission(REQUEST_CODE);
        }
    }
}
