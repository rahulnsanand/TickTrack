package com.theflopguyproductions.ticktrack.widgets.clock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;

public class TickTrackClockWidget extends AppWidgetProvider {

    private ArrayList<ClockData> clockDataArrayList;
    private TickTrackDatabase tickTrackDatabase;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        tickTrackDatabase = new TickTrackDatabase(context);
        Intent intent = new Intent(context, ClockWidgetConfigActivity.class);
        intent.putExtra("clockId", appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
        RemoteViews views = setupWidgetTheme(getClockTheme(appWidgetId), context);
        views.setOnClickPendingIntent(R.id.clockWidgetRootLayout, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    private RemoteViews setupWidgetTheme(int clockTheme, Context context) {
        if(clockTheme==1){
            return new RemoteViews(context.getPackageName(), R.layout.tick_track_clock_widget1);
        } else if (clockTheme==2){
            return new RemoteViews(context.getPackageName(), R.layout.tick_track_clock_widget2);
        } else if (clockTheme==3){
            return new RemoteViews(context.getPackageName(), R.layout.tick_track_clock_widget3);
        } else if (clockTheme==4){
            return new RemoteViews(context.getPackageName(), R.layout.tick_track_clock_widget4);
        } else if (clockTheme==5){
            return new RemoteViews(context.getPackageName(), R.layout.tick_track_clock_widget5);
        } else if (clockTheme==6){
            return new RemoteViews(context.getPackageName(), R.layout.tick_track_clock_widget6);
        } else {
            return new RemoteViews(context.getPackageName(), R.layout.tick_track_clock_widget1);
        }
    }
    private int getClockTheme(int clockWidgetId){
        clockDataArrayList = tickTrackDatabase.retrieveClockWidgetList();
        for(int i=0; i<clockDataArrayList.size(); i++){
            if(clockDataArrayList.get(i).getClockWidgetId()==clockWidgetId){
                return clockDataArrayList.get(i).getClockTheme();
            }
        }
        return -1;
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        tickTrackDatabase = new TickTrackDatabase(context);
        tickTrackDatabase.storeClockWidgetList(new ArrayList<>());
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        tickTrackDatabase = new TickTrackDatabase(context);
        clockDataArrayList = tickTrackDatabase.retrieveClockWidgetList();
        for(int i=0; i<clockDataArrayList.size(); i++){
            for (int appWidgetId : appWidgetIds) {
                if (appWidgetId == clockDataArrayList.get(i).getClockWidgetId()) {
                    clockDataArrayList.remove(i);
                }
            }
        }
        tickTrackDatabase.storeClockWidgetList(clockDataArrayList);
    }
}

