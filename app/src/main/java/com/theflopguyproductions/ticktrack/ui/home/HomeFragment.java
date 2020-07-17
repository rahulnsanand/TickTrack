package com.theflopguyproductions.ticktrack.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.dialogs.AlarmDelete;
import com.theflopguyproductions.ticktrack.ui.home.activity.alarm.AlarmCreator;
import com.theflopguyproductions.ticktrack.ui.utils.deletehelper.AlarmSlideDeleteHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.os.Looper.getMainLooper;

public class HomeFragment extends Fragment implements AlarmSlideDeleteHelper.RecyclerItemTouchHelperListener {

    private TextView timeTextBig;
    final Handler someHandler = new Handler(getMainLooper());
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mma E, d MMMM ''yy");

    public static boolean isEnabled;
    public static boolean isDisabled;
    private static Vibrator hapticFeed;

    public boolean isEnabled() {
        return isEnabled;
    }


    public boolean isDisabled() {
        return isDisabled;
    }

    private static final String ENABLED_PARAM = "param1";
    private static final String DISABLED_PARAM = "param2";

    public HomeFragment() {
    }

    public static HomeFragment newInstance(boolean enabled, boolean disabled) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putBoolean(ENABLED_PARAM, enabled);
        args.putBoolean(DISABLED_PARAM, disabled);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isEnabled = getArguments().getBoolean(ENABLED_PARAM);
            isDisabled = getArguments().getBoolean(DISABLED_PARAM);
        }
    }

    private static ArrayList<AlarmData> alarmDataArrayList = new ArrayList<>();
    private static AlarmAdapter alarmAdapter;
    private RecyclerView alarmRecyclerView;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        timeTextBig = root.findViewById(R.id.expandedTimeText);
        alarmRecyclerView = root.findViewById(R.id.alarmRecyclerView);
        hapticFeed = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);

        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeTextBig.setText(sdf.format(System.currentTimeMillis()));
                someHandler.postDelayed(this, 1000);
            }
        }, 0);

        buildRecyclerView();

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new AlarmSlideDeleteHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(alarmRecyclerView);

        return root;
    }

    private void buildRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        alarmRecyclerView.setItemAnimator(new DefaultItemAnimator());
        alarmRecyclerView.setHasFixedSize(true);

//        Collections.sort(alarmDataArrayList);

        alarmAdapter = new AlarmAdapter(alarmDataArrayList);
        alarmRecyclerView.setLayoutManager(layoutManager);
        alarmRecyclerView.setAdapter(alarmAdapter);
        alarmAdapter.notifyDataSetChanged();
    }

    public void fabClicked() {
        Intent intent = new Intent(getContext(), AlarmCreator.class);
        startActivity(intent);
        Toast.makeText(getContext(),"Add Alarm",Toast.LENGTH_SHORT).show();
    }

    public static void onSaveAlarm(int alarmHour, int alarmMinute, int alarmTheme, ArrayList<Calendar> calendarRepeatDays,
                                   ArrayList<Calendar> calendarRepeatWeeks, Uri alarmRingTone, String alarmLabel, boolean alarmVibrate){

        AlarmData alarmData = new AlarmData();
        alarmData.setAlarmHour(alarmHour);
        alarmData.setAlarmMinute(alarmMinute);
        alarmData.setAlarmTheme(alarmTheme);
        alarmData.setCalendarRepeatDays(calendarRepeatDays);
        alarmData.setCalendarRepeatWeeks(calendarRepeatWeeks);
        alarmData.setAlarmRingTone(alarmRingTone);
        alarmData.setAlarmLabel(alarmLabel);
        alarmData.setAlarmVibrate(alarmVibrate);
        alarmDataArrayList.add(0,alarmData);
        alarmAdapter.notifyData(alarmDataArrayList);

        storeAlarm();
    }

    private static void storeAlarm() {



    }


    String deletedAlarm = null;
    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AlarmAdapter.RecyclerItemViewHolder) {
            deletedAlarm = alarmDataArrayList.get(viewHolder.getAdapterPosition()).getAlarmLabel();
            position = viewHolder.getAdapterPosition();

            AlarmDelete alarmDelete = new AlarmDelete(getActivity(), position, deletedAlarm, viewHolder);
            alarmDelete.show();

        }
    }
    public static void yesToDelete(int position, Activity activity, String alarmLabel) {
        deleteItem(position);
        Toast.makeText(activity, "Deleted alarm " + alarmLabel, Toast.LENGTH_SHORT).show();
    }
    public static void noToDelete(RecyclerView.ViewHolder viewHolder) {
        alarmAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
    }
    public static void deleteItem(int position){
        alarmDataArrayList.remove(position);
        //StoreCounter();
        hapticFeed.vibrate(50);
        alarmAdapter.notifyData(alarmDataArrayList);
    }


}