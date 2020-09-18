package com.theflopguyproductions.ticktrack.widgets.shortcuts;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;


public class CreateTimerWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.create_timer_widget);
        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);

        int theme = tickTrackDatabase.getThemeMode();

        if(theme==1){
            views.setImageViewResource(R.id.appwidget_text,  R.drawable.ic_shortcut_timer_dark);
        } else {
            views.setImageViewResource(R.id.appwidget_text,  R.drawable.ic_shortcut_timericon);
        }

        Intent createTimerIntent = new Intent(context, SoYouADeveloperHuh.class);
        createTimerIntent.putExtra("fragmentID","timerCreate");
        createTimerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        views.setOnClickPendingIntent(R.id.createTimerShortcutWidgetRootLayout, PendingIntent.getActivity(context, 74, createTimerIntent, 0));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

