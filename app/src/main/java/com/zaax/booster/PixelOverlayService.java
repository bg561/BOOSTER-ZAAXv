package com.zaax.booster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class PixelOverlayService extends Service {

    public static final String ACTION_STOP =
            "com.zaax.booster.STOP_PIXEL_EFFECT";

    private WindowManager windowManager;
    private PixelView pixelView;

    private static final String CHANNEL = "zaax_pixel_effect";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotification();

        Notification notification =
                new Notification.Builder(this, CHANNEL)
                        .setContentTitle("ZAAX PIXEL EFFECT")
                        .setContentText("Visual overlay is active")
                        .setSmallIcon(android.R.drawable.ic_menu_view)
                        .setOngoing(true)
                        .build();

        startForeground(2001, notification);

        windowManager = (WindowManager)
                getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null &&
                ACTION_STOP.equals(intent.getAction())) {
            stopEffect();
            return START_NOT_STICKY;
        }

        if (pixelView == null) {
            pixelView = new PixelView();

            WindowManager.LayoutParams params =
                    new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                            PixelFormat.TRANSLUCENT
                    );

            params.gravity = Gravity.TOP | Gravity.START;

            try {
                windowManager.addView(pixelView, params);
            } catch (Exception e) {
                stopSelf();
            }
        }

        return START_NOT_STICKY;
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL,
                            "ZAAX Pixel Effect",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    private void stopEffect() {
        if (pixelView != null) {
            try {
                windowManager.removeView(pixelView);
            } catch (Exception ignored) {
            }

            pixelView = null;
        }

        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (pixelView != null) {
            try {
                windowManager.removeView(pixelView);
            } catch (Exception ignored) {
            }
            pixelView = null;
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class PixelView extends View {

        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final float density;

        PixelView() {
            super(PixelOverlayService.this);
            density = getResources().getDisplayMetrics().density;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float step = 22 * density;
            float block = 10 * density;

            paint.setStyle(Paint.Style.FILL);

            for (float y = 0; y < getHeight(); y += step) {
                for (float x = 0; x < getWidth(); x += step) {

                    if (((int)(x / step) +
                            (int)(y / step)) % 2 == 0) {

                        paint.setColor(Color.argb(24, 180, 0, 20));

                        canvas.drawRect(
                                x, y,
                                x + block, y + block,
                                paint
                        );
                    }
                }
            }
        }
    }
}
