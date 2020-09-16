package com.theflopguyproductions.ticktrack.widgets.shortcuts.panel;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.widgets.shortcuts.panel.data.ShortcutsData;

import java.util.ArrayList;

public class ShortcutsPanelWidget extends AppWidgetProvider {


    private static final String ACTION_CREATE_COUNTER = "ACTION_CREATE_COUNTER";
    private static final String ACTION_CREATE_TIMER = "ACTION_CREATE_TIMER";
    private static final String ACTION_CREATE_QUICK_TIMER = "ACTION_CREATE_QUICK_TIMER";
    private static final String ACTION_CREATE_STOPWATCH = "ACTION_CREATE_STOPWATCH";
    private static final String ACTION_CREATE_SCREENSAVER = "ACTION_CREATE_SCREENSAVER";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);
        ArrayList<ShortcutsData> shortcutsData = tickTrackDatabase.retrieveShortcutWidgetData();

        if(shortcutsData.size()>0){
            int theme = getShortcutTheme(shortcutsData, appWidgetId);
            if(theme!=-1){
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcuts_panel_widget);
                setupTheme(theme, views);

                Intent createCounterIntent = new Intent(context, SoYouADeveloperHuh.class);
                createCounterIntent.putExtra("fragmentID","counterCreate");
                createCounterIntent.setAction(ACTION_CREATE_COUNTER);
                PendingIntent createCounterPending = PendingIntent.getActivity(context, 98, createCounterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.counterShortcutButton, createCounterPending);

                Intent createTimerIntent = new Intent(context, SoYouADeveloperHuh.class);
                createTimerIntent.putExtra("fragmentID","timerCreate");
                createTimerIntent.setAction(ACTION_CREATE_TIMER);
                PendingIntent createTimerPending = PendingIntent.getActivity(context, 987, createTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.timerShortcutButton, createTimerPending);

                Intent createQTimerIntent = new Intent(context, SoYouADeveloperHuh.class);
                createQTimerIntent.putExtra("fragmentID","quickTimerCreate");
                createTimerIntent.setAction(ACTION_CREATE_QUICK_TIMER);
                PendingIntent createQTimerPending = PendingIntent.getActivity(context, 987, createQTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.quickTimerShortcutButton, createQTimerPending);

                Intent createStopwatchIntent = new Intent(context, SoYouADeveloperHuh.class);
                createStopwatchIntent.putExtra("fragmentID","stopwatchCreate");
                createTimerIntent.setAction(ACTION_CREATE_STOPWATCH);
                PendingIntent createStopwatchPending = PendingIntent.getActivity(context, 9874, createStopwatchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.stopwatchShortcutButton, createStopwatchPending);

                Intent createScreensaverIntent = new Intent(context, SoYouADeveloperHuh.class);
                createScreensaverIntent.putExtra("fragmentID","screensaverCreate");
                createTimerIntent.setAction(ACTION_CREATE_SCREENSAVER);
                PendingIntent createScreensaverPending = PendingIntent.getActivity(context, 9875, createScreensaverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.screensaverShortcutButton, createScreensaverPending);


                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    private static void setupTheme(int theme, RemoteViews views) {
        if(theme==1){
            views.setInt(R.id.shortcutWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_black);
            views.setInt(R.id.counterShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.timerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.quickTimerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.stopwatchShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
            views.setInt(R.id.screensaverShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_dark_background);
        } else if(theme==2){
            views.setInt(R.id.shortcutWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_dark);
            views.setInt(R.id.counterShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.timerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.quickTimerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.stopwatchShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
            views.setInt(R.id.screensaverShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_gray_background);
        } else if(theme==3){
            views.setInt(R.id.shortcutWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_light);
            views.setInt(R.id.counterShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.timerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.stopwatchShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.screensaverShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
        } else {
            views.setInt(R.id.shortcutWidgetRootLayout, "setBackgroundResource", R.drawable.round_rect_light);
            views.setInt(R.id.counterShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.timerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.quickTimerShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.stopwatchShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
            views.setInt(R.id.screensaverShortcutButton, "setBackgroundResource", R.drawable.clickable_layout_light_background);
        }
    }

    private static int getShortcutTheme(ArrayList<ShortcutsData> shortcutsData, int appWidgetId) {
        for(int i=0; i<shortcutsData.size(); i++){
            if(shortcutsData.get(i).getShortcutWidgetId()==appWidgetId){
                return shortcutsData.get(i).getTheme();
            }
        }
        return -1;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);
        tickTrackDatabase.storeShortcutWidgetData(new ArrayList<>());
    }
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);
        ArrayList<ShortcutsData> shortcutsData = tickTrackDatabase.retrieveShortcutWidgetData();
        for(int i=0; i<shortcutsData.size(); i++){
            for (int appWidgetId : appWidgetIds) {
                if (appWidgetId == shortcutsData.get(i).getShortcutWidgetId()) {
                    shortcutsData.remove(i);
                }
            }
        }
        tickTrackDatabase.storeShortcutWidgetData(shortcutsData);
    }
    
}

