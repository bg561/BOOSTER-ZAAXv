
package com.zaax.booster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import android.view.Display;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(9, 10, 15);

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        setContentView(R.layout.activity_main);

        connectButton(R.id.btn_boost_game, "BOOST GAME");
        connectButton(R.id.btn_anti_lag, "ANTI LAG");
        connectButton(R.id.btn_boost_ping, "BOOST PING");
        connectButton(R.id.btn_pixel_graphics, "PIXEL GRAPHICS");
        connectButton(R.id.btn_gaming_monitor, "GAMING MONITOR");
        connectButton(R.id.btn_fast_touch, "FAST TOUCH");
        connectButton(R.id.btn_refresh_rate, "REFRESH RATE");
        connectButton(R.id.btn_device_info, "DEVICE INFO");
        connectButton(R.id.btn_cpu_info, "CPU INFO");
        connectButton(R.id.btn_resolution, "RESOLUTION");
        connectButton(R.id.btn_storage_info, "STORAGE INFO");
    }

    private void connectButton(int id, String featureName) {
        Button button = findViewById(id);
        button.setOnClickListener(v -> runFeature(featureName));
    }

    private void runFeature(String name) {
        switch (name) {
            case "ANTI LAG":
                antiLag();
                break;
            case "BOOST GAME":
                chooseGame();
                break;
            case "PIXEL GRAPHICS":
                pixelGraphics();
                break;
            case "BOOST PING":
                testPing();
                break;
            case "GAMING MONITOR":
                startGamingMonitor();
                break;
            case "FAST TOUCH":
                openSettings(Settings.ACTION_SETTINGS);
                break;
            case "REFRESH RATE":
                refreshInfo();
                break;
            case "DEVICE INFO":
                deviceInfo();
                break;
            case "CPU INFO":
                cpuInfo();
                break;
            case "RESOLUTION":
                resolutionInfo();
                break;
            case "STORAGE INFO":
                storageInfo();
                break;
        }
    }

    private void pixelGraphics() {
        String[] presets = {
                "LOW GRAPHICS",
                "VERY LOW GRAPHICS",
                "PIXEL / BLOCKY LOOK"
        };

        new AlertDialog.Builder(this)
                .setTitle("ZAAX PIXEL GRAPHICS")
                .setItems(presets, (dialog, which) -> {
                    String title;
                    String message;

                    if (which == 0) {
                        title = "LOW GRAPHICS";
                        message = "Pilih kualitas grafis LOW atau SMOOTH di dalam game.\n\n" +
                                "Matikan efek visual tambahan jika tersedia.";
                    } else if (which == 1) {
                        title = "VERY LOW GRAPHICS";
                        message = "Gunakan kualitas grafis paling rendah yang tersedia.\n\n" +
                                "Nonaktifkan bayangan dan efek tambahan jika tersedia.";
                    } else {
                        title = "PIXEL / BLOCKY LOOK";
                        message = "Efek tekstur kotak-kotak membutuhkan dukungan dari game.\n\n" +
                                "BOOSTER ZAAX belum bisa mengubah tekstur internal game secara langsung.";
                    }

                    show(title, message);
                })
                .setNegativeButton("CLOSE", null)
                .show();
    }

    private void antiLag() {
        try {
            ActivityManager manager =
                    (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            if (manager == null) {
                show("ZAAX ANTI LAG", "Memory service unavailable.");
                return;
            }

            ActivityManager.MemoryInfo memory =
                    new ActivityManager.MemoryInfo();

            manager.getMemoryInfo(memory);

            long total = memory.totalMem / 1048576;
            long available = memory.availMem / 1048576;
            long used = Math.max(0, total - available);

            int percent = total > 0
                    ? (int) (available * 100 / total)
                    : 0;

            String status;

            if (memory.lowMemory || percent < 15) {
                status = "LOW MEMORY WARNING";
            } else if (percent < 30) {
                status = "MEMORY MODERATE";
            } else {
                status = "MEMORY AVAILABLE";
            }

            String report =
                    "♛ ZAAX ANTI LAG ♛\n\n" +
                    "TOTAL RAM: " + total + " MB\n" +
                    "USED RAM: " + used + " MB\n" +
                    "AVAILABLE RAM: " + available + " MB\n" +
                    "AVAILABLE: " + percent + "%\n\n" +
                    "SYSTEM STATUS: " + status +
                    "\n\nZAAX has checked your device memory." +
                    "\n\nAndroid restricts third-party apps " +
                    "from force-stopping other applications." +
                    "\n\nClose unused apps through Android settings " +
                    "if you need to free up resources.";

            new AlertDialog.Builder(this)
                    .setTitle("ZAAX ANTI LAG")
                    .setMessage(report)
                    .setPositiveButton("APP SETTINGS",
                            (dialog, which) ->
                                    openSettings(Settings.ACTION_APPLICATION_SETTINGS))
                    .setNegativeButton("CLOSE", null)
                    .show();

        } catch (Exception e) {
            show("ZAAX ANTI LAG", "Unable to check device memory.");
        }
    }

    private void chooseGame() {
        PackageManager pm = getPackageManager();

        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);

        List<android.content.pm.ResolveInfo> apps =
                pm.queryIntentActivities(query, 0);

        List<String> names = new ArrayList<>();
        List<Intent> launchIntents = new ArrayList<>();

        for (android.content.pm.ResolveInfo app : apps) {
            String packageName = app.activityInfo.packageName;

            if (packageName.equals(getPackageName())) continue;

            Intent launch = pm.getLaunchIntentForPackage(packageName);

            if (launch != null) {
                names.add(app.loadLabel(pm).toString());
                launchIntents.add(launch);
            }
        }

        if (names.isEmpty()) {
            show("BOOST GAME", "Tidak ditemukan aplikasi yang bisa dibuka.");
            return;
        }

        String[] items = names.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("PILIH GAME / APLIKASI")
                .setItems(items, (d, which) -> {
                    try {
                        startActivity(launchIntents.get(which));
                    } catch (Exception e) {
                        show("BOOST GAME", "Aplikasi tidak dapat dibuka.");
                    }
                })
                .setNegativeButton("CLOSE", null)
                .show();
    }

    private void testPing() {
        show("BOOST PING", "Sedang menguji respons internet...");

        new Thread(() -> {
            long start = System.currentTimeMillis();
            String result;
            HttpURLConnection connection = null;

            try {
                URL url = new URL("https://www.google.com/generate_204");
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");

                int response = connection.getResponseCode();
                long elapsed = System.currentTimeMillis() - start;

                result = "INTERNET RESPONSE: " + elapsed + " ms" +
                        "\nHTTP STATUS: " + response +
                        "\n\nIni adalah waktu respons ke server pengujian, " +
                        "bukan ping server Roblox.";

            } catch (Exception e) {
                result = "Tes koneksi gagal atau timeout.\nPeriksa koneksi internet.";
            } finally {
                if (connection != null) connection.disconnect();
            }

            final String output = result;
            runOnUiThread(() -> show("BOOST PING", output));

        }).start();
    }

    private void startGamingMonitor() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("ZAAX MONITOR PERMISSION")
                    .setMessage("Izinkan BOOSTER ZAAX menampilkan panel monitor di atas aplikasi lain.")
                    .setPositiveButton("ALLOW", (d, w) -> {
                        Intent intent = new Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();

            return;
        }

        Intent service = new Intent(this, MonitorService.class);

        try {
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(service);
            } else {
                startService(service);
            }

            Toast.makeText(this, "ZAAX MONITOR STARTING",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            show("MONITOR", "Tidak dapat memulai monitor.");
        }
    }

    private void refreshInfo() {
        StringBuilder info = new StringBuilder();

        try {
            Display display = getWindowManager().getDefaultDisplay();

            info.append("CURRENT REFRESH RATE: ")
                    .append(String.format(Locale.US, "%.1f Hz",
                            display.getRefreshRate()));

            if (Build.VERSION.SDK_INT >= 23) {
                Display.Mode[] modes = display.getSupportedModes();

                info.append("\n\nSUPPORTED MODES:");

                for (Display.Mode mode : modes) {
                    info.append("\n")
                            .append(mode.getPhysicalWidth())
                            .append(" x ")
                            .append(mode.getPhysicalHeight())
                            .append(" - ")
                            .append(mode.getRefreshRate())
                            .append(" Hz");
                }
            }

        } catch (Exception e) {
            info.append("Information unavailable.");
        }

        show("REFRESH RATE", info.toString());
    }

    private void deviceInfo() {
        ActivityManager manager =
                (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memory =
                new ActivityManager.MemoryInfo();

        manager.getMemoryInfo(memory);

        String info =
                "MODEL: " + Build.MODEL +
                "\nBRAND: " + Build.BRAND +
                "\nDEVICE: " + Build.DEVICE +
                "\nANDROID: " + Build.VERSION.RELEASE +
                "\nSDK: " + Build.VERSION.SDK_INT +
                "\nTOTAL RAM: " + memory.totalMem / 1048576 + " MB" +
                "\nAVAILABLE RAM: " + memory.availMem / 1048576 + " MB";

        show("DEVICE INFORMATION", info);
    }

    private void cpuInfo() {
        String info =
                "PROCESSOR: " + Build.HARDWARE +
                "\nDEVICE: " + Build.DEVICE +
                "\nCPU CORES: " +
                Runtime.getRuntime().availableProcessors() +
                "\nARCHITECTURE: " + Build.SUPPORTED_ABIS[0] +
                "\n\nCPU CLOCK CONTROL IS RESTRICTED BY ANDROID.";

        show("CPU INFORMATION", info);
    }

    private void resolutionInfo() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        float density = getResources().getDisplayMetrics().density;

        String info =
                "SCREEN WIDTH: " + width + " px" +
                "\nSCREEN HEIGHT: " + height + " px" +
                "\nDENSITY: " +
                String.format(Locale.US, "%.2f", density) +
                "\n\nThese are current app display metrics.";

        show("RESOLUTION INFORMATION", info);
    }

    private void storageInfo() {
        try {
            StatFs stat = new StatFs(
                    Environment.getDataDirectory().getPath());

            long total = stat.getTotalBytes();
            long free = stat.getAvailableBytes();
            long used = total - free;

            String info =
                    "TOTAL STORAGE: " + formatGB(total) +
                    "\nUSED STORAGE: " + formatGB(used) +
                    "\nAVAILABLE STORAGE: " + formatGB(free);

            show("STORAGE INFORMATION", info);

        } catch (Exception e) {
            show("STORAGE INFORMATION", "Storage information unavailable.");
        }
    }

    private String formatGB(long bytes) {
        return String.format(Locale.US, "%.2f GB",
                bytes / 1073741824.0);
    }

    private void openSettings(String action) {
        try {
            startActivity(new Intent(action));
        } catch (Exception e) {
            Toast.makeText(this, "Settings unavailable",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void show(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
