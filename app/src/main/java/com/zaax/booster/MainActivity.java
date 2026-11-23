
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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

        connectButtons();
    }

    private void connectButtons() {
        View root = findViewById(android.R.id.content);
        connect(root);
    }

    private void connect(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;
            String name = button.getText().toString();

            button.setOnClickListener(v -> runFeature(name));
        }

        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group =
                    (android.view.ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {
                connect(group.getChildAt(i));
            }
        }
    }

    private void runFeature(String name) {
        switch (name) {
            case "ANTI LAG":
                antiLag();
                break;
            case "BOOST GAME":
                chooseGame();
                break;
            case "BOOST PING":
                testPing();
                break;
            case "LOW GRAPHICS":
                pixelGraphics();
                break;
            case "FAST TOUCH":
                openSettings(Settings.ACTION_SETTINGS);
                break;
            case "DEVICE INFORMATION":
                deviceInfo();
                break;
        }
    }

    private void antiLag() {
        ActivityManager manager =
                (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memory =
                new ActivityManager.MemoryInfo();

        manager.getMemoryInfo(memory);

        long total = memory.totalMem / 1048576;
        long available = memory.availMem / 1048576;

        show("ZAAX ANTI LAG",
                "TOTAL RAM: " + total + " MB\n" +
                "AVAILABLE RAM: " + available + " MB\n\n" +
                "Android membatasi aplikasi untuk menghentikan aplikasi lain.");
    }

    private void chooseGame() {
        PackageManager pm = getPackageManager();

        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);

        List<android.content.pm.ResolveInfo> apps =
                pm.queryIntentActivities(query, 0);

        List<String> names = new ArrayList<>();
        List<Intent> intents = new ArrayList<>();

        for (android.content.pm.ResolveInfo app : apps) {
            String pkg = app.activityInfo.packageName;

            if (pkg.equals(getPackageName())) continue;

            Intent launch = pm.getLaunchIntentForPackage(pkg);

            if (launch != null) {
                names.add(app.loadLabel(pm).toString());
                intents.add(launch);
            }
        }

        if (names.isEmpty()) {
            show("BOOST GAME", "Tidak ada aplikasi yang ditemukan.");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("PILIH GAME")
                .setItems(names.toArray(new String[0]), (d, which) -> {
                    try {
                        startActivity(intents.get(which));
                    } catch (Exception e) {
                        show("BOOST GAME", "Gagal membuka aplikasi.");
                    }
                })
                .setNegativeButton("CLOSE", null)
                .show();
    }

    private void pixelGraphics() {
        String[] options = {
                "LOW GRAPHICS",
                "VERY LOW GRAPHICS",
                "PIXEL / BLOCKY LOOK"
        };

        new AlertDialog.Builder(this)
                .setTitle("ZAAX GRAPHICS")
                .setItems(options, (d, which) -> {
                    String message =
                            "Atur kualitas grafis dari pengaturan game.\n\n" +
                            "BOOSTER ZAAX tidak dapat mengubah tekstur internal game secara langsung.";

                    show(options[which], message);
                })
                .setNegativeButton("CLOSE", null)
                .show();
    }

    private void testPing() {
        show("BOOST PING", "Sedang menguji respons internet...");

        new Thread(() -> {
            String result;

            HttpURLConnection connection = null;

            try {
                long start = System.currentTimeMillis();

                URL url = new URL(
                        "https://www.google.com/generate_204");

                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int response = connection.getResponseCode();

                long elapsed =
                        System.currentTimeMillis() - start;

                result = "INTERNET RESPONSE: " + elapsed + " ms\n" +
                        "HTTP STATUS: " + response +
                        "\n\nBukan ping server Roblox.";

            } catch (Exception e) {
                result = "Tes koneksi gagal atau timeout.";
            } finally {
                if (connection != null) connection.disconnect();
            }

            String output = result;
            runOnUiThread(() -> show("BOOST PING", output));

        }).start();
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
                "\nANDROID: " + Build.VERSION.RELEASE +
                "\nTOTAL RAM: " + memory.totalMem / 1048576 + " MB" +
                "\nAVAILABLE RAM: " + memory.availMem / 1048576 + " MB";

        show("DEVICE INFORMATION", info);
    }

    private void openSettings(String action) {
        try {
            startActivity(new Intent(action));
        } catch (Exception e) {
            show("SETTINGS", "Pengaturan tidak tersedia.");
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
