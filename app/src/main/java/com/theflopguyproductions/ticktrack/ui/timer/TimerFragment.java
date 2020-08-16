package com.theflopguyproductions.ticktrack.ui.timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.activity.TimerActivity;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;

public class TimerFragment extends Fragment {

    static TickTrackDatabase tickTrackDatabase;

    private FloatingActionButton floatingActionButton, timerDiscardFAB;
    private boolean isFirst = true, recyclerOn=false;

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private Activity activity;


    public static void startTimerActivity(int position, Activity context) {
        Intent timerIntent = new Intent(context, TimerActivity.class);
        timerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ArrayList<TimerData> timerData;
        timerData = tickTrackDatabase.retrieveTimerList();
        timerIntent.putExtra("timerID",timerData.get(position).getTimerID());
        context.startActivity(timerIntent);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        if(timerDataArrayList.size()>0){
            TickTrackAnimator.fabDissolve(timerDiscardFAB);
            displayRecyclerView();
        } else {
            displayCreatorView();
            isFirst = tickTrackDatabase.isFirstTimer();
            if(isFirst){
                TickTrackAnimator.fabDissolve(timerDiscardFAB);
            } else {
                TickTrackAnimator.fabUnDissolve(timerDiscardFAB);
            }
        }
        floatingActionButton.setOnClickListener(view1 -> {
            addTimer();
        });
        timerDiscardFAB.setOnClickListener(view14 -> displayRecyclerView());

        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();
        requireView().setOnKeyListener((v, keyCode, event) -> {
            if( keyCode == KeyEvent.KEYCODE_BACK ) {
                if(!isFirst){
                    if(!recyclerOn){
                        displayRecyclerView();
                        return true;
                    }
                } else {
                    requireActivity().finish();
                }
                return false;
            }
            return false;
        });

    }

    private void displayCreatorView() {
        TickTrackAnimator.fabDissolve(floatingActionButton);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        tickTrackDatabase.setFirstTimer(true);
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, new TimerCreatorFragment()).commit();
        recyclerOn=false;
    }

    private void displayRecyclerView() {
        TickTrackAnimator.fabDissolve(timerDiscardFAB);
        TickTrackAnimator.fabUnDissolve(floatingActionButton);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.timerFragmentInnerFragmentContainer,  new TimerRecyclerFragment()).commit();
        recyclerOn=true;
    }

    private void addTimer(){
        isFirst = tickTrackDatabase.isFirstTimer();
        if(isFirst){
            TickTrackAnimator.fabDissolve(timerDiscardFAB);
        } else {
            TickTrackAnimator.fabUnDissolve(timerDiscardFAB);
        }
        TickTrackAnimator.fabDissolve(floatingActionButton);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        tickTrackDatabase.setFirstTimer(false);
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, new TimerCreatorFragment()).commit();
        recyclerOn=false;
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