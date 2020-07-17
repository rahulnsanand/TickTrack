package com.theflopguyproductions.ticktrack.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterData;
import com.theflopguyproductions.ticktrack.ui.counter.activity.counter.CounterActivity;
import com.theflopguyproductions.ticktrack.ui.dialogs.AlarmDelete;
import com.theflopguyproductions.ticktrack.ui.home.activity.alarm.CreateAlarmActivity;
import com.theflopguyproductions.ticktrack.ui.home.activity.alarm.EditAlarmActivity;
import com.theflopguyproductions.ticktrack.ui.utils.deletehelper.AlarmSlideDeleteHelper;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
import static android.os.Looper.getMainLooper;

public class HomeFragment extends Fragment implements AlarmSlideDeleteHelper.RecyclerItemTouchHelperListener {

    private TextView timeTextBig;
    final Handler someHandler = new Handler(getMainLooper());
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mma E, d MMMM ''yy");

    public static boolean isEnabled;
    public static boolean isDisabled;
    private static Vibrator hapticFeed;

    private static Context context;

    public static void openEditActivity(Context staticContext, int adapterPosition) {
        Intent intent = new Intent(staticContext, EditAlarmActivity.class);
        intent.putExtra("currentAlarmPosition",adapterPosition);
        staticContext.startActivity(intent);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        loadAlarmData();
        buildRecyclerView();
    }

    private static ArrayList<AlarmData> alarmDataArrayList;
    private static AlarmAdapter alarmAdapter;
    private RecyclerView alarmRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
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
        loadAlarmData();
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
        Intent intent = new Intent(getContext(), CreateAlarmActivity.class);
        startActivity(intent);
        Toast.makeText(getContext(),"Add Alarm",Toast.LENGTH_SHORT).show();
    }

    public static void onSaveAlarm(int alarmHour, int alarmMinute, int alarmTheme, ArrayList<Date> calendarRepeatDays,
                                   ArrayList<Integer> calendarRepeatWeeks, String alarmRingTone, String alarmLabel, boolean alarmVibrate){

        AlarmData alarmData = new AlarmData();
        alarmData.setAlarmHour(alarmHour);
        alarmData.setAlarmMinute(alarmMinute);
        alarmData.setAlarmTheme(alarmTheme);
        alarmData.setRepeatCustomDates(calendarRepeatDays);
        alarmData.setRepeatDaysInWeek(calendarRepeatWeeks);
        alarmData.setAlarmRingTone(alarmRingTone);
        alarmData.setAlarmLabel(alarmLabel);
        alarmData.setAlarmVibrate(alarmVibrate);
        alarmDataArrayList.add(0,alarmData);
        alarmAdapter.notifyData(alarmDataArrayList);
        storeAlarm();
    }

    private static void storeAlarm() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmDataArrayList);
        editor.putString("AlarmData", json);
        editor.apply();
    }

    private static void loadAlarmData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("AlarmData", null);
        Type type = new TypeToken<ArrayList<AlarmData>>() {}.getType();
        alarmDataArrayList = gson.fromJson(json, type);

        if(alarmDataArrayList == null){
            alarmDataArrayList = new ArrayList<>();
        }
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
        storeAlarm();
        hapticFeed.vibrate(50);
        alarmAdapter.notifyData(alarmDataArrayList);
    }

}