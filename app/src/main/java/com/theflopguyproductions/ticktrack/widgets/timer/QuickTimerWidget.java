package com.theflopguyproductions.ticktrack.widgets.timer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;
import com.theflopguyproductions.ticktrack.widgets.timer.data.QuickTimerPreset;
import com.theflopguyproductions.ticktrack.widgets.timer.data.TimerWidgetData;

import java.util.ArrayList;

public class QuickTimerWidget extends AppWidgetProvider {

    public static final String ACTION_ONE_MINUTE_QUICK_TIMER = "ACTION_ONE_MINUTE_QUICK_TIMER";
    public static final String ACTION_TWO_MINUTE_QUICK_TIMER = "ACTION_TWO_MINUTE_QUICK_TIMER";
    public static final String ACTION_FIVE_MINUTE_QUICK_TIMER = "ACTION_FIVE_MINUTE_QUICK_TIMER";
    public static final String ACTION_TEN_MINUTE_QUICK_TIMER = "ACTION_TEN_MINUTE_QUICK_TIMER";
    public static final String ACTION_CUSTOM_QUICK_TIMER = "ACTION_CUSTOM_QUICK_TIMER";
    public static final String ACTION_PLAY_QUICK_TIMER = "ACTION_PLAY_QUICK_TIMER";
    public static final String ACTION_PAUSE_QUICK_TIMER = "ACTION_PAUSE_QUICK_TIMER";
    public static final String ACTION_RESET_QUICK_TIMER = "ACTION_RESET_QUICK_TIMER";
    public static final String ACTION_STOP_QUICK_TIMER = "ACTION_STOP_QUICK_TIMER";

