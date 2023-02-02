package com.theflopguyproductions.ticktrack.widgets.counter;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.activity.CounterActivity;
import com.theflopguyproductions.ticktrack.dialogs.CreateCounter;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.helpers.UniqueIdGenerator;
import com.theflopguyproductions.ticktrack.widgets.counter.data.CounterWidgetData;
import com.theflopguyproductions.ticktrack.widgets.counter.data.WidgetCounterAdapter;

import java.util.ArrayList;
import java.util.Collections;

import static com.theflopguyproductions.ticktrack.widgets.counter.CounterWidget.ACTION_WIDGET_CLICK_MINUS;
import static com.theflopguyproductions.ticktrack.widgets.counter.CounterWidget.ACTION_WIDGET_CLICK_PLUS;


public class CounterWidgetConfigActivity extends AppCompatActivity {

    private static ArrayList<CounterData> counterDataArrayList = new ArrayList<>();
    private static WidgetCounterAdapter counterAdapter;
    private Button cancelButton;
    private static Activity activity;
    private static TextView noCounterText, blackThemeText, grayThemeText, lightThemeText;
    private SharedPreferences sharedPreferences;
    private static TickTrackDatabase tickTrackDatabase;
    private ConstraintLayout counterFragmentRootLayout;
    private FloatingActionButton counterFab;
    private static RecyclerView counterRecyclerView;

    private ImageButton blackTheme, grayTheme, lightTheme;
    private static int currentTheme = 3;
    private ImageView blackTick, grayTick, lightTick;

    private static int counterWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        if (s.equals("CounterData")){
            Collections.sort(counterDataArrayList);
            counterAdapter.diffUtilsChangeData(counterDataArrayList);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        TickTrackAnimator.fabUnDissolve(counterFab);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        TickTrackThemeSetter.counterWidgetActivityTheme(this, counterRecyclerView, counterFragmentRootLayout,
                noCounterText, tickTrackDatabase, blackThemeText, grayThemeText, lightThemeText, cancelButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_widget_config);

        activity = this;
        tickTrackDatabase = new TickTrackDatabase(activity);
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        counterRecyclerView = findViewById(R.id.counterWidgetActivityRecyclerView);
        noCounterText = findViewById(R.id.counterWidgetActivityNoCounterText);
        counterFragmentRootLayout = findViewById(R.id.counterWidgetActivityRootLayout);
        counterFab = findViewById(R.id.counterWidgetActivityFAB);
        cancelButton = findViewById(R.id.counterWidgetActivityCancelButton);
        blackTheme = findViewById(R.id.counterWidgetActivityBlackThemeButton);
        grayTheme = findViewById(R.id.counterWidgetActivityGrayThemeButton);
        lightTheme = findViewById(R.id.counterWidgetActivityLightThemeButton);
        blackTick = findViewById(R.id.counterWidgetActivityBlackThemeTick);
        grayTick = findViewById(R.id.counterWidgetActivityGrayThemeTick);
        lightTick = findViewById(R.id.counterWidgetActivityLightThemeTick);
        blackThemeText = findViewById(R.id.counterWidgetActivityBlackThemeText);
        grayThemeText = findViewById(R.id.counterWidgetActivityGrayThemeText);
        lightThemeText = findViewById(R.id.counterWidgetActivityLightThemeText);

        grayTick.setVisibility(View.VISIBLE);
        blackTick.setVisibility(View.GONE);
        lightTick.setVisibility(View.GONE);
        currentTheme = 2;

        lightTheme.setOnClickListener(view -> {
            currentTheme = 1;
            grayTick.setVisibility(View.GONE);
            blackTick.setVisibility(View.GONE);
            lightTick.setVisibility(View.VISIBLE);
        });
        blackTheme.setOnClickListener(view -> {
            currentTheme = 3;
            grayTick.setVisibility(View.GONE);
            blackTick.setVisibility(View.VISIBLE);
            lightTick.setVisibility(View.GONE);
        });
        grayTheme.setOnClickListener(view -> {
            currentTheme = 2;
            grayTick.setVisibility(View.VISIBLE);
            blackTick.setVisibility(View.GONE);
            lightTick.setVisibility(View.GONE);
        });

        buildRecyclerView(activity);

        Intent counterIntent = getIntent();
        Bundle extras = counterIntent.getExtras();
        if(extras!=null){
            counterWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, counterWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        if(counterWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }

        counterFab.setOnClickListener(view -> {
            CreateCounter createCounter = new CreateCounter(this);
            createCounter.show();
            createCounter.createCounterButton.setOnClickListener(view1 -> {
                if(createCounter.counterLabelText.getText().toString().trim().length() > 0){
                    createCounter(createCounter.counterLabelText.getText().toString(),System.currentTimeMillis(),createCounter.counterFlag,
                            activity,0,0,false, false, false, UniqueIdGenerator.getUniqueCounterID());
                } else {
                    tickTrackDatabase.storeCounterNumber(createCounter.counterNumber+1);
                    createCounter(createCounter.counterName, System.currentTimeMillis(),createCounter.counterFlag, activity,
                            0,0,false, false, false, UniqueIdGenerator.getUniqueCounterID());
                }
                createCounter.dismiss();
            });
        });

        cancelButton.setOnClickListener(view -> finish());

        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }

    private ArrayList<CounterWidgetData> counterWidgetDataArrayList;

    private void confirmSelection(String counterStringId){

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int intentUniqueId =  UniqueIdGenerator.getUniqueIntegerCounterID();

        Intent intent = new Intent(this, CounterActivity.class);
        intent.putExtra("currentCounterPosition", counterStringId);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, intentUniqueId, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, intentUniqueId, intent, 0);
        }

