package com.theflopguyproductions.ticktrack.widgets.shortcuts;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class StopwatchWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stopwatch_widget);
        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);

        int theme = tickTrackDatabase.getThemeMode();

        if(theme==2){
            views.setImageViewResource(R.id.appwidget_text4,  R.drawable.ic_shortcut_stopwatch_dark);

        } else {
            views.setImageViewResource(R.id.appwidget_text4,  R.drawable.ic_shortcut_stopwatchicon);

        }

        Intent createTimerIntent = new Intent(context, SoYouADeveloperHuh.class);
        createTimerIntent.putExtra("fragmentID","stopwatchCreate");
        createTimerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            views.setOnClickPendingIntent(R.id.createStopwatchShortcutWidgetRootLayout, PendingIntent.getActivity(context, 77, createTimerIntent, PendingIntent.FLAG_MUTABLE));
        } else {
            views.setOnClickPendingIntent(R.id.createStopwatchShortcutWidgetRootLayout, PendingIntent.getActivity(context, 77, createTimerIntent, 0));
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
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
        // Enter relevant functionality for when the last widget is disabled
    }
}

