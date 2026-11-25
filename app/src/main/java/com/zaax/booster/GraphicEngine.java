
package com.zaax.booster;

import android.os.Build;

public class GraphicEngine {

    private static boolean enabled = false;

    // Minimum Android 11
    public static boolean isAndroidSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    // Check device and Shizuku availability
    public static boolean isReady() {
        return isAndroidSupported()
                && ShizukuManager.isAvailable()
                && ShizukuManager.hasPermission();
    }

    // Graphic engine status
    public static String getStatus() {

        if (!isAndroidSupported()) {
            return "ANDROID VERSION NOT SUPPORTED";
        }

        if (!ShizukuManager.isAvailable()) {
            return "WIRELESS ADB NOT CONNECTED";
        }

        if (!ShizukuManager.hasPermission()) {
            return "SHIZUKU PERMISSION REQUIRED";
        }

        return enabled
                ? "GRAPHIC MC READY"
                : "GRAPHIC MC STANDBY";
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean setEnabled(boolean value) {

        if (!isReady()) {
            enabled = false;
            return false;
        }

        enabled = value;
        return true;
    }

    public static void reset() {
        enabled = false;
    }
}
