package com.theflopguyproductions.ticktrack.widgets.shortcuts.panel;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
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


    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int[] appWidgetIds) {
        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);
        ArrayList<ShortcutsData> shortcutsData = tickTrackDatabase.retrieveShortcutWidgetData();

        if(shortcutsData.size()>0){
            int theme = getShortcutTheme(shortcutsData, appWidgetId);
            if(theme!=-1){
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcuts_panel_widget);
                setupTheme(theme, views);

                views.setImageViewBitmap(R.id.ticktrackShortcutPanelWidgetTitle, buildUpdate("TickTrack Shortcut Console", context));

                views.setOnClickPendingIntent(R.id.counterShortcutButton,
                        getPendingSelfIntent(context, ACTION_CREATE_COUNTER, 9, "counterCreate", appWidgetIds ));


                views.setOnClickPendingIntent(R.id.timerShortcutButton,
                        getPendingSelfIntent(context, ACTION_CREATE_TIMER, 99, "timerCreate", appWidgetIds ));


                views.setOnClickPendingIntent(R.id.quickTimerShortcutButton,
                        getPendingSelfIntent(context, ACTION_CREATE_QUICK_TIMER, 999, "quickTimerCreate", appWidgetIds ));


                views.setOnClickPendingIntent(R.id.stopwatchShortcutButton,
                        getPendingSelfIntent(context, ACTION_CREATE_STOPWATCH, 9999, "stopwatchCreate", appWidgetIds ));


                views.setOnClickPendingIntent(R.id.screensaverShortcutButton,
                        getPendingSelfIntent(context, ACTION_CREATE_SCREENSAVER, 99999, "screensaverCreate", appWidgetIds ));


                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        } else {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcuts_panel_widget);
            if(tickTrackDatabase.getThemeMode()==1){
                setupTheme(3, views);
            } else {
                setupTheme(1, views);
            }

            views.setImageViewBitmap(R.id.ticktrackShortcutPanelWidgetTitle, buildUpdate("TickTrack Shortcut Console", context));

            views.setOnClickPendingIntent(R.id.counterShortcutButton,
                    getPendingSelfIntent(context, ACTION_CREATE_COUNTER, 9, "counterCreate", appWidgetIds ));


            views.setOnClickPendingIntent(R.id.timerShortcutButton,
                    getPendingSelfIntent(context, ACTION_CREATE_TIMER, 99, "timerCreate", appWidgetIds ));


            views.setOnClickPendingIntent(R.id.quickTimerShortcutButton,
                    getPendingSelfIntent(context, ACTION_CREATE_QUICK_TIMER, 999, "quickTimerCreate", appWidgetIds ));


            views.setOnClickPendingIntent(R.id.stopwatchShortcutButton,
                    getPendingSelfIntent(context, ACTION_CREATE_STOPWATCH, 9999, "stopwatchCreate", appWidgetIds ));


            views.setOnClickPendingIntent(R.id.screensaverShortcutButton,
                    getPendingSelfIntent(context, ACTION_CREATE_SCREENSAVER, 99999, "screensaverCreate", appWidgetIds ));


            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
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

    private PendingIntent getPendingSelfIntent(Context context, String action, int counterID, String counterIdString, int[] appWidgetIds) {

        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getBroadcast(context, counterID, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            return PendingIntent.getBroadcast(context, counterID, intent, 0);
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
            updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetIds);
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

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        System.out.println("RECEIVED SOEMTHING CREATE BROADCAST");
        if(ACTION_CREATE_COUNTER.equals(intent.getAction())){
            Intent createCounterIntent = new Intent(context, SoYouADeveloperHuh.class);
            createCounterIntent.putExtra("fragmentID","counterCreate");
            createCounterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(createCounterIntent);
            System.out.println("RECEIVED COUNTER CREATE BROADCAST");
        } else if(ACTION_CREATE_TIMER.equals(intent.getAction())){
            Intent createTimerIntent = new Intent(context, SoYouADeveloperHuh.class);
            createTimerIntent.putExtra("fragmentID","timerCreate");
            createTimerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(createTimerIntent);
        } else if(ACTION_CREATE_QUICK_TIMER.equals(intent.getAction())){
            Intent createQTimerIntent = new Intent(context, SoYouADeveloperHuh.class);
            createQTimerIntent.putExtra("fragmentID","quickTimerCreate");
            createQTimerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(createQTimerIntent);
        } else if(ACTION_CREATE_STOPWATCH.equals(intent.getAction())){
            Intent createStopwatchIntent = new Intent(context, SoYouADeveloperHuh.class);
            createStopwatchIntent.putExtra("fragmentID","stopwatchCreate");
            createStopwatchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(createStopwatchIntent);
        } else if(ACTION_CREATE_SCREENSAVER.equals(intent.getAction())){
            Intent createScreensaverIntent = new Intent(context, SoYouADeveloperHuh.class);
            createScreensaverIntent.putExtra("fragmentID","screensaverCreate");
            createScreensaverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(createScreensaverIntent);
        }
    }
}

