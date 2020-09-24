package com.theflopguyproductions.ticktrack.ui.counter;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterAdapter;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.activity.CounterActivity;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.dialogs.CreateCounter;
import com.theflopguyproductions.ticktrack.dialogs.DeleteCounter;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.ui.utils.deletehelper.CounterSlideDeleteHelper;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.widgets.counter.CounterWidget;
import com.theflopguyproductions.ticktrack.widgets.counter.data.CounterWidgetData;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;


public class CounterFragment extends Fragment implements CounterSlideDeleteHelper.RecyclerItemTouchHelperListener{

    private static final long LONG_MAX_VALUE = 9223372036854775807L;
    private static final long LONG_MAX_VALUE_NEGATIVE = -9223372036854775807L;

    private static ArrayList<CounterData> counterDataArrayList = new ArrayList<>();
    private static CounterAdapter counterAdapter;
    private static RecyclerView counterRecyclerView;
    private ConstraintLayout counterFab;
    private static TextView noCounterText;
    private static ConstraintLayout counterFragmentRootLayout;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private static TickTrackDatabase tickTrackDatabase;

    private ConstraintLayout sumLayout, deleteHelpLayout;
    private TextView sumValue;

    private String receivedAction;

    public CounterFragment() {
    }

    public CounterFragment(String shortcutAction) {
        this.receivedAction = shortcutAction;
    }

    @Override
    public void onStop() {
        super.onStop();
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private boolean isLongSumDisplayed = false;
    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if("counterCreate".equals(receivedAction)){
            createCounterDialog(activity);
        }
        TickTrackAnimator.fabLayoutUnDissolve(counterFab);
        if(tickTrackDatabase.isSumEnabled()){
            setupSumLayout();
        } else {
            sumLayout.setVisibility(View.GONE);
            sumValue.setVisibility(View.GONE);
        }
        TickTrackThemeSetter.counterFragmentTheme(getActivity(), counterRecyclerView, counterFragmentRootLayout, noCounterText, tickTrackDatabase, sumLayout, counterFab);
        sumLayout.setOnClickListener(view -> {
            if(isLongSumDisplayed){
                if(sum>1000){
                    sumValue.setText("Sum: "+format(sum));

                }
                isLongSumDisplayed = false;
            } else {
                NumberFormat myFormat = new DecimalFormat("#,###");
                sumValue.setText("Sum: "+myFormat.format(sum));
                isLongSumDisplayed = true;
            }
        });
    }

