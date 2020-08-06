package com.theflopguyproductions.ticktrack.ui.timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.activity.TimerVisibleActivity;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.util.ArrayList;

public class TimerFragment extends Fragment {

    static TickTrackDatabase tickTrackDatabase;

    private FloatingActionButton floatingActionButton, timerDiscardFAB;
    private boolean isFirst = true;

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private Activity activity;


    public static void startTimerActivity(int position, Activity context) {
        Intent timerIntent = new Intent(context, TimerVisibleActivity.class);
        ArrayList<TimerData> timerData;
        timerData = tickTrackDatabase.retrieveTimerList();
        timerIntent.putExtra("timerID",timerData.get(position).getTimerStringID());
        context.startActivity(timerIntent);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        if(timerDataArrayList.size()>0){
            timerDiscardFAB.setVisibility(View.INVISIBLE);
            displayRecyclerView();
        } else {
            displayCreatorView();
            isFirst = tickTrackDatabase.isFirstTimer();
            if(isFirst){
                timerDiscardFAB.setVisibility(View.INVISIBLE);
            } else {
                timerDiscardFAB.setVisibility(View.VISIBLE);
            }
        }
        floatingActionButton.setOnClickListener(view1 -> {
            addTimer();
        });
        timerDiscardFAB.setOnClickListener(view14 -> displayRecyclerView());
    }

    private void displayCreatorView() {
        floatingActionButton.setVisibility(View.INVISIBLE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.from_right, R.anim.to_right);
        tickTrackDatabase.setFirstTimer(true);
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, new TimerCreatorFragment()).commit();
    }

    private void displayRecyclerView() {
        timerDiscardFAB.setVisibility(View.INVISIBLE);
        floatingActionButton.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.from_right, R.anim.to_right);
        transaction.replace(R.id.timerFragmentInnerFragmentContainer,  new TimerRecyclerFragment()).commit();
    }

    private void addTimer(){
        isFirst = tickTrackDatabase.isFirstTimer();
        if(isFirst){
            timerDiscardFAB.setVisibility(View.INVISIBLE);
        } else {
            timerDiscardFAB.setVisibility(View.VISIBLE);
        }
        floatingActionButton.setVisibility(View.INVISIBLE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.from_right, R.anim.to_right);
        tickTrackDatabase.setFirstTimer(false);
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, new TimerCreatorFragment()).commit();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_timer, container, false);

        floatingActionButton = root.findViewById(R.id.timerFragmentFAB);
        timerDiscardFAB = root.findViewById(R.id.timerCreateFragmentDiscardFAB);
        activity = getActivity();

        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        isFirst = tickTrackDatabase.isFirstTimer();

        return root;
    }

}