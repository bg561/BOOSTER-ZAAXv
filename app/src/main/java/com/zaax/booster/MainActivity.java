
package com.zaax.booster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    int bg = Color.rgb(9, 10, 15);
    int panel = Color.rgb(22, 24, 33);
    int red = Color.rgb(255, 35, 65);
    int white = Color.WHITE;
    int gray = Color.rgb(155, 160, 175);

    LinearLayout main;

    int dp(float value) {
        return (int) (value * getResources()
            .getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        feature("⚡", "ANTI LAG", "Reduce background distractions",
            "ANTI LAG");
        feature("🎮", "BOOST GAME", "Prepare your device for gaming",
            "BOOST GAME");
        feature("▣", "LOW GRAPHICS", "Game graphics guide",
            "LOW GRAPHICS");
        feature("◉", "BOOST PING", "Check your network connection",
            "BOOST PING");

        section("DEVICE CONTROL");
        feature("👆", "FAST TOUCH", "Touch settings shortcut",
            "FAST TOUCH");
        feature("↻", "REFRESH RATE", "Open display settings",
            "REFRESH RATE");
        feature("▤", "DEVICE INFO", "View device specifications",
            "DEVICE INFO");
        feature("◆", "CPU BOOST", "CPU information",
            "CPU BOOST");
        feature("▦", "RESOLUTION", "Resolution information",
            "RESOLUTION");

        footer();

        setContentView(scroll);
    }

    void header() {
        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setOrientation(LinearLayout.HORIZONTAL);

        TextView logo = new TextView(this);
        logo.setText("♛");
        logo.setTextColor(red);
        logo.setTextSize(38);
        logo.setTypeface(null, Typeface.BOLD);
        top.addView(logo);

        LinearLayout names = new LinearLayout(this);
        names.setOrientation(LinearLayout.VERTICAL);
        names.setPadding(dp(12), 0, 0, 0);

        TextView title = text("BOOSTER ZAAX", 21, white, true);
        TextView sub = text("GAMING PERFORMANCE", 10, gray, true);

        names.addView(title);
        names.addView(sub);

        top.addView(names);
        main.addView(top);

        space(25);

        TextView welcome = text("LEVEL UP YOUR GAME", 23, white, true);
        main.addView(welcome);

        TextView desc = text(
            "Your gaming control center.",
            13, gray, false
        );
        main.addView(desc);

        space(20);
    }

    void statusCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(box(red, 22));

        TextView label = text("ZAAX SYSTEM", 12, gray, true);
        TextView ready = text("●  BOOSTER READY", 19, white, true);
        TextView version = text("VERSION 1.0  •  ANDROID", 10, gray, true);

        card.addView(label);
        spaceIn(card, 8);
        card.addView(ready);
        spaceIn(card, 7);
        card.addView(version);

        main.addView(card);
        space(25);
    }

    void section(String name) {
        space(8);
        TextView heading = text(name, 12, red, true);
        heading.setPadding(2, dp(8), 2, dp(8));
        main.addView(heading);
    }

    void feature(String icon, String name,
                 String description, String action) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(15), dp(15), dp(15), dp(15));
        card.setBackground(box(panel, 18));

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                -1, dp(82)
            );
        params.setMargins(0, dp(5), 0, dp(5));

        TextView symbol = text(icon, 25, red, true);
        symbol.setGravity(Gravity.CENTER);
        card.addView(symbol, new LinearLayout.LayoutParams(
            dp(42), -1
        ));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, 0, 0);

        details.addView(text(name, 14, white, true));
        details.addView(text(description, 10, gray, false));

        card.addView(details, new LinearLayout.LayoutParams(
            0, -2, 1
        ));

        TextView arrow = text("›", 28, red, true);
        card.addView(arrow);

        card.setOnClickListener(v -> openFeature(action));

        main.addView(card, params);
    }

    void openFeature(String action) {

        if (action.equals("DEVICE INFO")) {
            deviceInfo();
            return;
        }

        if (action.equals("REFRESH RATE")) {
            openSettings(Settings.ACTION_DISPLAY_SETTINGS);
            return;
        }

        if (action.equals("FAST TOUCH")) {
            openSettings(Settings.ACTION_SETTINGS);
            return;
        }

        String message;

        switch (action) {
            case "ANTI LAG":
                message = "Close unused apps manually and keep enough free storage.";
                break;
            case "BOOST GAME":
                message = "Your device is ready. Open your game and adjust its settings.";
                break;
            case "LOW GRAPHICS":
                message = "Open your game's graphics settings and choose a lower quality.";
                break;
            case "BOOST PING":
                message = "Check your Wi-Fi or mobile signal. Ping depends on your network and game server.";
                break;
            case "CPU BOOST":
                message = "CPU clock control requires system privileges. This app cannot force CPU speed.";
                break;
            default:
                message = "60%, 70%, and 80% resolution presets need special system permissions. They are not applied yet.";
                break;
        }

        new AlertDialog.Builder(this)
            .setTitle(action)
            .setMessage(message)
            .setPositiveButton("GOT IT", null)
            .show();
    }

    void deviceInfo() {
        ActivityManager manager =
            (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memory =
            new ActivityManager.MemoryInfo();

        manager.getMemoryInfo(memory);

        String info =
            "DEVICE: " + android.os.Build.MODEL +
            "\nBRAND: " + android.os.Build.BRAND +
            "\nANDROID: " + android.os.Build.VERSION.RELEASE +
            "\nRAM TOTAL: " +
            (memory.totalMem / (1024 * 1024)) + " MB" +
            "\nRAM AVAILABLE: " +
            (memory.availMem / (1024 * 1024)) + " MB";

        new AlertDialog.Builder(this)
            .setTitle("DEVICE INFORMATION")
            .setMessage(info)
            .setPositiveButton("CLOSE", null)
            .show();
    }

    void openSettings(String action) {
        try {
            startActivity(new Intent(action));
        } catch (Exception e) {
            Toast.makeText(this,
                "Settings unavailable",
                Toast.LENGTH_SHORT).show();
        }
    }

    void footer() {
        space(25);
        TextView end = text(
            "♛  POWERED BY ZAAX  ♛",
            12, red, true
        );
        end.setGravity(Gravity.CENTER);
        main.addView(end);
    }

    TextView text(String value, int size,
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

    GradientDrawable box(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));
        shape.setStroke(dp(1), Color.rgb(48, 35, 48));
        return shape;
    }

    void space(int height) {
        View v = new View(this);
        main.addView(v, new LinearLayout.LayoutParams(1, dp(height)));
    }

    void spaceIn(LinearLayout layout, int height) {
        View v = new View(this);
        layout.addView(v, new LinearLayout.LayoutParams(1, dp(height)));
    }
}
