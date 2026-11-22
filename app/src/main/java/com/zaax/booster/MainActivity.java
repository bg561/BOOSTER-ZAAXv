
package com.zaax.booster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(9, 10, 15);
    private final int PANEL = Color.rgb(22, 24, 33);
    private final int RED = Color.rgb(255, 23, 68);
    private final int WHITE = Color.WHITE;
    private final int GRAY = Color.rgb(155, 160, 175);

    private LinearLayout main;

    private int dp(float value) {
        return (int) (value *
                getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(dp(20), dp(25), dp(20), dp(30));
        scroll.addView(main);

        header();
        statusCard();
        section("PERFORMANCE TOOLS");
        feature("⚡", "ANTI LAG", "Gaming maintenance");
        feature("🎮", "BOOST GAME", "Choose and launch installed games");
        feature("▦", "PIXEL GRAPHICS", "Low graphics preset");
        feature("◉", "BOOST PING", "Internet response time test");
        feature("📊", "GAMING MONITOR", "Floating live RAM monitor");

        section("DEVICE CONTROL");
        feature("👆", "FAST TOUCH", "Touch settings");
        feature("↻", "REFRESH RATE", "Display refresh information");
        feature("▤", "DEVICE INFO", "Complete device information");
        feature("◆", "CPU INFO", "Processor information");
        feature("▦", "RESOLUTION", "Screen resolution information");
        feature("💾", "STORAGE INFO", "Internal storage information");

        footer();
        setContentView(scroll);
    }

    private void header() {
        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.addView(text("♛", 48, RED, true));

        LinearLayout names = new LinearLayout(this);
        names.setOrientation(LinearLayout.VERTICAL);
        names.setPadding(dp(12), 0, 0, 0);
        names.addView(text("BOOSTER ZAAX", 22, WHITE, true));
        names.addView(text("PREMIUM GAMING TOOL", 11, RED, true));

        top.addView(names);
        main.addView(top);
        space(28);
        main.addView(text("LEVEL UP YOUR GAME", 25, WHITE, true));
        main.addView(text("Your ultimate gaming control center.", 13, GRAY, false));
        space(20);
    }

    private void statusCard() {
        LinearLayout card = panel();
        card.addView(text("ZAAX SYSTEM", 12, GRAY, true));
        spaceIn(card, 8);
        card.addView(text("● SYSTEM ONLINE", 20, RED, true));
        spaceIn(card, 8);
        card.addView(text("BOOSTER ZAAX • VERSION 2.0", 10, WHITE, true));
        main.addView(card);
        space(24);
    }

    private void section(String title) {
        space(10);
        TextView heading = text(title, 13, RED, true);
        heading.setPadding(dp(3), dp(10), 0, dp(8));
        main.addView(heading);
    }

    private void feature(String icon, String name, String description) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(15), dp(12), dp(15), dp(12));
        card.setBackground(box(PANEL, 18));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(82));
        params.setMargins(0, dp(5), 0, dp(5));

        TextView symbol = text(icon, 25, RED, true);
        symbol.setGravity(Gravity.CENTER);
        card.addView(symbol, new LinearLayout.LayoutParams(dp(42), -1));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setGravity(Gravity.CENTER_VERTICAL);
        details.setPadding(dp(12), 0, 0, 0);
        details.addView(text(name, 14, WHITE, true));
        details.addView(text(description, 10, GRAY, false));

        card.addView(details, new LinearLayout.LayoutParams(0, -1, 1));
        card.addView(text("›", 28, RED, true));
        card.setOnClickListener(v -> runFeature(name));

        main.addView(card, params);
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

    // UPDATED ZAAX ANTI LAG
    private void antiLag() {
        try {
            ActivityManager manager =
                    (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            if (manager == null) {
                show("ZAAX ANTI LAG",
                        "Memory service unavailable.");
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
                    "\n\n" +
                    "ZAAX has checked your device memory." +
                    "\n\n" +
                    "Android restricts third-party apps " +
                    "from force-stopping other applications." +
                    "\n\n" +
                    "Close unused apps through Android settings " +
                    "if you need to free up resources.";

            new AlertDialog.Builder(this)
                    .setTitle("ZAAX ANTI LAG")
                    .setMessage(report)
                    .setPositiveButton("APP SETTINGS",
                            (dialog, which) ->
                                    openSettings(
                                            Settings.ACTION_APPLICATION_SETTINGS))
                    .setNegativeButton("CLOSE", null)
                    .show();

        } catch (Exception e) {
            show("ZAAX ANTI LAG",
                    "Unable to check device memory.");
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

    private LinearLayout panel() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(box(PANEL, 20));
        return card;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);

        if (bold) t.setTypeface(null, Typeface.BOLD);

        return t;
    }

    private GradientDrawable box(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));
        shape.setStroke(dp(1), RED);
        return shape;
    }

    private void space(int height) {
        View v = new View(this);
        main.addView(v, new LinearLayout.LayoutParams(1, dp(height)));
    }

    private void spaceIn(LinearLayout layout, int height) {
        View v = new View(this);
        layout.addView(v, new LinearLayout.LayoutParams(1, dp(height)));
    }

    private void footer() {
        space(25);

        TextView end = text("♛ POWERED BY ZAAX ♛", 13, RED, true);
        end.setGravity(Gravity.CENTER);
        main.addView(end);

        space(10);

        TextView version = text("BOOSTER ZAAX • V2.0", 10, GRAY, false);
        version.setGravity(Gravity.CENTER);
        main.addView(version);
    }
}
