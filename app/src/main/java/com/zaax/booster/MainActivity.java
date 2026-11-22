
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
import android.os.Bundle;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Display;
import android.view.WindowManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {

    private final int bg = Color.rgb(9, 10, 15);
    private final int panel = Color.rgb(22, 24, 33);
    private final int red = Color.rgb(255, 35, 65);
    private final int white = Color.WHITE;
    private final int gray = Color.rgb(155, 160, 175);

    private LinearLayout main;

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(bg);

        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(dp(20), dp(24), dp(20), dp(30));

        scroll.addView(main);

        header();
        statusCard();

        section("PERFORMANCE TOOLS");
        feature("⚡", "ANTI LAG", "Manage background apps");
        feature("🎮", "BOOST GAME", "Prepare your gaming session");
        feature("▣", "LOW GRAPHICS", "Optimize game graphics");
        feature("◉", "BOOST PING", "Check live network status");

        section("DEVICE CONTROL");
        feature("👆", "FAST TOUCH", "Open touch settings");
        feature("↻", "REFRESH RATE", "Check supported display modes");
        feature("▤", "DEVICE INFO", "View actual device information");
        feature("◆", "CPU BOOST", "Check processor information");
        feature("▦", "RESOLUTION", "Check actual screen resolution");

        footer();
        setContentView(scroll);
    }

    private void header() {
        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView logo = text("♛", 38, red, true);
        top.addView(logo);

        LinearLayout names = new LinearLayout(this);
        names.setOrientation(LinearLayout.VERTICAL);
        names.setPadding(dp(12), 0, 0, 0);

        names.addView(text("BOOSTER ZAAX", 21, white, true));
        names.addView(text("GAMING PERFORMANCE", 10, gray, true));

        top.addView(names);
        main.addView(top);

        space(25);
        main.addView(text("LEVEL UP YOUR GAME", 23, white, true));
        main.addView(text("Your gaming control center.", 13, gray, false));
        space(20);
    }

    private void statusCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(box(red, 22));

        card.addView(text("ZAAX SYSTEM", 12, white, true));
        spaceIn(card, 8);
        card.addView(text("●  SYSTEM ONLINE", 19, white, true));
        spaceIn(card, 7);
        card.addView(text("VERSION 1.0 • ANDROID", 10, white, true));

        main.addView(card);
        space(25);
    }

    private void section(String name) {
        space(8);
        TextView heading = text(name, 12, red, true);
        heading.setPadding(2, dp(8), 2, dp(8));
        main.addView(heading);
    }

    private void feature(String icon, String name, String description) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(15), dp(15), dp(15), dp(15));
        card.setBackground(box(panel, 18));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(82));
        params.setMargins(0, dp(5), 0, dp(5));

        TextView symbol = text(icon, 25, red, true);
        symbol.setGravity(Gravity.CENTER);
        card.addView(symbol, new LinearLayout.LayoutParams(dp(42), -1));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, 0, 0);

        details.addView(text(name, 14, white, true));
        details.addView(text(description, 10, gray, false));

        card.addView(details, new LinearLayout.LayoutParams(0, -2, 1));

        card.addView(text("›", 28, red, true));

        card.setOnClickListener(v -> runFeature(name));
        main.addView(card, params);
    }

    private void runFeature(String name) {
        switch (name) {
            case "ANTI LAG":
                showDialog(name,
                        "Manage unused apps and review storage to reduce background distractions.",
                        "APP SETTINGS", Settings.ACTION_APPLICATION_SETTINGS);
                break;

            case "BOOST GAME":
                showDialog(name,
                        "Gaming preparation:\\n\\n• Close unused apps.\\n• Keep enough free storage.\\n• Use a stable network.\\n• Avoid overheating.",
                        "DISPLAY SETTINGS", Settings.ACTION_DISPLAY_SETTINGS);
                break;

            case "LOW GRAPHICS":
                showDialog(name,
                        "Open your game's graphics settings and select a lower graphics quality for smoother gameplay.",
                        "APP SETTINGS", Settings.ACTION_APPLICATION_SETTINGS);
                break;

            case "BOOST PING":
                networkInfo();
                break;

            case "FAST TOUCH":
                showDialog(name,
                        "Touch response settings depend on your Android device. Check the available options in system settings.",
                        "OPEN SETTINGS", Settings.ACTION_SETTINGS);
                break;

            case "REFRESH RATE":
                refreshInfo();
                break;

            case "DEVICE INFO":
                deviceInfo();
                break;

            case "CPU BOOST":
                cpuInfo();
                break;

            case "RESOLUTION":
                resolutionInfo();
                break;
        }
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
            } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                result += "CONNECTED VIA WI-FI";
            } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                result += "CONNECTED VIA MOBILE DATA";
            } else {
                result += "CONNECTED";
            }

            if (caps != null) {
                result += "\\nINTERNET: " +
                        (caps.hasCapability(
                                NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                                ? "AVAILABLE" : "NOT VERIFIED");
            }

        } catch (Exception e) {
            result += "UNAVAILABLE";
        }

        show(nameTitle("BOOST PING"), result);
    }

    private String nameTitle(String name) {
        return name;
    }

    private void refreshInfo() {
        float rate = 0;
        StringBuilder info = new StringBuilder();

        try {
            Display display = getWindowManager()
                    .getDefaultDisplay();

            rate = display.getRefreshRate();

            info.append("CURRENT REFRESH RATE: ")
                    .append(String.format(Locale.US, "%.1f Hz", rate));

            if (Build.VERSION.SDK_INT >= 23) {
                Display.Mode[] modes = display.getSupportedModes();

                info.append("\\n\\nSUPPORTED MODES:");

                for (Display.Mode mode : modes) {
                    info.append("\\n")
                            .append(mode.getPhysicalWidth())
                            .append(" x ")
                            .append(mode.getPhysicalHeight())
                            .append(" - ")
                            .append(mode.getRefreshRate())
                            .append(" Hz");
                }
            }
        } catch (Exception e) {
            info.append("Display information unavailable.");
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
                "\\nBRAND: " + Build.BRAND +
                "\\nANDROID: " + Build.VERSION.RELEASE +
                "\\nSDK: " + Build.VERSION.SDK_INT +
                "\\nTOTAL RAM: " + (memory.totalMem / 1048576) + " MB" +
                "\\nAVAILABLE RAM: " + (memory.availMem / 1048576) + " MB" +
                "\\nLOW MEMORY: " + memory.lowMemory;

        show("DEVICE INFORMATION", info);
    }

    private void cpuInfo() {
        String info =
                "PROCESSOR: " + Build.HARDWARE +
                "\\nDEVICE: " + Build.DEVICE +
                "\\nCPU CORES: " +
                Runtime.getRuntime().availableProcessors() +
                "\\nARCHITECTURE: " + Build.SUPPORTED_ABIS[0] +
                "\\n\\nCPU CLOCK CONTROL IS RESTRICTED BY ANDROID.";

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
                "\\nSCREEN HEIGHT: " + height + " px" +
                "\\nDENSITY: " + density +
                "\\n\\nResolution changes require supported system permissions.";

        show("RESOLUTION INFORMATION", info);
    }

    private void showDialog(String title, String message,
                            String button, String action) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(button, (d, w) -> openSettings(action))
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

    private void footer() {
        space(25);
        TextView end = text("♛ POWERED BY ZAAX ♛", 12, red, true);
        end.setGravity(Gravity.CENTER);
        main.addView(end);
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
        shape.setStroke(dp(1), Color.rgb(48, 35, 48));
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
}
