
package com.zaax.booster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
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

import java.util.Locale;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(9, 10, 15);
    private final int PANEL = Color.rgb(22, 24, 33);
    private final int RED = Color.rgb(255, 23, 68);
    private final int WHITE = Color.WHITE;
    private final int GRAY = Color.rgb(155, 160, 175);

    private LinearLayout main;

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
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

        feature("⚡", "ANTI LAG",
                "Background app management");

        feature("🎮", "BOOST GAME",
                "Gaming preparation");

        feature("▣", "LOW GRAPHICS",
                "Graphics optimization guide");

        feature("◉", "BOOST PING",
                "Network connection information");

        feature("📊", "GAMING MONITOR",
                "Floating live RAM monitor");

        section("DEVICE CONTROL");

        feature("👆", "FAST TOUCH",
                "Touch settings");

        feature("↻", "REFRESH RATE",
                "Display refresh information");

        feature("▤", "DEVICE INFO",
                "Complete device information");

        feature("◆", "CPU INFO",
                "Processor information");

        feature("▦", "RESOLUTION",
                "Screen resolution information");

        feature("💾", "STORAGE INFO",
                "Internal storage information");

        footer();

        setContentView(scroll);
    }

    private void header() {
        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView logo = text("♛", 48, RED, true);
        top.addView(logo);

        LinearLayout names = new LinearLayout(this);
        names.setOrientation(LinearLayout.VERTICAL);
        names.setPadding(dp(12), 0, 0, 0);

        names.addView(text("BOOSTER ZAAX", 22, WHITE, true));
        names.addView(text("PREMIUM GAMING TOOL", 11, RED, true));

        top.addView(names);
        main.addView(top);

        space(28);

        main.addView(text("LEVEL UP YOUR GAME", 25, WHITE, true));
        main.addView(text(
                "Your ultimate gaming control center.",
                13, GRAY, false));

        space(20);
    }

    private void statusCard() {
        LinearLayout card = panel();

        card.addView(text("ZAAX SYSTEM", 12, GRAY, true));
        spaceIn(card, 8);
        card.addView(text("● SYSTEM ONLINE", 20, RED, true));
        spaceIn(card, 8);
        card.addView(text("BOOSTER ZAAX • VERSION 2.0",
                10, WHITE, true));

        main.addView(card);
        space(24);
    }

    private void section(String title) {
        space(10);

        TextView heading = text(title, 13, RED, true);
        heading.setPadding(dp(3), dp(10), 0, dp(8));

        main.addView(heading);
    }

    private void feature(String icon, String name,
                         String description) {

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

        card.addView(symbol,
                new LinearLayout.LayoutParams(dp(42), -1));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setGravity(Gravity.CENTER_VERTICAL);
        details.setPadding(dp(12), 0, 0, 0);

        details.addView(text(name, 14, WHITE, true));
        details.addView(text(description, 10, GRAY, false));

        card.addView(details,
                new LinearLayout.LayoutParams(0, -1, 1));

        card.addView(text("›", 28, RED, true));

        card.setOnClickListener(v -> runFeature(name));

        main.addView(card, params);
    }

    private void runFeature(String name) {

        switch (name) {

            case "ANTI LAG":
                dialog(name,
                        "Gaming maintenance tips:\n\n" +
                        "• Close unused applications.\n" +
                        "• Keep storage space available.\n" +
                        "• Avoid overheating.\n" +
                        "• Check background applications.",
                        "APP SETTINGS",
                        Settings.ACTION_APPLICATION_SETTINGS);
                break;

            case "BOOST GAME":
                dialog(name,
                        "Prepare your gaming session:\n\n" +
                        "• Close unused apps.\n" +
                        "• Use a stable connection.\n" +
                        "• Keep your phone cool.\n" +
                        "• Lower graphics when needed.",
                        "DISPLAY SETTINGS",
                        Settings.ACTION_DISPLAY_SETTINGS);
                break;

            case "LOW GRAPHICS":
                dialog(name,
                        "Open your game's graphics settings.\n\n" +
                        "Choose a lower graphics quality " +
                        "to reduce rendering workload.",
                        "APP SETTINGS",
                        Settings.ACTION_APPLICATION_SETTINGS);
                break;

            case "BOOST PING":
                networkInfo();
                break;

            case "GAMING MONITOR":
                startGamingMonitor();
                break;

            case "FAST TOUCH":
                dialog(name,
                        "Touch response options depend on " +
                        "your phone manufacturer.\n\n" +
                        "Check available touch settings.",
                        "OPEN SETTINGS",
                        Settings.ACTION_SETTINGS);
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

    private void startGamingMonitor() {

        if (Build.VERSION.SDK_INT >= 23 &&
                !Settings.canDrawOverlays(this)) {

            new AlertDialog.Builder(this)
                    .setTitle("ZAAX MONITOR PERMISSION")
                    .setMessage(
                            "Izinkan BOOSTER ZAAX menampilkan " +
                            "panel monitor di atas aplikasi lain."
                    )
                    .setPositiveButton("ALLOW", (d, w) -> {
                        Intent intent = new Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName())
                        );
                        startActivity(intent);
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();

            return;
        }

        Intent service = new Intent(
                this, MonitorService.class
        );

        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(service);
        } else {
            startService(service);
        }

        Toast.makeText(
                this,
                "ZAAX MONITOR STARTING",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void networkInfo() {

        String result = "NETWORK STATUS: ";

        try {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(
                            Context.CONNECTIVITY_SERVICE);

            Network network = cm.getActiveNetwork();

            NetworkCapabilities caps =
                    cm.getNetworkCapabilities(network);

            if (caps == null) {
                result += "DISCONNECTED";
            } else if (caps.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI)) {
                result += "WI-FI";
            } else if (caps.hasTransport(
                    NetworkCapabilities.TRANSPORT_CELLULAR)) {
                result += "MOBILE DATA";
            } else {
                result += "CONNECTED";
            }

            if (caps != null) {
                result += "\nINTERNET: " +
                        (caps.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        ? "AVAILABLE" : "NOT VERIFIED");
            }

        } catch (Exception e) {
            result += "UNAVAILABLE";
        }

        show("BOOST PING", result);
    }

    private void refreshInfo() {

        StringBuilder info = new StringBuilder();

        try {
            Display display = getWindowManager()
                    .getDefaultDisplay();

            info.append("CURRENT REFRESH RATE: ")
                    .append(String.format(Locale.US,
                            "%.1f Hz", display.getRefreshRate()));

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
                "\nTOTAL RAM: " +
                (memory.totalMem / 1048576) + " MB" +
                "\nAVAILABLE RAM: " +
                (memory.availMem / 1048576) + " MB" +
                "\nLOW MEMORY: " + memory.lowMemory;

        show("DEVICE INFORMATION", info);
    }

    private void cpuInfo() {

        String info =
                "PROCESSOR: " + Build.HARDWARE +
                "\nDEVICE: " + Build.DEVICE +
                "\nCPU CORES: " +
                Runtime.getRuntime().availableProcessors() +
                "\nARCHITECTURE: " +
                Build.SUPPORTED_ABIS[0] +
                "\n\nCPU CLOCK CONTROL IS RESTRICTED BY ANDROID.";

        show("CPU INFORMATION", info);
    }

    private void resolutionInfo() {

        int width = getResources()
                .getDisplayMetrics().widthPixels;

        int height = getResources()
                .getDisplayMetrics().heightPixels;

        float density = getResources()
                .getDisplayMetrics().density;

        String info =
                "SCREEN WIDTH: " + width + " px" +
                "\nSCREEN HEIGHT: " + height + " px" +
                "\nDENSITY: " +
                String.format(Locale.US, "%.2f", density) +
                "\n\nThese are the current app display metrics.";

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
            show("STORAGE INFORMATION",
                    "Storage information unavailable.");
        }
    }

    private String formatGB(long bytes) {
        return String.format(Locale.US,
                "%.2f GB", bytes / 1073741824.0);
    }

    private void dialog(String title, String message,
                        String button, String action) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(button,
                        (d, w) -> openSettings(action))
                .setNegativeButton("CLOSE", null)
                .show();
    }

    private void show(String title, String message) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void openSettings(String action) {

        try {
            startActivity(new Intent(action));
        } catch (Exception e) {
            Toast.makeText(this,
                    "Settings unavailable",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private LinearLayout panel() {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(box(PANEL, 20));

        return card;
    }

    private TextView text(String value, int size,
                          int color, boolean bold) {

        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);

        if (bold) {
            t.setTypeface(null, Typeface.BOLD);
        }

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

        main.addView(v,
                new LinearLayout.LayoutParams(1, dp(height)));
    }

    private void spaceIn(LinearLayout layout, int height) {

        View v = new View(this);

        layout.addView(v,
                new LinearLayout.LayoutParams(1, dp(height)));
    }

    private void footer() {

        space(25);

        TextView end = text(
                "♛ POWERED BY ZAAX ♛",
                13, RED, true);

        end.setGravity(Gravity.CENTER);
        main.addView(end);

        space(10);

        TextView version = text(
                "BOOSTER ZAAX • V2.0",
                10, GRAY, false);

        version.setGravity(Gravity.CENTER);
        main.addView(version);
    }
}