        addCounterWidgetData(counterStringId, intentUniqueId);

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.counter_widget);
        views.setOnClickPendingIntent(R.id.counterWidgetRootRelativeLayout, pendingIntent);
        views.setOnClickPendingIntent(R.id.counterWidgetPlusButton, getPendingSelfIntent(this, ACTION_WIDGET_CLICK_PLUS, intentUniqueId, counterStringId ));
        views.setOnClickPendingIntent(R.id.counterWidgetMinusButton, getPendingSelfIntent(this, ACTION_WIDGET_CLICK_MINUS, intentUniqueId, counterStringId ));
        views.setTextViewText(R.id.counterWidgetCountText, ""+counterDataArrayList.get(getCurrentPosition(counterStringId)).getCounterValue());
        views.setTextViewText(R.id.counterWidgetCounterNameText, counterDataArrayList.get(getCurrentPositionStatic(counterStringId)).getCounterLabel());
        setFlag(views, counterDataArrayList.get(getCurrentPositionStatic(counterStringId)).getCounterFlag());
        setupTheme(views);
        appWidgetManager.updateAppWidget(counterWidgetId, views);


        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, counterWidgetId);
        setResult(RESULT_OK, resultValue);
        updateWidget();
        finish();
    }

    public static void confirmSelectionStatic(String counterStringId){

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);

        int intentUniqueId =  UniqueIdGenerator.getUniqueIntegerCounterID();

        Intent intent = new Intent(activity, CounterActivity.class);
        intent.putExtra("currentCounterPosition", counterStringId);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(activity, intentUniqueId, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(activity, intentUniqueId, intent, 0);
        }

        ArrayList<CounterWidgetData> counterWidgetDataArrayList = tickTrackDatabase.retrieveCounterWidgetList();

        CounterWidgetData counterWidgetData = new CounterWidgetData();
        counterWidgetData.setCounterIdInteger(intentUniqueId);
        counterWidgetData.setCounterIdString(counterStringId);
        counterWidgetData.setCounterWidgetId(counterWidgetId);
        counterWidgetData.setWidgetTheme(currentTheme);

        counterWidgetDataArrayList.add(counterWidgetData);
        tickTrackDatabase.storeCounterWidgetList(counterWidgetDataArrayList);

        RemoteViews views = new RemoteViews(activity.getPackageName(), R.layout.counter_widget);
        views.setOnClickPendingIntent(R.id.counterWidgetRootRelativeLayout, pendingIntent);
        views.setOnClickPendingIntent(R.id.counterWidgetPlusButton, getPendingSelfIntentStatic(activity, ACTION_WIDGET_CLICK_PLUS, intentUniqueId, counterStringId ));
        views.setOnClickPendingIntent(R.id.counterWidgetMinusButton, getPendingSelfIntentStatic(activity, ACTION_WIDGET_CLICK_MINUS, intentUniqueId, counterStringId ));
        views.setTextViewText(R.id.counterWidgetCountText, ""+counterDataArrayList.get(getCurrentPositionStatic(counterStringId)).getCounterValue());
        views.setTextViewText(R.id.counterWidgetCounterNameText, counterDataArrayList.get(getCurrentPositionStatic(counterStringId)).getCounterLabel());
        setupTheme(views);
        setFlag(views, counterDataArrayList.get(getCurrentPositionStatic(counterStringId)).getCounterFlag());
        appWidgetManager.updateAppWidget(counterWidgetId, views);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, counterWidgetId);
        activity.setResult(RESULT_OK, resultValue);
        updateWidget();
        activity.finish();
    }

    private static void setupTheme(RemoteViews views) {
        if( currentTheme == 1){
            views.setInt(R.id.counterWidgetRootRelativeLayout, "setBackgroundResource", R.drawable.round_rect_light);
            views.setInt(R.id.counterWidgetPlusButton, "setBackgroundResource", R.drawable.button_selector_white);
            views.setInt(R.id.counterWidgetMinusButton, "setBackgroundResource",R.drawable.button_selector_white);
            views.setTextColor(R.id.counterWidgetCountText,   activity.getResources().getColor(R.color.DarkText));
            views.setTextColor(R.id.counterWidgetCounterNameText,   activity.getResources().getColor(R.color.DarkText));
            views.setTextColor(R.id.counterWidgetPlusButton,   activity.getResources().getColor(R.color.DarkText));
            views.setTextColor(R.id.counterWidgetMinusButton,   activity.getResources().getColor(R.color.DarkText));
        } else if(currentTheme == 2){
            views.setInt(R.id.counterWidgetRootRelativeLayout, "setBackgroundResource", R.drawable.round_rect_dark);
            views.setInt(R.id.counterWidgetPlusButton, "setBackgroundResource", R.drawable.button_selector_dark);
            views.setInt(R.id.counterWidgetMinusButton, "setBackgroundResource",R.drawable.button_selector_dark);
            views.setTextColor(R.id.counterWidgetCountText,   activity.getResources().getColor(R.color.LightText));
            views.setTextColor(R.id.counterWidgetCounterNameText,   activity.getResources().getColor(R.color.LightText));
            views.setTextColor(R.id.counterWidgetPlusButton,   activity.getResources().getColor(R.color.LightText));
            views.setTextColor(R.id.counterWidgetMinusButton,   activity.getResources().getColor(R.color.LightText));
        } else if(currentTheme == 3){
            views.setInt(R.id.counterWidgetRootRelativeLayout, "setBackgroundResource", R.drawable.round_rect_black);
            views.setInt(R.id.counterWidgetPlusButton, "setBackgroundResource", R.drawable.button_selector_dark);
            views.setInt(R.id.counterWidgetMinusButton, "setBackgroundResource",R.drawable.button_selector_dark);
            views.setTextColor(R.id.counterWidgetCountText,   activity.getResources().getColor(R.color.LightText));
            views.setTextColor(R.id.counterWidgetCounterNameText,   activity.getResources().getColor(R.color.LightText));
            views.setTextColor(R.id.counterWidgetPlusButton,   activity.getResources().getColor(R.color.LightText));
            views.setTextColor(R.id.counterWidgetMinusButton,   activity.getResources().getColor(R.color.LightText));
        }
    }

    private static void updateWidget(){
        Intent intent = new Intent(activity, CounterWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(activity).getAppWidgetIds(new ComponentName(activity, CounterWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        activity.sendBroadcast(intent);
    }

    private static void setFlag(RemoteViews views, int counterFlag) {
        if(counterFlag==1){
            views.setImageViewResource(R.id.counterWidgetFlag, R.drawable.ic_flag_red);
        }
        else if(counterFlag==2){
            views.setImageViewResource(R.id.counterWidgetFlag, R.drawable.ic_flag_green);
        }
        else if(counterFlag==3){
            views.setImageViewResource(R.id.counterWidgetFlag, R.drawable.ic_flag_orange);
        }
        else if(counterFlag==4){
            views.setImageViewResource(R.id.counterWidgetFlag, R.drawable.ic_flag_purple);
        }
        else if(counterFlag==5){
            views.setImageViewResource(R.id.counterWidgetFlag, R.drawable.ic_flag_blue);
        } else {
            views.setViewVisibility(R.id.counterWidgetFlag, View.GONE);
        }
    }

    private static PendingIntent getPendingSelfIntentStatic(Context context, String action, int counterID, String counterIdString) {

        Intent intent = new Intent(context, context.getClass());
        intent.setAction(action);
        intent.putExtra("counterID", counterIdString);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getBroadcast(context, counterID, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            return PendingIntent.getBroadcast(context, counterID, intent, 0);
        }
    }
    private PendingIntent getPendingSelfIntent(Context context, String action, int counterID, String counterIdString) {

        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra("counterID", counterIdString);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getBroadcast(context, counterID, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            return PendingIntent.getBroadcast(context, counterID, intent, 0);
        }
    }

    private static int getCurrentPositionStatic(String counterID) {
        for(int i = 0; i < counterDataArrayList.size(); i ++){
            if(counterDataArrayList.get(i).getCounterID().equals(counterID)){
                return i;
            }
        }
        return 0;
    }
    private int getCurrentPosition(String counterID) {
        for(int i = 0; i < counterDataArrayList.size(); i ++){
            if(counterDataArrayList.get(i).getCounterID().equals(counterID)){
                return i;
            }
        }
        return 0;
    }


    private void addCounterWidgetData(String counterStringId, int counterIntegerId) {
        counterWidgetDataArrayList = tickTrackDatabase.retrieveCounterWidgetList();

        CounterWidgetData counterWidgetData = new CounterWidgetData();
        counterWidgetData.setCounterIdInteger(counterIntegerId);
        counterWidgetData.setCounterIdString(counterStringId);
        counterWidgetData.setCounterWidgetId(counterWidgetId);
        counterWidgetData.setWidgetTheme(currentTheme);

        counterWidgetDataArrayList.add(counterWidgetData);
        tickTrackDatabase.storeCounterWidgetList(counterWidgetDataArrayList);

    }

    private static void buildRecyclerView(Activity activity) {
        counterAdapter = new WidgetCounterAdapter(activity, counterDataArrayList);
        if(counterDataArrayList.size()>0){

            counterRecyclerView.setVisibility(View.VISIBLE);
            noCounterText.setVisibility(View.INVISIBLE);

            Collections.sort(counterDataArrayList);

            counterRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            counterRecyclerView.setItemAnimator(new DefaultItemAnimator());
            counterRecyclerView.setAdapter(counterAdapter);

            counterAdapter.diffUtilsChangeData(counterDataArrayList);

        } else {
            counterRecyclerView.setVisibility(View.INVISIBLE);
            noCounterText.setVisibility(View.VISIBLE);
        }

    }

    private static String getCounterID(int position) {
        ArrayList<CounterData> counterData = tickTrackDatabase.retrieveCounterList();
        return counterData.get(position).getCounterID();
    }

    public void createCounter(String counterLabel, long createdTimestamp, int counterFlag, Activity activity, int significantCount,
                                     int countValue, boolean isSignificant, boolean isSwipe, boolean isPersistent, String uniqueCounterID){
        CounterData counterData = new CounterData();
        counterData.setCounterLabel(counterLabel);
        counterData.setCounterValue(countValue);
        counterData.setCounterTimestamp(createdTimestamp);
        counterData.setCounterFlag(counterFlag);
        counterData.setCounterSignificantCount(significantCount);
        counterData.setCounterSignificantExist(isSignificant);
        counterData.setCounterSwipeMode(isSwipe);
        counterData.setCounterPersistentNotification(isPersistent);
        counterData.setCounterID(uniqueCounterID);
        counterData.setNegativeAllowed(false);

        counterDataArrayList.add(counterData);
        System.out.println(counterDataArrayList.size());
        tickTrackDatabase.storeCounterList(counterDataArrayList);
        buildRecyclerView(activity);

        confirmSelection(counterData.getCounterID());

    }

}