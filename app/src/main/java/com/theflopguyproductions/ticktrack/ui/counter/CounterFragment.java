package com.theflopguyproductions.ticktrack.ui.counter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterAdapter;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.activity.CounterActivity;
import com.theflopguyproductions.ticktrack.dialogs.CreateCounter;
import com.theflopguyproductions.ticktrack.dialogs.DeleteCounter;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.ui.utils.deletehelper.CounterSlideDeleteHelper;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

import java.util.ArrayList;
import java.util.Collections;

public class CounterFragment extends Fragment implements CounterSlideDeleteHelper.RecyclerItemTouchHelperListener{

    private static ArrayList<CounterData> counterDataArrayList = new ArrayList<>();
    private static CounterAdapter counterAdapter;
    private static RecyclerView counterRecyclerView;
    private FloatingActionButton counterFab;
    private static TextView noCounterText;
    private ConstraintLayout counterFragmentRootLayout;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private static TickTrackDatabase tickTrackDatabase;

    private ConstraintLayout sumLayout;
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
        if("counterCreate".equals(receivedAction)){
            CreateCounter createCounter = new CreateCounter(getActivity());
            createCounter.show();
        }
        if(tickTrackDatabase.isSumEnabled()){
            setupSumLayout();
        } else {
            sumLayout.setVisibility(View.GONE);
            sumValue.setVisibility(View.GONE);
        }
    }

    private void setupSumLayout() {
        sumLayout.setVisibility(View.VISIBLE);
        sumValue.setVisibility(View.VISIBLE);

        long sum = 0;
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        for(int i=0; i<counterDataArrayList.size(); i++){
            if(counterDataArrayList.get(i).getCounterValue()>0){
                sum += counterDataArrayList.get(i).getCounterValue();
            }
        }
        sumValue.setText("Sum: "+sum);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        if (s.equals("CounterData")){
            Collections.sort(counterDataArrayList);
            counterAdapter.diffUtilsChangeData(counterDataArrayList);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_counter, container, false);
        activity = getActivity();
        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        counterRecyclerView = root.findViewById(R.id.counterRecycleView);
        noCounterText = root.findViewById(R.id.counterFragmentNoCounterText);
        counterFragmentRootLayout = root.findViewById(R.id.counterRootLayout);
        sumLayout = root.findViewById(R.id.counterFragmentSumLayout);
        sumValue = root.findViewById(R.id.counterFragmentSumValue);

        buildRecyclerView(activity);

        TickTrackThemeSetter.counterFragmentTheme(getActivity(), counterRecyclerView, counterFragmentRootLayout, noCounterText, tickTrackDatabase);

        counterFab = root.findViewById(R.id.counterAddButton);

        counterFab.setOnClickListener(view -> {
            CreateCounter createCounter = new CreateCounter(getActivity());
            createCounter.show();
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new CounterSlideDeleteHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(counterRecyclerView);
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        return root;
    }

    private static void buildRecyclerView(Activity activity) {

        counterAdapter = new CounterAdapter(activity, counterDataArrayList);

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

        counterDataArrayList.add(counterData);
        System.out.println(counterDataArrayList.size());
        tickTrackDatabase.storeCounterList(counterDataArrayList);
        buildRecyclerView(activity);

    }

    String deletedCounter = null;
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CounterAdapter.counterDataViewHolder) {

            sharedPreferences = tickTrackDatabase.getSharedPref(activity);
//            sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

            deletedCounter = counterDataArrayList.get(viewHolder.getAdapterPosition()).getCounterLabel();
            position = viewHolder.getAdapterPosition();

            DeleteCounter counterDelete = new DeleteCounter(getActivity(), position, deletedCounter);
            counterDelete.show();

        }
    }

    public static void deleteCounter(int position, Activity activity, String counterName){
        deleteItem(position);
        Toast.makeText(activity, "Deleted Counter " + counterName, Toast.LENGTH_SHORT).show();
    }
    public static void refreshItemChanged(int position){
        counterAdapter.notifyItemChanged(position);
    }
    public static void deleteItem(int position){
        counterAdapter.notifyItemRemoved(position);
        counterDataArrayList.remove(position);
        tickTrackDatabase.storeCounterList(counterDataArrayList);
    }

    public static void startCounterActivity(String counterID, Activity activity) {
        Intent intent = new Intent(activity, CounterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("currentCounterPosition", counterID);
        activity.startActivity(intent);
    }
}