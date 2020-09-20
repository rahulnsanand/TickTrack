package com.theflopguyproductions.ticktrack.ui.timer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.dialogs.DeleteTimer;
import com.theflopguyproductions.ticktrack.timer.data.TimerAdapter;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerAdapter;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerData;
import com.theflopguyproductions.ticktrack.ui.utils.deletehelper.TimerSlideDeleteHelper;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

import java.util.ArrayList;
import java.util.Collections;

public class TimerRecyclerFragment extends Fragment implements TimerSlideDeleteHelper.RecyclerItemTouchHelperListener {

    private static TickTrackDatabase tickTrackDatabase;

    private static ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private static ArrayList<QuickTimerData> quickTimerDataArrayList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private Activity activity;
    private static RecyclerView timerRecyclerView, quickTimerRecyclerView;
    private TextView noTimerText, timerTitleText, quickTimerTitleText;
    private static ConstraintLayout timerRecyclerRootLayout, timerLayout, quickTimerLayout;
    private static TimerAdapter timerAdapter;
    private QuickTimerAdapter quickTimerAdapter;

    public static void deleteTimer(int timerId, int position, Activity activity, String timerName) {
        deleteItem(timerId, position);
        if(!"Set label".equals(timerName)){
            Toast.makeText(activity, "Deleted Timer " + timerName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Deleted Timer", Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteItem(int timerId, int position){
        timerAdapter.notifyItemRemoved(position);
        for(int i=0; i<timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).getTimerIntID()==timerId){
                timerDataArrayList.remove(i);
                tickTrackDatabase.storeTimerList(timerDataArrayList);
                return;
            }
        }

    }

    public static void refreshRecyclerView() {
        timerAdapter.notifyDataSetChanged();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timer_recycler, container, false);
        activity = getActivity();

        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();
        timerRecyclerView = root.findViewById(R.id.timerFragmentRecyclerView);
        quickTimerRecyclerView = root.findViewById(R.id.quickTimerFragmentRecyclerView);
        timerLayout= root.findViewById(R.id.timerRootLayout);
        quickTimerLayout= root.findViewById(R.id.quickTimerRootLayout);
        timerTitleText = root.findViewById(R.id.timerFragmentTimerTitleText);
        quickTimerTitleText = root.findViewById(R.id.timerFragmentQuickTimerTitleText);

        noTimerText = root.findViewById(R.id.timerFragmentNoTimerText);
        timerRecyclerRootLayout = root.findViewById(R.id.timerRecyclerRootLayout);
        timerRecyclerRootLayout.setOnClickListener(view -> rootLayoutClickListener.onRootLayoutClick());
        timerRecyclerView.setOnClickListener(view -> rootLayoutClickListener.onRootLayoutClick());

