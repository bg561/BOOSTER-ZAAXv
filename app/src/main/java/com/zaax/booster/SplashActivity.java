
package com.zaax.booster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        getWindow().setStatusBarColor(0xFF090909);
        getWindow().setNavigationBarColor(0xFF090909);

        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.splash_logo);
        TextView title = findViewById(R.id.splash_title);
        TextView subtitle = findViewById(R.id.splash_subtitle);
        TextView creator = findViewById(R.id.splash_creator);

        Animation zoom = AnimationUtils.loadAnimation(
                this, R.anim.zaax_zoom_in);

        Animation fade = AnimationUtils.loadAnimation(
                this, R.anim.zaax_fade_in);

        Animation bounce = AnimationUtils.loadAnimation(
                this, R.anim.zaax_bounce);

        logo.startAnimation(zoom);

        handler.postDelayed(() -> {
            title.startAnimation(bounce);
            subtitle.startAnimation(fade);
            creator.startAnimation(fade);
        }, 700);

        handler.postDelayed(() -> {
            startActivity(new Intent(
                    SplashActivity.this,
                    MainActivity.class));

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out);

            finish();
        }, 4500);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
