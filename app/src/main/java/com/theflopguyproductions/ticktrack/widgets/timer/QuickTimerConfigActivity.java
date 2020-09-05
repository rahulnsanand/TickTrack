package com.theflopguyproductions.ticktrack.widgets.timer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import androidx.appcompat.app.AppCompatActivity;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.widgets.counter.CounterWidget;
import com.theflopguyproductions.ticktrack.widgets.timer.data.TimerWidgetData;

import java.util.ArrayList;

import static com.theflopguyproductions.ticktrack.widgets.timer.QuickTimerWidget.ACTION_CUSTOM_QUICK_TIMER;
import static com.theflopguyproductions.ticktrack.widgets.timer.QuickTimerWidget.ACTION_FIVE_MINUTE_QUICK_TIMER;
import static com.theflopguyproductions.ticktrack.widgets.timer.QuickTimerWidget.ACTION_ONE_MINUTE_QUICK_TIMER;
import static com.theflopguyproductions.ticktrack.widgets.timer.QuickTimerWidget.ACTION_RESET_QUICK_TIMER;
import static com.theflopguyproductions.ticktrack.widgets.timer.QuickTimerWidget.ACTION_TEN_MINUTE_QUICK_TIMER;
import static com.theflopguyproductions.ticktrack.widgets.timer.QuickTimerWidget.ACTION_TWO_MINUTE_QUICK_TIMER;

public class QuickTimerConfigActivity extends AppCompatActivity {

    private Button grayButton, blackButton, lightButton;
    private static int timerWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private void initVariables() {
        blackButton = findViewById(R.id.button3);
        grayButton = findViewById(R.id.button4);
        lightButton = findViewById(R.id.button5);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_timer_config);

        initVariables();

        Intent counterIntent = getIntent();
        Bundle extras = counterIntent.getExtras();
        if(extras!=null){
            timerWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, timerWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        if(timerWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }

        grayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmSelection(2);
            }
        });
    }

    private void confirmSelection(int themeSelection){

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.quick_timer_widget);

        ArrayList<TimerWidgetData> timerWidgetDataArrayList = new TickTrackDatabase(this).retrieveTimerWidgetList();

        TimerWidgetData timerWidgetData = new TimerWidgetData();
        timerWidgetData.setTimerIdInteger(-1);
        timerWidgetData.setTimerIdString(null);
        timerWidgetData.setTimerWidgetId(timerWidgetId);
        timerWidgetData.setWidgetTheme(themeSelection);

        timerWidgetDataArrayList.add(timerWidgetData);
        new TickTrackDatabase(this).storeTimerWidgetList(timerWidgetDataArrayList);

        Intent intent = new Intent(this, SoYouADeveloperHuh.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, timerWidgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.quickTimerWidgetRootLayout, pendingIntent);
        views.setOnClickPendingIntent(R.id.quickTimerWidgetOneMinuteButton, getPendingIntent(ACTION_ONE_MINUTE_QUICK_TIMER));
        views.setOnClickPendingIntent(R.id.quickTimerWidgetTwoMinuteButton, getPendingIntent(ACTION_TWO_MINUTE_QUICK_TIMER));
        views.setOnClickPendingIntent(R.id.quickTimerWidgetFiveMinuteButton, getPendingIntent(ACTION_FIVE_MINUTE_QUICK_TIMER));
        views.setOnClickPendingIntent(R.id.quickTimerWidgetTenMinuteButton, getPendingIntent(ACTION_TEN_MINUTE_QUICK_TIMER));
        views.setOnClickPendingIntent(R.id.quickTimerWidgetCustomButton, getPendingIntent(ACTION_CUSTOM_QUICK_TIMER));
        views.setOnClickPendingIntent(R.id.quickTimerWidgetDiscardTimerButton, getPendingIntent(ACTION_RESET_QUICK_TIMER));
        views.setViewVisibility(R.id.quickTimerWidgetCustomButton, View.VISIBLE);
        views.setViewVisibility(R.id.quickTimerWidgetTenMinuteButton, View.VISIBLE);
        views.setViewVisibility(R.id.quickTimerWidgetFiveMinuteButton, View.VISIBLE);
        views.setViewVisibility(R.id.quickTimerWidgetTwoMinuteButton, View.VISIBLE);
        views.setViewVisibility(R.id.quickTimerWidgetOneMinuteButton, View.VISIBLE);
        views.setViewVisibility(R.id.quickTimerWidgetDiscardTimerButton, View.GONE);
        views.setViewVisibility(R.id.quickTimerWidgetTimerText, View.GONE);
        setupTheme(views, themeSelection);
        appWidgetManager.updateAppWidget(timerWidgetId, views);


        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, timerWidgetId);
        setResult(RESULT_OK, resultValue);
        updateWidget();
        finish();
    }
    private PendingIntent getPendingIntent(String actionText) {

        Intent intent = new Intent(this, getClass());
        intent.setAction(actionText);
        intent.putExtra("timerWidgetId", timerWidgetId);
        return PendingIntent.getBroadcast(this, timerWidgetId, intent, 0);

    }
    private void updateWidget(){
        Intent intent = new Intent(this, CounterWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, CounterWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
    private void setupTheme(RemoteViews views, int currentTheme) {
        if( currentTheme == 1){
            views.setInt(R.id.quickTimerWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_light);
            views.setInt(R.id.quickTimerWidgetOneMinuteButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerWidgetTwoMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerWidgetFiveMinuteButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerWidgetTenMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerWidgetCustomButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);

            views.setTextColor(R.id.quickTimerWidgetTimerText,   getResources().getColor(R.color.DarkText));
        } else if(currentTheme == 2){
            views.setInt(R.id.quickTimerWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_dark);
            views.setInt(R.id.quickTimerWidgetOneMinuteButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.quickTimerWidgetTwoMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.quickTimerWidgetFiveMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.quickTimerWidgetTenMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.quickTimerWidgetCustomButton, "setBackgroundResource",R.drawable.clickable_layout_gray_background);

            views.setTextColor(R.id.quickTimerWidgetTimerText,   getResources().getColor(R.color.LightText));
        } else if(currentTheme == 3){
            views.setInt(R.id.quickTimerWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_black);
            views.setInt(R.id.quickTimerWidgetOneMinuteButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.quickTimerWidgetTwoMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.quickTimerWidgetFiveMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.quickTimerWidgetTenMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.quickTimerWidgetCustomButton, "setBackgroundResource",R.drawable.clickable_layout_dark_background);

            views.setTextColor(R.id.quickTimerWidgetTimerText,   getResources().getColor(R.color.LightText));
        }
    }
}