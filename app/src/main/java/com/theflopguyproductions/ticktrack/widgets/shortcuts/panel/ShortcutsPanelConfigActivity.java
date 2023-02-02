package com.theflopguyproductions.ticktrack.widgets.shortcuts.panel;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.widgets.shortcuts.panel.data.ShortcutsData;

import java.util.ArrayList;

public class ShortcutsPanelConfigActivity extends AppCompatActivity {

    private TextView blackText, grayText, lightText;
    private ImageView blackImage, grayImage, lightImage;
    private ImageView blackCheck, grayCheck, lightCheck;
    private ConstraintLayout rootLayout;

    private TickTrackDatabase tickTrackDatabase;

    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onResume() {
        super.onResume();
        TickTrackThemeSetter.shortcutPanelActivity(tickTrackDatabase, this, rootLayout, blackText, grayText, lightText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcuts_panel_config);
        tickTrackDatabase = new TickTrackDatabase(this);

        rootLayout = findViewById(R.id.shortcutPanelConfigActivityRootLayout);
        blackText = findViewById(R.id.shortcutPanelConfigBlackText);
        grayText = findViewById(R.id.shortcutPanelConfigGrayText);
        lightText = findViewById(R.id.shortcutPanelConfigLightText);
        blackImage = findViewById(R.id.shortcutPanelConfigBlackButton);
        grayImage = findViewById(R.id.shortcutPanelConfigGrayButton);
        lightImage = findViewById(R.id.shortcutPanelConfigLightButton);
        blackCheck = findViewById(R.id.shortcutPanelConfigBlackButtonCheck);
        grayCheck = findViewById(R.id.shortcutPanelConfigGrayButtonCheck);
        lightCheck = findViewById(R.id.shortcutPanelConfigLightButtonCheck);

        setupTheme(tickTrackDatabase.getThemeMode());

        setupInit();

        setupClickListeners();

        Intent counterIntent = getIntent();
        Bundle extras = counterIntent.getExtras();
        if(extras!=null){
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_CANCELED, resultValue);

        if(widgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }
    }

    private void setupClickListeners() {

        blackImage.setOnClickListener(view -> confirmSelection(1));
        grayImage.setOnClickListener(view -> confirmSelection(2));
        lightImage.setOnClickListener(view -> confirmSelection(3));

    }

    private void confirmSelection(int i) {


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.shortcuts_panel_widget);

        views.setImageViewBitmap(R.id.ticktrackShortcutPanelWidgetTitle, buildUpdate("TickTrack Shortcut Console", this));

        if(i==1){
            views.setInt(R.id.shortcutWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_black);
            views.setInt(R.id.counterShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.timerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.quickTimerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.stopwatchShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.screensaverShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);


        } else if(i==2){
            views.setInt(R.id.shortcutWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_dark);
            views.setInt(R.id.counterShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.timerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.quickTimerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.stopwatchShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.screensaverShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);

        } else if(i==3){
            views.setInt(R.id.shortcutWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_light);
            views.setInt(R.id.counterShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.timerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.stopwatchShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.screensaverShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);

            views.setImageViewResource(R.id.counterShortcutButtonImage, R.drawable.ic_shortcut_counter_dark);
            views.setImageViewResource(R.id.timerShortcutButtonImage,  R.drawable.ic_shortcut_timer_dark);
            views.setImageViewResource(R.id.quickTimerShortcutButtonImage,  R.drawable.ic_shortcut_quicktimer_dark);
            views.setImageViewResource(R.id.stopwatchShortcutButtonImage,  R.drawable.ic_shortcut_stopwatch_dark);
            views.setImageViewResource(R.id.screensaverShortcutButtonImage,  R.drawable.ic_shortcut_screensaver_dark);
        } else {
            views.setInt(R.id.shortcutWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_light);
            views.setInt(R.id.counterShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.timerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.stopwatchShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.screensaverShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);

            views.setImageViewResource(R.id.counterShortcutButtonImage, R.drawable.ic_shortcut_counter_dark);
            views.setImageViewResource(R.id.timerShortcutButtonImage,  R.drawable.ic_shortcut_timer_dark);
            views.setImageViewResource(R.id.quickTimerShortcutButtonImage,  R.drawable.ic_shortcut_quicktimer_dark);
            views.setImageViewResource(R.id.stopwatchShortcutButtonImage,  R.drawable.ic_shortcut_stopwatch_dark);
            views.setImageViewResource(R.id.screensaverShortcutButtonImage,  R.drawable.ic_shortcut_screensaver_dark);
        }

