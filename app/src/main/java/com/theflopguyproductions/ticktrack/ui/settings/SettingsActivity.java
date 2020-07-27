package com.theflopguyproductions.ticktrack.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
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

    private ConstraintLayout themeLayout;
    private TextView themeName, themeTitle;
    private ImageButton backButton;
    private ScrollView settingsScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        themeLayout = findViewById(R.id.themeSettingsLayout);
        themeTitle = findViewById(R.id.themeSettingsLabel);
        themeName = findViewById(R.id.themeValueSettingsTextView);
        backButton = findViewById(R.id.settingsActivityBackButton);
        settingsScrollView = findViewById(R.id.settingsActivityScrollView);

        TickTrackThemeSetter.settingsActivityTheme(this, themeTitle, themeName, settingsScrollView, themeLayout);

        themeLayout.setOnClickListener(view -> {
            ThemeDialog themeDialog = new ThemeDialog(this, TickTrackDatabase.getThemeMode(this));
            themeDialog.show();
        });

        backButton.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, SoYouADeveloperHuh.class));
        finish();
    }
}