package com.theflopguyproductions.ticktrack.widgets.counter;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

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
    private Activity activity;
    private static TextView noCounterText;
    private SharedPreferences sharedPreferences;
    private static TickTrackDatabase tickTrackDatabase;
    private ConstraintLayout counterFragmentRootLayout;
    private FloatingActionButton counterFab;
    private static RecyclerView counterRecyclerView;

    private int counterWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

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

        buildRecyclerView(activity);

        TickTrackThemeSetter.counterFragmentTheme(this, counterRecyclerView, counterFragmentRootLayout, noCounterText, tickTrackDatabase);

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
                            this.activity,0,0,false, false, false, UniqueIdGenerator.getUniqueCounterID());
                } else {
                    tickTrackDatabase.storeCounterNumber(createCounter.counterNumber+1);
                    createCounter(createCounter.counterName, System.currentTimeMillis(),createCounter.counterFlag, this.activity,
                            0,0,false, false, false, UniqueIdGenerator.getUniqueCounterID());
                }
                createCounter.dismiss();
            });
        });

        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }

    private ArrayList<CounterWidgetData> counterWidgetDataArrayList;

    private void confirmSelection(String counterStringId){

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int intentUniqueId =  UniqueIdGenerator.getUniqueIntegerCounterID();

        Intent intent = new Intent(this, CounterActivity.class);
        intent.putExtra("currentCounterPosition", counterStringId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, intentUniqueId, intent, 0);

        addCounterWidgetData(counterStringId, intentUniqueId);

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.counter_widget);
        views.setOnClickPendingIntent(R.id.counterWidgetRootRelativeLayout, pendingIntent);
        views.setOnClickPendingIntent(R.id.counterWidgetPlusButton, getPendingSelfIntent(this, ACTION_WIDGET_CLICK_PLUS, intentUniqueId, counterStringId ));
        views.setOnClickPendingIntent(R.id.counterWidgetMinusButton, getPendingSelfIntent(this, ACTION_WIDGET_CLICK_MINUS, intentUniqueId, counterStringId ));
        views.setTextViewText(R.id.counterWidgetCountText, ""+counterDataArrayList.get(getCurrentPosition(counterStringId)).getCounterValue());
        appWidgetManager.updateAppWidget(counterWidgetId, views);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, counterWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
    private int getCurrentPosition(String counterID) {
        for(int i = 0; i < counterDataArrayList.size(); i ++){
            if(counterDataArrayList.get(i).getCounterID().equals(counterID)){
                return i;
            }
        }
        return 0;
    }
    private PendingIntent getPendingSelfIntent(Context context, String action, int counterID, String counterIdString) {

        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra("counterID", counterIdString);
        return PendingIntent.getBroadcast(context, counterID, intent, 0);
    }

    private void addCounterWidgetData(String counterStringId, int counterIntegerId) {
        counterWidgetDataArrayList = tickTrackDatabase.retrieveCounterWidgetList();

        CounterWidgetData counterWidgetData = new CounterWidgetData();
        counterWidgetData.setCounterIdInteger(counterIntegerId);
        counterWidgetData.setCounterIdString(counterStringId);
        counterWidgetData.setCounterWidgetId(counterWidgetId);

        counterWidgetDataArrayList.add(counterWidgetData);
        tickTrackDatabase.storeCounterWidgetList(counterWidgetDataArrayList);

    }

    private static void buildRecyclerView(Activity activity) {

        WidgetCounterAdapter.counterDataViewHolder.RecyclerViewClickListener listener = (view, position) -> {
            Toast.makeText(activity, "Position " + position+" ID "+getCounterID(position), Toast.LENGTH_SHORT).show();
        };

        counterAdapter = new WidgetCounterAdapter(activity, counterDataArrayList, listener);

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

        counterDataArrayList.add(counterData);
        System.out.println(counterDataArrayList.size());
        tickTrackDatabase.storeCounterList(counterDataArrayList);
        buildRecyclerView(activity);

        confirmSelection(counterData.getCounterID());

    }

}