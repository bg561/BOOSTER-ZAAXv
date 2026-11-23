
package com.zaax.booster;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
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

import java.util.Locale;

public class MonitorService extends Service {

    private static final String CHANNEL_ID = "zaax_monitor";

    private WindowManager windowManager;
    private LinearLayout panel;
    private TextView ramText;
    private WindowManager.LayoutParams params;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

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
                .setSmallIcon(
                        android.R.drawable.ic_menu_info_details
                )
                .build();
    }

    private void createPanel() {

        windowManager =
                (WindowManager) getSystemService(WINDOW_SERVICE);

        panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.HORIZONTAL);
        panel.setGravity(Gravity.CENTER_VERTICAL);

        panel.setPadding(10, 5, 8, 5);

        GradientDrawable background = new GradientDrawable();
        background.setColor(0xCC141418);
        background.setCornerRadius(30);
        background.setStroke(1, 0xCCFF3030);

        panel.setBackground(background);

        // ZAAX DRAG HANDLE
        TextView title = new TextView(this);
        title.setText("♛ ZAAX");
        title.setTextColor(Color.RED);
        title.setTextSize(11);
        title.setTypeface(null, 1);
        title.setPadding(3, 0, 10, 0);

        // RAM INFORMATION
        ramText = new TextView(this);
        ramText.setTextColor(Color.WHITE);
        ramText.setTextSize(11);
        ramText.setSingleLine(true);
        ramText.setTypeface(null, 1);

        // CLOSE BUTTON
        TextView close = new TextView(this);
        close.setText(" × ");
        close.setTextColor(Color.RED);
        close.setTextSize(17);
        close.setPadding(8, 0, 2, 0);

        close.setOnClickListener(v -> stopSelf());

        panel.addView(title);

        panel.addView(ramText);

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
        params.x = 20;
        params.y = 100;

        // DRAG MONITOR
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
                        "RAM %d/%d MB",
                        used,
                        total
                )
        );
    }

    @Override
    public void onDestroy() {

        handler.removeCallbacks(updater);

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
