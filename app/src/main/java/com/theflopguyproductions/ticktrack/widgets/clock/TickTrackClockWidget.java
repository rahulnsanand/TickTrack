package com.theflopguyproductions.ticktrack.widgets.clock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
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
        System.out.println(appWidgetId+"+++++++++++++++(((((((((()))))))))))))+++++++++++++++++"+getClockTheme(appWidgetId));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
        //TODO USE MULTIPLE LAYOUTS TO REDUCE LAG. CHECK!
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tick_track_clock_widget);
        views.setOnClickPendingIntent(R.id.clockWidgetRootLayout, pendingIntent);
        setupWidgetTheme(views, getClockTheme(appWidgetId));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    private void setupWidgetTheme(RemoteViews views, int clockTheme) {
        if(clockTheme==1){
            views.setViewVisibility(R.id.unordinaryClock, View.VISIBLE);
            views.setViewVisibility(R.id.oxygenyClock, View.GONE);
            views.setViewVisibility(R.id.minimalClock, View.GONE);
            views.setViewVisibility(R.id.simplisticClock, View.GONE);
            views.setViewVisibility(R.id.romanClock, View.GONE);
            views.setViewVisibility(R.id.funkyClock, View.GONE);
        } else if (clockTheme==2){
            views.setViewVisibility(R.id.unordinaryClock, View.GONE);
            views.setViewVisibility(R.id.oxygenyClock, View.VISIBLE);
            views.setViewVisibility(R.id.minimalClock, View.GONE);
            views.setViewVisibility(R.id.simplisticClock, View.GONE);
            views.setViewVisibility(R.id.romanClock, View.GONE);
            views.setViewVisibility(R.id.funkyClock, View.GONE);
        } else if (clockTheme==3){
            views.setViewVisibility(R.id.unordinaryClock, View.GONE);
            views.setViewVisibility(R.id.oxygenyClock, View.GONE);
            views.setViewVisibility(R.id.minimalClock, View.VISIBLE);
            views.setViewVisibility(R.id.simplisticClock, View.GONE);
            views.setViewVisibility(R.id.romanClock, View.GONE);
            views.setViewVisibility(R.id.funkyClock, View.GONE);
        } else if (clockTheme==4){
            views.setViewVisibility(R.id.unordinaryClock, View.GONE);
            views.setViewVisibility(R.id.oxygenyClock, View.GONE);
            views.setViewVisibility(R.id.minimalClock, View.GONE);
            views.setViewVisibility(R.id.simplisticClock, View.VISIBLE);
            views.setViewVisibility(R.id.romanClock, View.GONE);
            views.setViewVisibility(R.id.funkyClock, View.GONE);
        } else if (clockTheme==5){
            views.setViewVisibility(R.id.unordinaryClock, View.GONE);
            views.setViewVisibility(R.id.oxygenyClock, View.GONE);
            views.setViewVisibility(R.id.minimalClock, View.GONE);
            views.setViewVisibility(R.id.simplisticClock, View.GONE);
            views.setViewVisibility(R.id.romanClock, View.VISIBLE);
            views.setViewVisibility(R.id.funkyClock, View.GONE);
        } else if (clockTheme==6){
            views.setViewVisibility(R.id.unordinaryClock, View.GONE);
            views.setViewVisibility(R.id.oxygenyClock, View.GONE);
            views.setViewVisibility(R.id.minimalClock, View.GONE);
            views.setViewVisibility(R.id.simplisticClock, View.GONE);
            views.setViewVisibility(R.id.romanClock, View.GONE);
            views.setViewVisibility(R.id.funkyClock, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.unordinaryClock, View.VISIBLE);
            views.setViewVisibility(R.id.oxygenyClock, View.GONE);
            views.setViewVisibility(R.id.minimalClock, View.GONE);
            views.setViewVisibility(R.id.simplisticClock, View.GONE);
            views.setViewVisibility(R.id.romanClock, View.GONE);
            views.setViewVisibility(R.id.funkyClock, View.GONE);
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