    private TickTrackDatabase tickTrackDatabase;
    private QuickTimerPreset quickTimerPreset;
    private TickTrackTimerDatabase tickTrackTimerDatabase;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId, int[] appWidgetIds) {

        tickTrackDatabase = new TickTrackDatabase(context);
        quickTimerPreset = new QuickTimerPreset(context);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(context);

        ArrayList<TimerWidgetData> timerWidgetDataArrayList = tickTrackDatabase.retrieveTimerWidgetList();
        ArrayList<TimerData> timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        int currentTheme = getTimerWidgetTheme(appWidgetId, timerWidgetDataArrayList);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quick_timer_widget);

        if(currentTheme!=-1 && !isTimerWidgetSetActive(timerWidgetDataArrayList)){
            Intent intent = new Intent(context, SoYouADeveloperHuh.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
            views.setOnClickPendingIntent(R.id.quickTimerWidgetRootLayout, pendingIntent);
            views.setOnClickPendingIntent(R.id.quickTimerWidgetOneMinuteButton, getPendingIntent(ACTION_ONE_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
            views.setOnClickPendingIntent(R.id.quickTimerWidgetTwoMinuteButton, getPendingIntent(ACTION_TWO_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
            views.setOnClickPendingIntent(R.id.quickTimerWidgetFiveMinuteButton, getPendingIntent(ACTION_FIVE_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
            views.setOnClickPendingIntent(R.id.quickTimerWidgetTenMinuteButton, getPendingIntent(ACTION_TEN_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
            views.setOnClickPendingIntent(R.id.quickTimerWidgetCustomButton, getPendingIntent(ACTION_CUSTOM_QUICK_TIMER, appWidgetIds, appWidgetId, context));

            views.setViewVisibility(R.id.quickTimerWidgetCustomButton, View.VISIBLE);
            views.setViewVisibility(R.id.quickTimerWidgetTenMinuteButton, View.VISIBLE);
            views.setViewVisibility(R.id.quickTimerWidgetFiveMinuteButton, View.VISIBLE);
            views.setViewVisibility(R.id.quickTimerWidgetTwoMinuteButton, View.VISIBLE);
            views.setViewVisibility(R.id.quickTimerWidgetOneMinuteButton, View.VISIBLE);
            views.setViewVisibility(R.id.quickTimerWidgetTimerText, View.GONE);
            setupTheme(views, currentTheme, context);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } else if(currentTheme!=-1 && isTimerWidgetSetActive(timerWidgetDataArrayList)) {
            Intent intent = new Intent(context, SoYouADeveloperHuh.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
            views.setOnClickPendingIntent(R.id.quickTimerWidgetRootLayout, pendingIntent);
            views.setOnClickPendingIntent(R.id.quickTimerWidgetOneMinuteButton, getPendingIntent(ACTION_ONE_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
            views.setOnClickPendingIntent(R.id.quickTimerWidgetTwoMinuteButton, getPendingIntent(ACTION_TWO_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
            views.setOnClickPendingIntent(R.id.quickTimerWidgetFiveMinuteButton, getPendingIntent(ACTION_FIVE_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
            views.setOnClickPendingIntent(R.id.quickTimerWidgetTenMinuteButton, getPendingIntent(ACTION_TEN_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
            views.setOnClickPendingIntent(R.id.quickTimerWidgetCustomButton, getPendingIntent(ACTION_CUSTOM_QUICK_TIMER, appWidgetIds, appWidgetId, context));

            views.setViewVisibility(R.id.quickTimerWidgetCustomButton, View.GONE);
            views.setViewVisibility(R.id.quickTimerWidgetTenMinuteButton, View.GONE);
            views.setViewVisibility(R.id.quickTimerWidgetFiveMinuteButton, View.GONE);
            views.setViewVisibility(R.id.quickTimerWidgetTwoMinuteButton, View.GONE);
            views.setViewVisibility(R.id.quickTimerWidgetOneMinuteButton, View.GONE);
            views.setViewVisibility(R.id.quickTimerWidgetTimerText, View.VISIBLE);
            setupTheme(views, currentTheme, context);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    private PendingIntent getPendingIntent(String actionOneMinuteQuickTimer, int[] appWidgetIds, int appWidgetId, Context context) {

        Intent intent = new Intent(context, getClass());
        intent.setAction(actionOneMinuteQuickTimer);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, 0);

    }

    private boolean isTimerWidgetSetActive(ArrayList<TimerWidgetData> timerWidgetDataArrayList) {
        for(int i=0; i<timerWidgetDataArrayList.size(); i++){
            if(timerWidgetDataArrayList.get(i).getTimerIdInteger()!=-1){
                return true;
            }
        }
        return false;
    }
    private int getTimerWidgetTheme(int appWidgetId, ArrayList<TimerWidgetData> timerWidgetDataArrayList) {
        for(int i=0; i<timerWidgetDataArrayList.size(); i++){
            if(timerWidgetDataArrayList.get(i).getTimerWidgetId()==appWidgetId){
                return timerWidgetDataArrayList.get(i).getWidgetTheme();
            }
        }
        return -1;
    }
    private static void setupTheme(RemoteViews views, int currentTheme, Context context) {
        if( currentTheme == 1){
            views.setInt(R.id.quickTimerWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_light);
            views.setInt(R.id.quickTimerWidgetOneMinuteButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerWidgetTwoMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerWidgetFiveMinuteButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerWidgetTenMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerWidgetCustomButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);

            views.setTextColor(R.id.quickTimerWidgetTimerText,   context.getResources().getColor(R.color.DarkText));
        } else if(currentTheme == 2){
            views.setInt(R.id.quickTimerWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_dark);
            views.setInt(R.id.quickTimerWidgetOneMinuteButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.quickTimerWidgetTwoMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.quickTimerWidgetFiveMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.quickTimerWidgetTenMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.quickTimerWidgetCustomButton, "setBackgroundResource",R.drawable.clickable_layout_gray_background);

            views.setTextColor(R.id.quickTimerWidgetTimerText,   context.getResources().getColor(R.color.LightText));
        } else if(currentTheme == 3){
            views.setInt(R.id.quickTimerWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_black);
            views.setInt(R.id.quickTimerWidgetOneMinuteButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.quickTimerWidgetTwoMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.quickTimerWidgetFiveMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.quickTimerWidgetTenMinuteButton, "setBackgroundResource",R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.quickTimerWidgetCustomButton, "setBackgroundResource",R.drawable.clickable_layout_dark_background);

            views.setTextColor(R.id.quickTimerWidgetTimerText,   context.getResources().getColor(R.color.LightText));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetIds);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        tickTrackDatabase = new TickTrackDatabase(context);
        quickTimerPreset = new QuickTimerPreset(context);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(context);

        System.out.println("RECEIVED SOMETHING");

        Bundle extras = intent.getExtras();
        String counterID = intent.getStringExtra("counterID");
        String receivedAction = intent.getAction();


        super.onReceive(context, intent);
    }
}

