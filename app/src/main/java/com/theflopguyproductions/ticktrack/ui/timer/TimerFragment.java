package com.theflopguyproductions.ticktrack.ui.timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.activity.TimerActivity;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.util.ArrayList;

public class TimerFragment extends Fragment {

    static TickTrackDatabase tickTrackDatabase;

    private FloatingActionButton floatingActionButton;

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private Activity activity;

    private Fragment timerCreatorFragment = new TimerCreatorFragment();
    private Fragment timerRecyclerFragment = new TimerRecyclerFragment();

    public static void startTimerActivity(int position, Activity context) {
        Intent timerIntent = new Intent(context, TimerActivity.class);
        ArrayList<TimerData> timerData;
        timerData = tickTrackDatabase.retrieveTimerList();
        timerIntent.putExtra("timerID",timerData.get(position).getTimerID());
        context.startActivity(timerIntent);

    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        if(timerDataArrayList.size()>0){
            displayRecyclerView();
        } else {
            displayCreatorView();
        }

        floatingActionButton.setOnClickListener(view1 -> {
            addTimer();
        });
    }

    private void displayCreatorView() {
        floatingActionButton.setVisibility(View.INVISIBLE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        tickTrackDatabase.setFirstTimer(true);
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, timerCreatorFragment).commit();
    }

    private void displayRecyclerView() {
        floatingActionButton.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, timerRecyclerFragment).commit();
    }

    private void addTimer(){
        floatingActionButton.setVisibility(View.INVISIBLE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        tickTrackDatabase.setFirstTimer(false);
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, timerCreatorFragment).commit();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_timer, container, false);

        floatingActionButton = root.findViewById(R.id.timerFragmentFAB);
        activity = getActivity();

        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();


        return root;
    }

}