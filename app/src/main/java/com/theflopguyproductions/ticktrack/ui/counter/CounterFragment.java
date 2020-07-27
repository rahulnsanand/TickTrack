package com.theflopguyproductions.ticktrack.ui.counter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.operator.ObjTakeUntilIndexed;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterAdapter;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.activity.CounterActivity;
import com.theflopguyproductions.ticktrack.dialogs.CreateCounter;
import com.theflopguyproductions.ticktrack.dialogs.DeleteCounter;
import com.theflopguyproductions.ticktrack.ui.utils.deletehelper.CounterSlideDeleteHelper;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class CounterFragment extends Fragment implements CounterSlideDeleteHelper.RecyclerItemTouchHelperListener{

    private static ArrayList<CounterData> counterDataArrayList = new ArrayList<>();
    private static CounterAdapter counterAdapter;
    private static RecyclerView counterRecyclerView;
    private FloatingActionButton counterFab;
    private static TextView noCounterText;
    private ConstraintLayout counterFragmentRootLayout;

    @Override
    public void onResume() {
        super.onResume();

        counterDataArrayList = TickTrackDatabase.retrieveCounterList(getActivity());
        TickTrackThemeSetter.counterFragmentTheme(getActivity(), counterRecyclerView, counterFragmentRootLayout, noCounterText);
        buildRecyclerView(getActivity());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_counter, container, false);

        counterDataArrayList = TickTrackDatabase.retrieveCounterList(requireActivity());
        counterRecyclerView = root.findViewById(R.id.counterRecycleView);
        noCounterText = root.findViewById(R.id.counterFragmentNoCounterText);
        counterFragmentRootLayout = root.findViewById(R.id.counterRootLayout);

        TickTrackThemeSetter.counterFragmentTheme(getActivity(), counterRecyclerView, counterFragmentRootLayout, noCounterText);

        counterFab = root.findViewById(R.id.counterAddButton);

        counterFab.setOnClickListener(view -> {
            CreateCounter createCounter = new CreateCounter(getActivity());
            createCounter.show();
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new CounterSlideDeleteHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(counterRecyclerView);

        return root;
    }

    private static void buildRecyclerView(Activity activity) {
        counterAdapter = new CounterAdapter(counterDataArrayList);

        if(counterDataArrayList.size()>0){
            counterRecyclerView.setVisibility(View.VISIBLE);
            noCounterText.setVisibility(View.INVISIBLE);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
            counterRecyclerView.setItemAnimator(new DefaultItemAnimator());
            counterRecyclerView.setHasFixedSize(true);

            Collections.sort(counterDataArrayList);

            TickTrackDatabase.storeCounterList(counterDataArrayList, activity);

            counterRecyclerView.setLayoutManager(layoutManager);
            counterRecyclerView.setAdapter(counterAdapter);
            counterAdapter.notifyDataSetChanged();
            counterRecyclerView.scheduleLayoutAnimation();

        } else {
            counterRecyclerView.setVisibility(View.INVISIBLE);
            noCounterText.setVisibility(View.VISIBLE);
        }

    }

    public static void createCounter(String counterLabel, Timestamp createdTimestamp, int counterFlag, Activity activity, int significantCount,
                                     int countValue, boolean isSignificant, boolean isSwipe, boolean isPersistent){
        CounterData counterData = new CounterData();
        counterData.setCounterLabel(counterLabel);
        counterData.setCounterValue(countValue);
        counterData.setCounterTimestamp(createdTimestamp);
        counterData.setCounterFlag(counterFlag);
        counterData.setCounterSignificantCount(significantCount);
        counterData.setCounterSignificantExist(isSignificant);
        counterData.setCounterSwipeMode(isSwipe);
        counterData.setCounterPersistentNotification(isPersistent);
        counterDataArrayList.add(0,counterData);
        buildRecyclerView(activity);
        counterRecyclerView.scheduleLayoutAnimation();
        TickTrackDatabase.storeCounterList(counterDataArrayList, activity);
    }

    String deletedCounter = null;
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CounterAdapter.RecyclerItemViewHolder) {
            deletedCounter = counterDataArrayList.get(viewHolder.getAdapterPosition()).getCounterLabel();
            position = viewHolder.getAdapterPosition();

            DeleteCounter counterDelete = new DeleteCounter(getActivity(), position, deletedCounter);
            counterDelete.show();

        }
    }

    public static void deleteCounter(int position, Activity activity, String counterName){
        deleteItem(position, activity);
        Toast.makeText(activity, "Deleted Counter " + counterName, Toast.LENGTH_SHORT).show();
    }
    public static void refreshRecyclerView(){
        counterAdapter.notifyDataSetChanged();
    }
    public static void deleteItem(int position, Activity activity){
        counterDataArrayList.remove(position);
        TickTrackDatabase.storeCounterList(counterDataArrayList, activity);
        buildRecyclerView(activity);
        counterRecyclerView.scheduleLayoutAnimation();

    }

    public static void startCounterActivity(int adapterPosition, Activity activity) {
        Intent intent = new Intent(activity, CounterActivity.class);
        intent.putExtra("currentCounterPosition", adapterPosition);
        activity.startActivity(intent);
    }

}