package com.theflopguyproductions.ticktrack.widgets.timer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.Locale;

public class QuickTimerWidget extends AppWidgetProvider {

    public static final String ACTION_ONE_MINUTE_QUICK_TIMER = "ACTION_ONE_MINUTE_QUICK_TIMER";
    public static final String ACTION_TWO_MINUTE_QUICK_TIMER = "ACTION_TWO_MINUTE_QUICK_TIMER";
    public static final String ACTION_FIVE_MINUTE_QUICK_TIMER = "ACTION_FIVE_MINUTE_QUICK_TIMER";
    public static final String ACTION_TEN_MINUTE_QUICK_TIMER = "ACTION_TEN_MINUTE_QUICK_TIMER";
    public static final String ACTION_CUSTOM_QUICK_TIMER = "ACTION_CUSTOM_QUICK_TIMER";
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
            views.setOnClickPendingIntent(R.id.quickTimerWidgetDiscardTimerButton, getPendingIntent(ACTION_RESET_QUICK_TIMER, appWidgetIds, appWidgetId, context));

            views.setViewVisibility(R.id.quickTimerWidgetCustomButton, View.VISIBLE);
            views.setViewVisibility(R.id.quickTimerWidgetTenMinuteButton, View.VISIBLE);
            views.setViewVisibility(R.id.quickTimerWidgetFiveMinuteButton, View.VISIBLE);
            views.setViewVisibility(R.id.quickTimerWidgetTwoMinuteButton, View.VISIBLE);
            views.setViewVisibility(R.id.quickTimerWidgetOneMinuteButton, View.VISIBLE);
            views.setViewVisibility(R.id.quickTimerWidgetDiscardTimerButton, View.GONE);
            views.setViewVisibility(R.id.quickTimerWidgetTimerText, View.GONE);
            setupTheme(views, currentTheme, context);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } else if(currentTheme!=-1 && isTimerWidgetSetActive(timerWidgetDataArrayList)) {

            int timerId = getTimerID(appWidgetId, timerWidgetDataArrayList);

            if(timerId!=-1){
                Intent intent = new Intent(context, SoYouADeveloperHuh.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
                views.setOnClickPendingIntent(R.id.quickTimerWidgetRootLayout, pendingIntent);
                views.setOnClickPendingIntent(R.id.quickTimerWidgetOneMinuteButton, getPendingIntent(ACTION_ONE_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
                views.setOnClickPendingIntent(R.id.quickTimerWidgetTwoMinuteButton, getPendingIntent(ACTION_TWO_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
                views.setOnClickPendingIntent(R.id.quickTimerWidgetFiveMinuteButton, getPendingIntent(ACTION_FIVE_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
                views.setOnClickPendingIntent(R.id.quickTimerWidgetTenMinuteButton, getPendingIntent(ACTION_TEN_MINUTE_QUICK_TIMER, appWidgetIds, appWidgetId, context));
                views.setOnClickPendingIntent(R.id.quickTimerWidgetCustomButton, getPendingIntent(ACTION_CUSTOM_QUICK_TIMER, appWidgetIds, appWidgetId, context));
                views.setOnClickPendingIntent(R.id.quickTimerWidgetDiscardTimerButton, getPendingIntent(ACTION_RESET_QUICK_TIMER, appWidgetIds, appWidgetId, context));

                views.setViewVisibility(R.id.quickTimerWidgetCustomButton, View.GONE);
                views.setViewVisibility(R.id.quickTimerWidgetTenMinuteButton, View.GONE);
                views.setViewVisibility(R.id.quickTimerWidgetFiveMinuteButton, View.GONE);
                views.setViewVisibility(R.id.quickTimerWidgetTwoMinuteButton, View.GONE);
                views.setViewVisibility(R.id.quickTimerWidgetOneMinuteButton, View.GONE);
                views.setViewVisibility(R.id.quickTimerWidgetDiscardTimerButton, View.VISIBLE);
                views.setViewVisibility(R.id.quickTimerWidgetTimerText, View.VISIBLE);
                setupTheme(views, currentTheme, context);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    private PendingIntent getPendingIntent(String actionText, int[] appWidgetIds, int appWidgetId, Context context) {

        Intent intent = new Intent(context, getClass());
        intent.setAction(actionText);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.putExtra("timerWidgetId", appWidgetId);
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
    private int getTimerID(int appWidgetId, ArrayList<TimerWidgetData> timerWidgetDataArrayList) {
        for(int i=0; i<timerWidgetDataArrayList.size(); i++){
            if(timerWidgetDataArrayList.get(i).getTimerWidgetId()==appWidgetId){
                return timerWidgetDataArrayList.get(i).getTimerIdInteger();
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
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetIds);
        }
    }


    AppWidgetManager appWidgetManager;
    ComponentName widget;
    Handler timerHandler;
    long timerStartTime, elapsedTime;
    private static final int REFRESH_RATE=100;
    RemoteViews remoteViews;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            final long start = timerStartTime;
            elapsedTime = start - System.currentTimeMillis();
            updateStopwatch(elapsedTime);
            timerHandler.postDelayed(this, REFRESH_RATE);
            appWidgetManager.updateAppWidget(widget, remoteViews);
        }
    };

    private void updateStopwatch(long time) {
        int seconds = (int) time / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;

        remoteViews.setTextViewText(R.id.quickTimerWidgetTimerText, String.format(Locale.getDefault(),"%d : %02d : %02d", hours, minutes, seconds));
    }

    ArrayList<TimerData> timerDataArrayList;
    ArrayList<TimerWidgetData> timerWidgetDataArrayList;

    @Override
    public void onReceive(Context context, Intent intent) {

        appWidgetManager = AppWidgetManager.getInstance(context);
        widget = new ComponentName(context, QuickTimerWidget.class);
        timerHandler = new Handler();

        tickTrackDatabase = new TickTrackDatabase(context);
        quickTimerPreset = new QuickTimerPreset(context);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(context);
        timerWidgetDataArrayList = tickTrackDatabase.retrieveTimerWidgetList();
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.quick_timer_widget);

        System.out.println("RECEIVED SOMETHING");
        Bundle extras = intent.getExtras();
        int appWidgetId = intent.getIntExtra("timerWidgetId", -1);
        String receivedAction = intent.getAction();

        if(appWidgetId!=-1 && extras!=null){
            int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if(ACTION_ONE_MINUTE_QUICK_TIMER.equals(receivedAction)){
                int timerId = quickTimerPreset.setOneMinuteTimer(appWidgetId);
                timerStartTime = getTimerStartTime(timerId);
                if(timerStartTime!=-1){
                    timerHandler.removeCallbacks(timerRunnable);
                    timerHandler.postDelayed(timerRunnable, REFRESH_RATE);
                }

            } else if(ACTION_TWO_MINUTE_QUICK_TIMER.equals(receivedAction)){
                int timerId = quickTimerPreset.setTwoMinuteTimer(appWidgetId);

            } else if(ACTION_FIVE_MINUTE_QUICK_TIMER.equals(receivedAction)){
                int timerId = quickTimerPreset.setFiveMinuteTimer(appWidgetId);

            } else if(ACTION_TEN_MINUTE_QUICK_TIMER.equals(receivedAction)){
                int timerId = quickTimerPreset.setTenMinuteTimer(appWidgetId);

            } else if(ACTION_CUSTOM_QUICK_TIMER.equals(receivedAction)){
                Intent intent1 = new Intent(context, QuickTimerActivity.class);
                intent1.setAction(QuickTimerActivity.ACTION_CREATE_CUSTOM_TIMER);
                intent1.putExtra("appWidgetId", appWidgetId);
                context.startActivity(intent1);

            } else if(ACTION_RESET_QUICK_TIMER.equals(receivedAction)) {
                int timerId = getTimerID(appWidgetId, timerWidgetDataArrayList);
                resetTimerData(timerId);
            }

            if(appWidgetIds!=null){
                System.out.println("ON UPDATE HAPPENED");
                onUpdate(context, appWidgetManager, appWidgetIds);
            }

        } else {
            super.onReceive(context, intent);
        }

    }

    private void resetTimerData(int timerId) {
        for(int i=0; i<timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).getTimerIntID()==timerId){
                timerDataArrayList.get(i).setTimerOn(false);
            }
        }
        tickTrackDatabase.storeTimerList(timerDataArrayList);
    }

    private long getTimerStartTime(int timerId) {
        for(int i=0; i<timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).getTimerIntID()==timerId){
                return timerDataArrayList.get(i).getTimerStartTimeInMillis();
            }
        }
        return -1;
    }
}