    private static void createCounterDialog(Activity context){
        new Handler().post(() -> {
            CreateCounter createCounter = new CreateCounter(context);
            createCounter.show();
        });
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "q");
        suffixes.put(1_000_000_000_000_000_000L, "Q");
    }
    private long sum = 0;
    private void setupSumLayout() {
        TickTrackAnimator.layoutUnDissolve(sumLayout);
        sumValue.setVisibility(View.VISIBLE);

        sum = 0L;
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        for(int i=0; i<counterDataArrayList.size(); i++){
            long currentValue = counterDataArrayList.get(i).getCounterValue();
            if(sum>=LONG_MAX_VALUE_NEGATIVE && sum<LONG_MAX_VALUE){
                sum += currentValue;
            }
        }
        sumValue.setText("Sum: "+format(sum));
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }


    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        if (s.equals("CounterData")){
            Collections.sort(counterDataArrayList);
            counterAdapter.diffUtilsChangeData(counterDataArrayList);
            setupSumLayout();

            if(counterDataArrayList.size()>0){
                counterRecyclerView.setVisibility(View.VISIBLE);
                noCounterText.setVisibility(View.INVISIBLE);
                checkForFirstCounter();
            } else {
                counterRecyclerView.setVisibility(View.INVISIBLE);
                noCounterText.setVisibility(View.VISIBLE);
            }

        }
        System.out.println("ActivityManager: Displayed CounterFrag SharedPref "+System.currentTimeMillis());
    };

    private void checkForFirstCounter(){
        if(tickTrackDatabase.isSwipeDeleteInformed()){
            if(counterDataArrayList.size()>0){
                deleteHelpLayout.setVisibility(View.VISIBLE);
                deleteHelpLayout.setOnClickListener(view -> {
                    tickTrackDatabase.setSwipeDeleteInformed(false);
                    deleteHelpLayout.setVisibility(View.GONE);
                });
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();

        buildRecyclerView(activity);
        counterFab.setOnClickListener(view1 -> createCounterDialog(activity));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new CounterSlideDeleteHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(counterRecyclerView);
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        System.out.println("ActivityManager: Displayed CounterFrag OnCreateViewED "+System.currentTimeMillis());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_counter, container, false);
        activity = getActivity();
        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        counterRecyclerView = root.findViewById(R.id.counterRecycleView);
        noCounterText = root.findViewById(R.id.counterFragmentNoCounterText);
        counterFragmentRootLayout = root.findViewById(R.id.counterRootLayout);
        sumLayout = root.findViewById(R.id.counterFragmentSumLayout);
        sumValue = root.findViewById(R.id.counterFragmentSumValue);
        counterFab = root.findViewById(R.id.counterAddButton);
        deleteHelpLayout = root.findViewById(R.id.counterDeleteHelpLayout);
        deleteHelpLayout.setVisibility(View.GONE);
        System.out.println("ActivityManager: Displayed CounterFrag OnCreateView "+System.currentTimeMillis());
        return root;
    }

    private static void buildRecyclerView(Activity activity) {

        counterAdapter = new CounterAdapter(activity, counterDataArrayList);

        if(counterDataArrayList.size()>0){

            counterRecyclerView.setVisibility(View.VISIBLE);
            noCounterText.setVisibility(View.INVISIBLE);

            Collections.sort(counterDataArrayList);
            tickTrackDatabase.storeCounterList(counterDataArrayList);

            counterRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            counterRecyclerView.setItemAnimator(new DefaultItemAnimator());
            counterRecyclerView.setAdapter(counterAdapter);

            counterAdapter.diffUtilsChangeData(counterDataArrayList);

        } else {
            counterRecyclerView.setVisibility(View.INVISIBLE);
            noCounterText.setVisibility(View.VISIBLE);
        }

    }

    public static void createCounter(String counterLabel, long createdTimestamp, int counterFlag, Activity activity, int significantCount,
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

    }

    String deletedCounter = null;
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CounterAdapter.counterDataViewHolder) {
            if(position!=counterDataArrayList.size()){
                deletedCounter = counterDataArrayList.get(position).getCounterLabel();

                new Handler().post(() -> {
                    killNotification(counterDataArrayList.get(position).getCounterID());
                    DeleteCounter counterDelete = new DeleteCounter(getActivity(), position, deletedCounter);
                    counterDelete.show();
                    counterDelete.yesButton.setOnClickListener(view12 -> {
                        deleteCounter(position, activity, deletedCounter);
                        counterDelete.dismiss();
                    });
                    counterDelete.noButton.setOnClickListener(view1 -> {
                        refreshItemChanged(position);
                        counterDelete.dismiss();
                    });
                    counterDelete.setOnCancelListener(dialogInterface -> {
                        refreshItemChanged(position);
                        counterDelete.cancel();
                    });
                });
            }
        }
    }
    public void killNotification(String counterID) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if(counterID.equals(tickTrackDatabase.getCurrentCounterNotificationID())){
            Intent intent = new Intent(activity, CounterNotificationService.class);
            intent.setAction(CounterNotificationService.ACTION_KILL_NOTIFICATIONS);
            activity.startService(intent);
        }
    }

    public static void deleteCounter(int position, Activity activity, String counterName){
        deleteItem(position, activity);
        Intent intent = new Intent(activity, CounterWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), CounterWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        activity.sendBroadcast(intent);
        Snackbar snackbar = Snackbar
                .make(counterFragmentRootLayout ,"Deleted Counter " + counterName, Snackbar.LENGTH_INDEFINITE)
                .setBackgroundTint(activity.getResources().getColor(R.color.Accent))
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setDuration(1000);
        snackbar.show();
    }
    public static void refreshItemChanged(int position){
        counterAdapter.notifyItemChanged(position);
    }
    public static void deleteItem(int position, Activity activity){

        ArrayList<CounterWidgetData> counterWidgetData = tickTrackDatabase.retrieveCounterWidgetList();
        for(int i=0; i<counterWidgetData.size(); i++){
            if(counterWidgetData.get(i).getCounterIdString().equals(counterDataArrayList.get(position).getCounterID())){
                counterWidgetData.remove(i);
                tickTrackDatabase.storeCounterWidgetList(counterWidgetData);
                counterDataArrayList.remove(position);
                tickTrackDatabase.storeCounterList(counterDataArrayList);
                counterAdapter.notifyItemRemoved(position);
                return;
            }
        }
        counterDataArrayList.remove(position);
        tickTrackDatabase.storeCounterList(counterDataArrayList);
        counterAdapter.notifyItemRemoved(position);
        buildRecyclerView(activity);
    }

    public static void startCounterActivity(String counterID, Activity activity) {
        Intent intent = new Intent(activity, CounterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("currentCounterPosition", counterID);
        activity.startActivity(intent);
    }
}