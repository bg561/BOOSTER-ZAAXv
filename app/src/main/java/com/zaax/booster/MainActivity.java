
package com.zaax.booster;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.Intent;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private LinearLayout root;
    private TextView boostStatus;
    private boolean boostActive = false;

    private final int BG = Color.rgb(12, 12, 16);
    private final int CARD = Color.rgb(25, 25, 32);
    private final int RED = Color.rgb(230, 25, 55);
    private final int WHITE = Color.WHITE;
    private final int GREY = Color.rgb(165, 165, 175);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(Color.BLACK);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(22), dp(20), dp(28));
        root.setBackgroundColor(BG);

        scroll.addView(root);
        setContentView(scroll);

        buildDashboard();
    }

    private void buildDashboard() {

        TextView crown = text("♛", 48, RED, true);
        crown.setGravity(Gravity.CENTER);
        root.addView(crown);

        TextView title = text("BOOSTER ZAAX", 27, WHITE, true);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = text(
            "GAMING PERFORMANCE CENTER  •  V1.0",
            10, GREY, true
        );
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle);

        space(25);

        LinearLayout statusCard = card();
        statusCard.setOrientation(LinearLayout.VERTICAL);

        addText(statusCard, "ZAAX PERFORMANCE STATUS", 12, GREY, true);

        boostStatus = text("READY TO BOOST", 21, RED, true);
        statusCard.addView(boostStatus);

        addText(statusCard,
            "Your gaming control center is ready.",
            12, GREY, false);

        root.addView(statusCard);

        space(22);

        addText(root, "MAIN CONTROL", 15, WHITE, true);
        space(10);

        addFeature("⚡", "START BOOST",
            "Gaming session", this::startBoost);

        addFeature("🎮", "BOOST GAME",
            "Game preparation", this::boostGame);

        addFeature("🚀", "ANTI LAG",
            "Performance tips", this::antiLag);

        addFeature("🎨", "LOW GRAPHICS",
            "Graphics guide", this::lowGraphics);

        addFeature("📡", "BOOST PING",
            "Network diagnostics", this::boostPing);

        addFeature("👆", "FAST TOUCH",
            "Touch settings guide", this::fastTouch);

        addFeature("🖥", "SET HZ 60 / 144",
            "Display refresh settings", this::refreshRate);

        addFeature("📱", "DEVICE INFORMATION",
            "Hardware information", this::deviceInfo);

        addFeature("🔍", "RESOLUTION 60 / 70 / 80%",
            "Display scaling guide", this::resolution);

        addFeature("⚙", "CPU BOOST",
            "CPU information", this::cpuInfo);

        space(25);

        TextView footer = text(
            "POWERED BY ZAAX  •  OWNER ALBY",
            10, GREY, true
        );
        footer.setGravity(Gravity.CENTER);
        root.addView(footer);
    }

    private void startBoost() {
        boostActive = !boostActive;

        if (boostActive) {
            boostStatus.setText("GAMING SESSION ACTIVE");
            boostStatus.setTextColor(RED);

            message("Gaming session started.");
        } else {
            boostStatus.setText("READY TO BOOST");
            message("Gaming session ended.");
        }
    }

    private void boostGame() {
        showDialog("BOOST GAME",
            "Prepare your gaming session:\n\n" +
            "• Close apps you are not using.\n" +
            "• Use a stable internet connection.\n" +
            "• Choose suitable graphics settings.\n\n" +
            "ZAAX does not force other games to change their settings.");
    }

    private void antiLag() {
        showDialog("ANTI LAG",
            "Performance checklist:\n\n" +
            "• Close unnecessary apps.\n" +
            "• Keep enough storage space.\n" +
            "• Avoid overheating.\n" +
            "• Use a stable network.\n\n" +
            "These steps help reduce avoidable slowdowns.");
    }

    private void lowGraphics() {
        showDialog("LOW GRAPHICS MODE",
            "For supported games:\n\n" +
            "• Open the game's Settings.\n" +
            "• Find Graphics Mode.\n" +
            "• Select Manual if available.\n" +
            "• Lower the graphics quality.\n\n" +
            "ZAAX cannot force graphics settings inside other apps.");
    }

    private void boostPing() {
        showDialog("BOOST PING",
            "NETWORK DIAGNOSTICS\n\n" +
            "• Check your Wi-Fi or mobile signal.\n" +
            "• Avoid downloads while gaming.\n" +
            "• Choose a nearby game server if available.\n\n" +
            "ZAAX cannot guarantee lower ping or change your ISP routing.");
    }

    private void fastTouch() {
        showDialog("FAST TOUCH",
            "TOUCH SETTINGS\n\n" +
            "Use your phone's supported touch settings if available.\n\n" +
            "Touch sampling rate is controlled by the device hardware. " +
            "A normal app cannot force it to increase.");
    }

    private void refreshRate() {
        try {
            startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
        } catch (Exception e) {
            message("Display settings are unavailable.");
        }
    }

    private void deviceInfo() {
        String model = Build.MANUFACTURER + " " + Build.MODEL;
        String android = Build.VERSION.RELEASE;
        String hardware = Build.HARDWARE;
        String cores = String.valueOf(
            Runtime.getRuntime().availableProcessors()
        );

        ActivityManager.MemoryInfo memory =
            new ActivityManager.MemoryInfo();

        ActivityManager manager =
            (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        manager.getMemoryInfo(memory);

        String ram = String.format("%.2f GB",
            memory.totalMem / (1024.0 * 1024.0 * 1024.0));

        float hz = getWindowManager()
            .getDefaultDisplay().getRefreshRate();

        showDialog("DEVICE INFORMATION",
            "MODEL: " + model +
            "\nANDROID: " + android +
            "\nHARDWARE: " + hardware +
            "\nCPU CORES: " + cores +
            "\nTOTAL RAM: " + ram +
            "\nCURRENT DISPLAY: " +
            String.format("%.0f Hz", hz));
    }

    private void resolution() {
        showDialog("RESOLUTION CONTROL",
            "AVAILABLE PRESETS\n\n" +
            "60%  •  Low scaling\n" +
            "70%  •  Medium scaling\n" +
            "80%  •  High scaling\n\n" +
            "These are planned presets. A normal Android app " +
            "cannot force system-wide resolution scaling without " +
            "supported system permissions.");
    }

    private void cpuInfo() {
        showDialog("CPU INFORMATION",
            "PROCESSOR: " + Build.HARDWARE +
            "\nAVAILABLE CORES: " +
            Runtime.getRuntime().availableProcessors() +
            "\n\nZAAX can display processor information, " +
            "but cannot force CPU clock speed on a standard " +
            "non-root Android device.");
    }

    private void addFeature(String icon, String name,
                            String description, Runnable action) {

        LinearLayout item = card();
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER_VERTICAL);

        TextView symbol = text(icon, 25, RED, true);
        symbol.setGravity(Gravity.CENTER);
        item.addView(symbol,
            new LinearLayout.LayoutParams(dp(48), dp(55)));

        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setPadding(dp(10), 0, 0, 0);

        addText(labels, name, 13, WHITE, true);
        addText(labels, description, 11, GREY, false);

        item.addView(labels,
            new LinearLayout.LayoutParams(0,
                -2, 1));

        TextView arrow = text("›", 28, RED, true);
        item.addView(arrow);

        item.setOnClickListener(v -> action.run());

        root.addView(item);
        space(9);
    }

    private LinearLayout card() {
        LinearLayout box = new LinearLayout(this);
        box.setPadding(dp(16), dp(15), dp(16), dp(15));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(CARD);
        bg.setCornerRadius(dp(15));
        bg.setStroke(dp(1), Color.rgb(65, 25, 38));

        box.setBackground(bg);
        return box;
    }

    private TextView text(String value, int size,
                          int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private void addText(LinearLayout parent, String value,
                         int size, int color, boolean bold) {
        parent.addView(text(value, size, color, bold));
    }

    private void showDialog(String title, String content) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(content)
            .setPositiveButton("GOT IT", null)
            .show();
    }

    private void message(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    private void space(int height) {
        View gap = new View(this);
        root.addView(gap,
            new LinearLayout.LayoutParams(1, dp(height)));
    }

    private int dp(int value) {
        return (int) (value *
            getResources().getDisplayMetrics().density + 0.5f);
    }
}