        TickTrackThemeSetter.timerRecycleTheme(activity, timerRecyclerView, tickTrackDatabase, timerRecyclerRootLayout, noTimerText);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new TimerSlideDeleteHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(timerRecyclerView);

        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        buildQuickTimerRecyclerView(activity);
        buildTimerRecyclerView(activity);
        return root;
    }

    private void buildQuickTimerRecyclerView(Activity activity) {

        quickTimerAdapter = new QuickTimerAdapter(activity, quickTimerDataArrayList);

        System.out.println("BUILD HAPPENED");

        if(quickTimerDataArrayList.size()>0){

            quickTimerRecyclerView.setVisibility(View.VISIBLE);
            quickTimerTitleText.setVisibility(View.VISIBLE);
            quickTimerLayout.setVisibility(View.VISIBLE);
            timerTitleText.setVisibility(View.VISIBLE);

            Collections.sort(quickTimerDataArrayList);
            tickTrackDatabase.storeQuickTimerList(quickTimerDataArrayList);

            quickTimerRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            quickTimerRecyclerView.setItemAnimator(new DefaultItemAnimator());
            quickTimerRecyclerView.setAdapter(quickTimerAdapter);

            quickTimerAdapter.diffUtilsChangeData(quickTimerDataArrayList);

        } else {
            quickTimerRecyclerView.setVisibility(View.GONE);
            quickTimerTitleText.setVisibility(View.GONE);
            quickTimerLayout.setVisibility(View.GONE);
            timerTitleText.setVisibility(View.GONE);
        }

    }
    private void buildTimerRecyclerView(Activity activity) {

        timerAdapter = new TimerAdapter(activity, timerDataArrayList);

        if(timerDataArrayList.size()>0){
            timerRecyclerView.setVisibility(View.VISIBLE);
            noTimerText.setVisibility(View.INVISIBLE);

            Collections.sort(timerDataArrayList);
            tickTrackDatabase.storeTimerList(timerDataArrayList);

            timerRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            timerRecyclerView.setItemAnimator(new DefaultItemAnimator());
            timerRecyclerView.setAdapter(timerAdapter);

            timerAdapter.diffUtilsChangeData(timerDataArrayList);

        } else {
            timerRecyclerView.setVisibility(View.INVISIBLE);
            noTimerText.setVisibility(View.VISIBLE);
        }

    }

    String deletedTimer = null;
    int timerId = -1;
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof TimerAdapter.timerDataViewHolder) {
            deletedTimer = timerDataArrayList.get(position).getTimerLabel();
            timerId = timerDataArrayList.get(position).getTimerIntID();
            new Handler().post(() -> {
                DeleteTimer timerDelete = new DeleteTimer(activity);
                timerDelete.show();
                if(!"Set label".equals(deletedTimer)){
                    timerDelete.dialogMessage.setText("Delete timer "+deletedTimer+"?");
                } else {
                    timerDelete.dialogMessage.setText("Delete timer ?");
                }
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
                timerDelete.yesButton.setOnClickListener(view -> {
                    TimerRecyclerFragment.deleteTimer(timerId, position, activity, deletedTimer);
                    timerDelete.cancel();
                    if(timerDataArrayList.size()>0){
                        timerRecyclerView.setVisibility(View.VISIBLE);
                        noTimerText.setVisibility(View.INVISIBLE);
                    } else {
                        timerRecyclerView.setVisibility(View.INVISIBLE);
                        noTimerText.setVisibility(View.VISIBLE);
                    }
                    buildTimerRecyclerView(activity);
                    sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
                });
                timerDelete.noButton.setOnClickListener(view -> {
                    TimerRecyclerFragment.refreshRecyclerView();
                    timerDelete.cancel();
                    buildTimerRecyclerView(activity);
                    sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
                });
                timerDelete.setOnCancelListener(dialogInterface -> {
                    TimerRecyclerFragment.refreshRecyclerView();
                    timerDelete.cancel();
                    buildTimerRecyclerView(activity);
                    sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
                });
            });
        }

    }




    @Override
    public void onStop() {
        super.onStop();
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        quickTimerRecyclerView.setAdapter(new QuickTimerAdapter(activity, quickTimerDataArrayList));
        timerRecyclerView.setAdapter(new TimerAdapter(activity, timerDataArrayList));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPreferences = tickTrackDatabase.getSharedPref(activity);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        timerAdapter.notifyDataSetChanged();
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        if (s.equals("TimerData") || s.equals("QuickTimerData")){
            timerDataArrayList = tickTrackDatabase.retrieveTimerList();
            quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();

            if(timerDataArrayList.size()>0){
                timerRecyclerView.setAdapter(new TimerAdapter(activity, timerDataArrayList));
            }
            timerAdapter.diffUtilsChangeData(timerDataArrayList);
            if(quickTimerDataArrayList.size()>0){
                quickTimerRecyclerView.setAdapter(new QuickTimerAdapter(activity, quickTimerDataArrayList));
            }
            quickTimerAdapter.diffUtilsChangeData(quickTimerDataArrayList);
            checkTimerExists();
            System.out.println("TIMER DATA SIZE = "+quickTimerDataArrayList.size());
        }

    };

    private void checkTimerExists() {
        if(!(timerDataArrayList.size() >0)){
            noTimerText.setVisibility(View.VISIBLE);
            timerRecyclerView.setVisibility(View.GONE);
            quickTimerLayout.setVisibility(View.GONE);
            timerTitleText.setVisibility(View.GONE);
        } else {
            buildTimerRecyclerView(activity);
        }
        if(!(quickTimerDataArrayList.size() > 0)){
            quickTimerLayout.setVisibility(View.GONE);
            timerTitleText.setVisibility(View.GONE);
        } else {
            buildQuickTimerRecyclerView(activity);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        quickTimerRecyclerView.setAdapter(null);
        timerRecyclerView.setAdapter(null);
    }


    private RootLayoutClickListener rootLayoutClickListener;
    public interface RootLayoutClickListener {
        void onRootLayoutClick();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            rootLayoutClickListener = (RootLayoutClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + RootLayoutClickListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        rootLayoutClickListener = null;
    }

}