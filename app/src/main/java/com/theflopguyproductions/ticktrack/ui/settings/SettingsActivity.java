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
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout themeLayout, rootLayout;
    private TextView themeName, themeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        themeLayout = findViewById(R.id.themeSettingsLayout);
        themeTitle = findViewById(R.id.themeSettingsLabel);
        themeName = findViewById(R.id.themeValueSettingsTextView);
        rootLayout = findViewById(R.id.generalRootLayout);
        TickTrackThemeSetter.settingsActivityTheme(this, rootLayout, themeTitle, themeName);

        themeLayout.setOnClickListener(view -> {
            ThemeDialog themeDialog = new ThemeDialog(this, TickTrackDatabase.getThemeMode(this));
            themeDialog.show();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, SoYouADeveloperHuh.class));
        finish();
    }
}