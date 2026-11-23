
package com.zaax.booster;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonitorService extends Service {

    private static final String CHANNEL_ID = "zaax_monitor";

    private WindowManager windowManager;
    private LinearLayout panel;
    private WindowManager.LayoutParams params;

    private TextView ramText;
    private TextView cpuText;
    private TextView gpuText;
    private TextView fpsText;
    private TextView pingText;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private final ExecutorService pingExecutor =
            Executors.newSingleThreadExecutor();

    private long previousTotal = 0;
    private long previousIdle = 0;

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateRam();
            updateCpu();

            if (panel != null) {
                handler.postDelayed(this, 1500);
            }
        }
    };

    private final Runnable pingUpdater = new Runnable() {
        @Override
        public void run() {
            if (panel != null) {
                pingExecutor.execute(
                        MonitorService.this::testPing
                );

                handler.postDelayed(this, 15000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForeground(1, buildNotification());

        if (Build.VERSION.SDK_INT >= 23 &&
                !Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }

        createPanel();

        handler.post(updater);
        handler.post(pingUpdater);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "ZAAX Gaming Monitor",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= 26) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        return builder
                .setContentTitle("BOOSTER ZAAX")
                .setContentText("Gaming Monitor aktif")
                .setSmallIcon(
                        android.R.drawable.ic_menu_info_details
                )
                .build();
    }

    private TextView createMetric(String initialText) {
        TextView text = new TextView(this);

        text.setText(initialText);
        text.setTextColor(Color.WHITE);
        text.setTextSize(9);
        text.setSingleLine(true);
        text.setTypeface(null, 1);
        text.setPadding(5, 0, 5, 0);

        return text;
    }

    private void createPanel() {

        windowManager =
                (WindowManager) getSystemService(WINDOW_SERVICE);

        panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.HORIZONTAL);
        panel.setGravity(Gravity.CENTER_VERTICAL);
        panel.setPadding(7, 5, 5, 5);

        GradientDrawable background = new GradientDrawable();
        background.setColor(0xDD101014);
        background.setCornerRadius(24);
        background.setStroke(1, 0xFFFF1744);

        panel.setBackground(background);

        TextView title = createMetric("♛ ZAAX");
        title.setTextColor(0xFFFF1744);

        ramText = createMetric("RAM --");
        cpuText = createMetric("CPU --");
        gpuText = createMetric("GPU N/A");
        fpsText = createMetric("FPS N/A");
        pingText = createMetric("PING --");

        TextView close = createMetric("×");
        close.setTextColor(0xFFFF1744);
        close.setTextSize(16);
        close.setPadding(5, 0, 3, 0);
        close.setOnClickListener(v -> stopSelf());

        panel.addView(title);
        panel.addView(ramText);
        panel.addView(cpuText);
        panel.addView(gpuText);
        panel.addView(fpsText);
        panel.addView(pingText);
        panel.addView(close);

        int overlayType;

        if (Build.VERSION.SDK_INT >= 26) {
            overlayType =
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            overlayType =
                    WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 10;
        params.y = 100;

        title.setOnTouchListener(new View.OnTouchListener() {

            private int startX;
            private int startY;

            private float touchX;
            private float touchY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        startX = params.x;
                        startY = params.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = startX +
                                (int) (event.getRawX() - touchX);

                        params.y = startY +
                                (int) (event.getRawY() - touchY);

                        try {
                            windowManager.updateViewLayout(
                                    panel, params
                            );
                        } catch (Exception ignored) {
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        return true;
                }

                return true;
            }
        });

        windowManager.addView(panel, params);
    }

    private void updateRam() {

        if (ramText == null) return;

        try {
            ActivityManager manager =
                    (ActivityManager) getSystemService(
                            Context.ACTIVITY_SERVICE
                    );

            ActivityManager.MemoryInfo info =
                    new ActivityManager.MemoryInfo();

            manager.getMemoryInfo(info);

            long total = info.totalMem / (1024 * 1024);
            long available = info.availMem / (1024 * 1024);
            long used = total - available;

            ramText.setText(
                    String.format(
                            Locale.US,
                            "RAM %d/%dM",
                            used,
                            total
                    )
            );

        } catch (Exception e) {
            ramText.setText("RAM N/A");
        }
    }

    private void updateCpu() {

        if (cpuText == null) return;

        try {
            BufferedReader reader =
                    new BufferedReader(
                            new FileReader("/proc/stat")
                    );

            String line = reader.readLine();
            reader.close();

            if (line == null || !line.startsWith("cpu ")) {
                cpuText.setText("CPU N/A");
                return;
            }

            String[] values = line.trim().split("\\s+");

            long total = 0;

            for (int i = 1; i < values.length; i++) {
                total += Long.parseLong(values[i]);
            }

            long idle = Long.parseLong(values[4]);

            if (values.length > 5) {
                idle += Long.parseLong(values[5]);
            }

            if (previousTotal != 0) {

                long totalDiff = total - previousTotal;
                long idleDiff = idle - previousIdle;

                if (totalDiff > 0) {
                    int usage = (int) (
                            100 * (totalDiff - idleDiff) / totalDiff
                    );

                    usage = Math.max(0, Math.min(100, usage));

                    cpuText.setText("CPU " + usage + "%");
                }
            }

            previousTotal = total;
            previousIdle = idle;

        } catch (Exception e) {
            cpuText.setText("CPU N/A");
        }
    }

    private void testPing() {

        HttpURLConnection connection = null;
        long start = SystemClock.elapsedRealtime();

        try {
            URL url = new URL(
                    "https://clients3.google.com/generate_204"
            );

            connection =
                    (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(4000);
            connection.setReadTimeout(4000);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");

            int response = connection.getResponseCode();

            long elapsed =
                    SystemClock.elapsedRealtime() - start;

            if (response >= 200 && response < 400) {
                final String result =
                        "PING " + elapsed + "ms";

                handler.post(() -> {
                    if (pingText != null) {
                        pingText.setText(result);
                    }
                });
            } else {
                showPingUnavailable();
            }

        } catch (Exception e) {
            showPingUnavailable();

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void showPingUnavailable() {
        handler.post(() -> {
            if (pingText != null) {
                pingText.setText("PING N/A");
            }
        });
    }

    @Override
    public void onDestroy() {

        handler.removeCallbacks(updater);
        handler.removeCallbacks(pingUpdater);

        pingExecutor.shutdownNow();

        if (windowManager != null && panel != null) {
            try {
                windowManager.removeView(panel);
            } catch (Exception ignored) {
            }

            panel = null;
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
