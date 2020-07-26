package com.theflopguyproductions.ticktrack.ui.counter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterAdapter;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.dialogs.CreateCounter;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class CounterFragment extends Fragment {

    private static ArrayList<CounterData> counterDataArrayList = new ArrayList<>();
    private static CounterAdapter counterAdapter;
    private RecyclerView counterRecyclerView;
    private int themeMode=1;
    private FloatingActionButton counterFab;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_counter, container, false);

        themeMode = TickTrackDatabase.getThemeMode(getActivity());

        counterDataArrayList = TickTrackDatabase.retrieveCounterList(requireActivity());
        counterRecyclerView = root.findViewById(R.id.counterRecycleView);
        buildRecyclerView();
        counterFab = root.findViewById(R.id.counterAddButton);

        counterFab.setOnClickListener(view -> {
            CreateCounter createCounter = new CreateCounter(getActivity());
            createCounter.show();
        });

        return root;
    }

    private void buildRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        counterRecyclerView.setItemAnimator(new DefaultItemAnimator());
        counterRecyclerView.setHasFixedSize(true);

        Collections.sort(counterDataArrayList);

        counterAdapter = new CounterAdapter(counterDataArrayList);
        counterRecyclerView.setLayoutManager(layoutManager);
        counterRecyclerView.setAdapter(counterAdapter);
        counterAdapter.notifyDataSetChanged();
    }

    public static void createCounter(String counterLabel, Timestamp createdTimestamp, int counterFlag, Activity activity){
        CounterData counterData = new CounterData();
        counterData.setCounterLabel(counterLabel);
        counterData.setCounterValue(0);
        counterData.setCounterTimestamp(createdTimestamp);
        counterData.setCounterFlag(counterFlag);
        counterDataArrayList.add(0,counterData);
        counterAdapter.notifyData(counterDataArrayList);
        TickTrackDatabase.storeCounterList(counterDataArrayList, activity);
    }

}