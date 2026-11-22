
package com.zaax.booster;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.app.ActivityManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.os.Handler;
import android.os.Looper;

public class MonitorService extends Service {

    private static final String CHANNEL_ID = "zaax_monitor";

    private WindowManager windowManager;
    private LinearLayout panel;
    private TextView ramText;
    private WindowManager.LayoutParams params;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateRam();

            if (panel != null) {
                handler.postDelayed(this, 1500);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForeground(1, buildNotification());

        if (Build.VERSION.SDK_INT >= 23 &&
                !android.provider.Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }

        createPanel();
        handler.post(updater);
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
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .build();
    }

    private void createPanel() {
        windowManager =
                (WindowManager) getSystemService(WINDOW_SERVICE);

        panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(18, 14, 18, 14);
        panel.setBackgroundColor(Color.rgb(20, 20, 24));

        TextView title = new TextView(this);
        title.setText("♛ ZAAX MONITOR");
        title.setTextColor(Color.RED);
        title.setTextSize(16);
        title.setPadding(0, 0, 0, 10);

        ramText = new TextView(this);
        ramText.setTextColor(Color.WHITE);
        ramText.setTextSize(13);

        TextView close = new TextView(this);
        close.setText("TUTUP MONITOR");
        close.setTextColor(Color.RED);
        close.setPadding(0, 12, 0, 0);
        close.setOnClickListener(v -> stopSelf());

        panel.addView(title);
        panel.addView(ramText);
        panel.addView(close);

        int overlayType;

        if (Build.VERSION.SDK_INT >= 26) {
            overlayType =
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            overlayType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 20;
        params.y = 100;

        panel.setOnTouchListener(new View.OnTouchListener() {
            private int startX, startY;
            private float touchX, touchY;

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

                        windowManager.updateViewLayout(panel, params);
                        return true;
                }

                return false;
            }
        });

        windowManager.addView(panel, params);
    }

    private void updateRam() {
        if (ramText == null) return;

        ActivityManager manager =
                (ActivityManager) getSystemService(
                        Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo info =
                new ActivityManager.MemoryInfo();

        manager.getMemoryInfo(info);

        long total = info.totalMem / (1024 * 1024);
        long available = info.availMem / (1024 * 1024);
        long used = total - available;

        ramText.setText(
                "RAM USED: " + used + " MB\n" +
                "RAM TOTAL: " + total + " MB"
        );
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(updater);

        if (windowManager != null && panel != null) {
            windowManager.removeView(panel);
            panel = null;
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

