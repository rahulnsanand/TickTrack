package com.theflopguyproductions.ticktrack.screensaver;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;

public class ScreensaverActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screensaver);

        rootLayout = findViewById(R.id.screenSaverRootLayout);

        rootLayout.setOnClickListener(view ->
                startActivity(new Intent(this, SoYouADeveloperHuh.class)));

        getWindow().setStatusBarColor(getResources().getColor(R.color.Accent));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |  View.SYSTEM_UI_FLAG_LOW_PROFILE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


}