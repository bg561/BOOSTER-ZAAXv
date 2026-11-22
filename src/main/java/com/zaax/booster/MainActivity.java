
package com.zaax.booster;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(12,12,16));
        getWindow().setNavigationBarColor(Color.BLACK);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(28, 30, 28, 30);
        layout.setBackgroundColor(Color.rgb(12,12,16));

        TextView logo = new TextView(this);
        logo.setText("♛");
        logo.setTextSize(65);
        logo.setTextColor(Color.RED);
        logo.setGravity(Gravity.CENTER);
        layout.addView(logo);

        TextView title = new TextView(this);
        title.setText("BOOSTER ZAAX");
        title.setTextSize(28);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView version = new TextView(this);
        version.setText("GAMING PERFORMANCE V1.0");
        version.setTextSize(12);
        version.setTextColor(Color.LTGRAY);
        version.setGravity(Gravity.CENTER);
        layout.addView(version);

        addSpace();

        addButton("START BOOST", "Panel boost V1 siap. Optimasi sistem belum diaktifkan.");
        addButton("LIGHT GRAPHIC MODE", "Mode grafik masih dalam tahap pengembangan.");
        addButton("DEVICE INFORMATION", "Informasi perangkat akan hadir di update berikutnya.");

        addSpace();

        TextView footer = new TextView(this);
        footer.setText("POWERED BY ZAAX\nOWNER ALBY");
        footer.setTextColor(Color.GRAY);
        footer.setTextSize(12);
        footer.setGravity(Gravity.CENTER);
        layout.addView(footer);

        setContentView(layout);
    }

    void addSpace() {
        TextView space = new TextView(this);
        space.setHeight(35);
        layout.addView(space);
    }

    void addButton(String name, String message) {
        Button button = new Button(this);
        button.setText(name);
        button.setTextColor(Color.WHITE);
        button.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(
                Color.rgb(180, 15, 35)
            )
        );

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            );

        params.setMargins(0, 10, 0, 10);
        layout.addView(button, params);

        button.setOnClickListener(v ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        );
    }
}
