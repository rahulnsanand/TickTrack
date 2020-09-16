package com.theflopguyproductions.ticktrack.widgets.shortcuts.panel;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.theflopguyproductions.ticktrack.R;

public class ShortcutsPanelConfigActivity extends AppCompatActivity {

    private TextView blackText, grayText, lightText;
    private ImageView blackImage, grayImage, lightImage;
    private ImageView blackCheck, grayCheck, lightCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcuts_panel_config);
    }
}