package com.theflopguyproductions.ticktrack.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceFragmentCompat;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout themeLayout;
    private int themeMode;
    private TextView themeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        themeLayout = findViewById(R.id.themeSettingsLayout);
        themeName = findViewById(R.id.themeValueSettingsTextView);

        setThemeText();

        themeLayout.setOnClickListener(view -> {
            ThemeDialog themeDialog = new ThemeDialog(this, themeMode);
            themeDialog.show();
        });

    }

    public void setThemeText(){
        themeMode = TickTrackDatabase.getThemeMode(this);
        if(themeMode==1){
            themeName.setText("Light");
        } else{
            themeName.setText("Dark");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, SoYouADeveloperHuh.class));
        finish();
    }
}