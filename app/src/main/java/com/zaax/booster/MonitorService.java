
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
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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

    private GLSurfaceView glView;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private final ExecutorService pingExecutor =
            Executors.newSingleThreadExecutor();

    private long previousCpuTime = 0;
    private long previousWallTime = 0;

    private int cpuCount = 1;

    private long lastFrameTime = 0;
    private int frameCount = 0;
    private int measuredFps = 0;

    private String gpuName = "GPU N/A";

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateRam();
            updateCpu();
            updateFps();

            if (panel != null) {
                handler.postDelayed(this, 1500);
            }
        }
    };

    private final Runnable pingUpdater = new Runnable() {
        @Override
        public void run() {
            if (panel != null && !pingExecutor.isShutdown()) {
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

        cpuCount = Math.max(
                1,
                Runtime.getRuntime().availableProcessors()
        );

        createNotificationChannel();
        startForeground(1, buildNotification());

        if (Build.VERSION.SDK_INT >= 23 &&
                !Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }

        createPanel();
        createGpuDetector();

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
        fpsText = createMetric("FPS --");
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

    private void createGpuDetector() {

        try {
            glView = new GLSurfaceView(this);
            glView.setEGLContextClientVersion(2);

            glView.setRenderer(new GLSurfaceView.Renderer() {

                @Override
                public void onSurfaceCreated(
                        GL10 gl,
                        EGLConfig config) {

                    String renderer =
                            GLES20.glGetString(
                                    GLES20.GL_RENDERER
                            );

                    String vendor =
                            GLES20.glGetString(
                                    GLES20.GL_VENDOR
                            );

                    String result = "GPU N/A";

                    if (renderer != null &&
                            !renderer.trim().isEmpty()) {
                        result = "GPU " + renderer.trim();
                    } else if (vendor != null &&
                            !vendor.trim().isEmpty()) {
                        result = "GPU " + vendor.trim();
                    }

                    final String finalResult = result;

                    handler.post(() -> {
                        gpuName = finalResult;

                        if (gpuText != null) {
                            gpuText.setText(gpuName);
                        }
                    });
                }

                @Override
                public void onSurfaceChanged(
                        GL10 gl,
                        int width,
                        int height) {
                }

                @Override
                public void onDrawFrame(GL10 gl) {
                }
            });

            glView.setRenderMode(
                    GLSurfaceView.RENDERMODE_WHEN_DIRTY
            );

            glView.setVisibility(View.GONE);

            glView.onResume();
            glView.requestRender();

        } catch (Exception e) {
            if (gpuText != null) {
                gpuText.setText("GPU N/A");
            }
        }
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
            long currentCpuTime =
                    android.os.Process.getElapsedCpuTime();

            long currentWallTime =
                    SystemClock.elapsedRealtime();

            if (previousCpuTime == 0 ||
                    previousWallTime == 0) {

                previousCpuTime = currentCpuTime;
                previousWallTime = currentWallTime;

                cpuText.setText("CPU --");
                return;
            }

            long cpuDiff =
                    currentCpuTime - previousCpuTime;

            long wallDiff =
                    currentWallTime - previousWallTime;

            previousCpuTime = currentCpuTime;
            previousWallTime = currentWallTime;

            if (wallDiff <= 0 || cpuDiff < 0) {
                cpuText.setText("CPU --");
                return;
            }

            double usage =
                    (cpuDiff * 100.0) /
                    (wallDiff * cpuCount);

            int percentage = (int) Math.round(usage);

            percentage = Math.max(
                    0,
                    Math.min(100, percentage)
            );

            cpuText.setText("CPU " + percentage + "%");

        } catch (Exception e) {
            cpuText.setText("CPU N/A");
            previousCpuTime = 0;
            previousWallTime = 0;
        }
    }

    private void updateFps() {

        if (fpsText == null) return;

        long now = SystemClock.elapsedRealtime();

        if (lastFrameTime == 0) {
            lastFrameTime = now;
            frameCount = 0;
            return;
        }

        frameCount++;

        long elapsed = now - lastFrameTime;

        if (elapsed >= 1000) {
            measuredFps =
                    (int) (frameCount * 1000L / elapsed);

            lastFrameTime = now;
            frameCount = 0;

            fpsText.setText(
                    "FPS " + measuredFps + " (UI)"
            );
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

        if (glView != null) {
            try {
                glView.onPause();
            } catch (Exception ignored) {
            }
            glView = null;
        }

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