        Intent createCounterIntent = new Intent(this, SoYouADeveloperHuh.class);
        createCounterIntent.putExtra("fragmentID","counterCreate");
        PendingIntent createCounterPending = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            createCounterPending = PendingIntent.getActivity(this, 98, createCounterIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            createCounterPending = PendingIntent.getActivity(this, 98, createCounterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        views.setOnClickPendingIntent(R.id.counterShortcutButton, createCounterPending);

        Intent createTimerIntent = new Intent(this, SoYouADeveloperHuh.class);
        createTimerIntent.putExtra("fragmentID","timerCreate");
        PendingIntent createTimerPending = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            createTimerPending = PendingIntent.getActivity(this, 987, createTimerIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            createTimerPending = PendingIntent.getActivity(this, 987, createTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        views.setOnClickPendingIntent(R.id.timerShortcutButton, createTimerPending);

        Intent createQTimerIntent = new Intent(this, SoYouADeveloperHuh.class);
        createQTimerIntent.putExtra("fragmentID","quickTimerCreate");
        PendingIntent createQTimerPending = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            createQTimerPending = PendingIntent.getActivity(this, 987, createQTimerIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            createQTimerPending = PendingIntent.getActivity(this, 987, createQTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        views.setOnClickPendingIntent(R.id.quickTimerShortcutButton, createQTimerPending);

        Intent createStopwatchIntent = new Intent(this, SoYouADeveloperHuh.class);
        createStopwatchIntent.putExtra("fragmentID","stopwatchCreate");
        PendingIntent createStopwatchPending = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            createStopwatchPending = PendingIntent.getActivity(this, 9874, createStopwatchIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            createStopwatchPending = PendingIntent.getActivity(this, 9874, createStopwatchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        views.setOnClickPendingIntent(R.id.stopwatchShortcutButton, createStopwatchPending);

        Intent createScreensaverIntent = new Intent(this, SoYouADeveloperHuh.class);
        createScreensaverIntent.putExtra("fragmentID","screensaverCreate");
        PendingIntent createScreensaverPending = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            createScreensaverPending = PendingIntent.getActivity(this, 9875, createScreensaverIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            createScreensaverPending = PendingIntent.getActivity(this, 9875, createScreensaverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        views.setOnClickPendingIntent(R.id.screensaverShortcutButton, createScreensaverPending);

        addShortcutData(i);
        appWidgetManager.updateAppWidget(widgetId, views);


        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);
        updateWidget();
        finish();
    }

    private void addShortcutData(int i) {
        ArrayList<ShortcutsData> shortcutsData = tickTrackDatabase.retrieveShortcutWidgetData();
        ShortcutsData shortcutsData1 = new ShortcutsData();
        shortcutsData1.setShortcutWidgetId(widgetId);
        shortcutsData1.setTheme(i);
        shortcutsData.add(shortcutsData1);
        tickTrackDatabase.storeShortcutWidgetData(shortcutsData);
    }

    private void updateWidget(){
        Intent intent = new Intent(this, ShortcutsPanelWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, ShortcutsPanelWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        this.sendBroadcast(intent);
    }
    public Bitmap buildUpdate(String time, Context context) {
        Bitmap myBitmap = Bitmap.createBitmap(700, 84, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(context.getAssets(),"fonts/apercu_regular.otf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(context.getResources().getColor(R.color.Accent) );
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(time, 350, 60, paint);
        return myBitmap;
    }

    private void setupInit() {

        blackCheck.setVisibility(View.INVISIBLE);
        lightCheck.setVisibility(View.INVISIBLE);
        grayCheck.setVisibility(View.INVISIBLE);

    }

    private void setupTheme(int themeMode) {
        if(themeMode==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            grayText.setTextColor(getResources().getColor(R.color.DarkText));
            blackText.setTextColor(getResources().getColor(R.color.DarkText));
            lightText.setTextColor(getResources().getColor(R.color.DarkText));
        } else {
            rootLayout.setBackgroundResource(R.color.Black);
            grayText.setTextColor(getResources().getColor(R.color.LightText));
            blackText.setTextColor(getResources().getColor(R.color.LightText));
            lightText.setTextColor(getResources().getColor(R.color.LightText));
        }
    }
}