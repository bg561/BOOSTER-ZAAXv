
package com.zaax.booster;

public final class SurfaceColorController {

    private SurfaceColorController() {
    }

    public static boolean setSaturation(float value) {
        if (!ShizukuManager.isAvailable()
                || !ShizukuManager.hasPermission()) {
            return false;
        }

        if (value < 0.0f || value > 2.0f) {
            return false;
        }

        // Belum ada perintah perubahan saturasi.
        return false;
    }

    public static float getDefaultSaturation() {
        return 1.0f;
    }
}
