package com.theflopguyproductions.ticktrack.widgets.clock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.utils.AnalogClock;

public class TickTrackClockWidget extends AppWidgetProvider {

    private RemoteViews views;
    private AnalogClock analogClock;
    private AppWidgetManager widgetManager;
    private Context context;
    private int[] appWidgetIds;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int[] appWidgetIds) {

        widgetManager = AppWidgetManager.getInstance(context);
        views = new RemoteViews(context.getPackageName(), R.layout.tick_track_clock_widget);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetIds);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

