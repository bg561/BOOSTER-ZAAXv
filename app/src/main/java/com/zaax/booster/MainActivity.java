
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
        layout.setPadding(28,30,28,30);
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
        title.setTypeface(null,Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView version = new TextView(this);
        version.setText("GAMING PERFORMANCE V1.0");
        version.setTextColor(Color.LTGRAY);
        version.setGravity(Gravity.CENTER);
        layout.addView(version);

        addButton("START BOOST");
        addButton("LIGHT GRAPHIC MODE");
        addButton("DEVICE INFORMATION");

        setContentView(layout);
    }

    void addButton(String name) {
        Button button = new Button(this);
        button.setText(name);
        button.setTextColor(Color.WHITE);
        button.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(
                Color.rgb(180,15,35)
            )
        );

        layout.addView(button);

        button.setOnClickListener(v ->
            Toast.makeText(this,
                "BOOSTER ZAAX V1.0",
                Toast.LENGTH_SHORT).show()
        );
    }
}
