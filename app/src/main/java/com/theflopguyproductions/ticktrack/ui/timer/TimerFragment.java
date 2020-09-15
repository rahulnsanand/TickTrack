package com.theflopguyproductions.ticktrack.ui.timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.activity.TimerActivity;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;

public class TimerFragment extends Fragment {

    static TickTrackDatabase tickTrackDatabase;

    private static ConstraintLayout quickTimerFab, normalTimerFab;
    private static ConstraintLayout timerPlusFab;
    private static TextView timerText, quickTimerText;
    private com.google.android.material.floatingactionbutton.FloatingActionButton timerDiscardFAB;
    private boolean recyclerOn=false;
    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private Activity activity;

    private String action;

    public TimerFragment() {
    }

    public TimerFragment(String action){
        this.action = action;
    }

    public static void startTimerActivity(String timerId, Activity context) {
        Intent timerIntent = new Intent(context, TimerActivity.class);
        timerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        timerIntent.putExtra("timerID", timerId);
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
        if("timerCreate".equals(action)){
            addTimer();
        } else {
            displayRecyclerView();
        }

        timerPlusFab.setOnClickListener(view1 -> toggleFabOptions());

        normalTimerFab.setOnClickListener(view12 -> addTimer());
        quickTimerFab.setOnClickListener(view15 -> addQuickTimer());
        timerDiscardFAB.setOnClickListener(view14 -> displayRecyclerView());

        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();
        requireView().setOnKeyListener((v, keyCode, event) -> {
            if( keyCode == KeyEvent.KEYCODE_BACK ) {
                if(!recyclerOn){
                    if(isOptionsOpen){
                        toggleFabOptions();
                    }
                    displayRecyclerView();
                    return true;
                } else {
                    requireActivity().finish();
                }
                return false;
            }
            return false;
        });

    }

    private void displayRecyclerView() {
        TickTrackAnimator.fabDissolve(timerDiscardFAB);
        TickTrackAnimator.fabLayoutUnDissolve(timerPlusFab);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.timerFragmentInnerFragmentContainer,  new TimerRecyclerFragment()).commit();
        recyclerOn=true;
    }

    private void addTimer(){
        toggleFabOptions();
        TickTrackAnimator.fabLayoutDissolve(timerPlusFab);
        TickTrackAnimator.fabUnDissolve(timerDiscardFAB);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        tickTrackDatabase.setFirstTimer(false);
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, new TimerCreatorFragment()).commit();
        recyclerOn=false;
    }

    private void addQuickTimer(){
        toggleFabOptions();
        TickTrackAnimator.fabLayoutDissolve(timerPlusFab);
        TickTrackAnimator.fabUnDissolve(timerDiscardFAB);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, new QuickTimerCreatorFragment()).commit();
        recyclerOn=false;
    }

    private static boolean isOptionsOpen = false;
    private void toggleFabOptions() {
        if(isOptionsOpen){
            TickTrackAnimator.collapseFabMenu(timerPlusFab, normalTimerFab, quickTimerFab, timerText, quickTimerText);
            isOptionsOpen=false;
        } else {
            TickTrackAnimator.expandFabMenu(activity, timerPlusFab, normalTimerFab, quickTimerFab, timerText, quickTimerText);
            isOptionsOpen=true;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_timer, container, false);
        timerDiscardFAB = root.findViewById(R.id.timerCreateFragmentDiscardFAB);
        quickTimerFab = root.findViewById(R.id.quickTimerFragmentFAB);
        normalTimerFab = root.findViewById(R.id.normalTimerFragmentFAB);
        timerPlusFab = root.findViewById(R.id.multiple_actions);
        timerText = root.findViewById(R.id.timerFragmentTimerTextFab);
        quickTimerText = root.findViewById(R.id.timerFragmentQuickTimerTextFab);
        activity = getActivity();

        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        isOptionsOpen = false;
        TickTrackAnimator.collapseFabMenu(timerPlusFab, normalTimerFab, quickTimerFab, timerText, quickTimerText);
    }

    public static void onRootLayoutClick() {
        if(isOptionsOpen){
            TickTrackAnimator.collapseFabMenu(timerPlusFab, normalTimerFab, quickTimerFab, timerText, quickTimerText);
            isOptionsOpen = false;
        }
    }
}