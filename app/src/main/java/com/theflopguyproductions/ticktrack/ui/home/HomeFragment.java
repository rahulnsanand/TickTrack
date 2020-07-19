package com.theflopguyproductions.ticktrack.ui.home;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.theflopguyproductions.ticktrack.utils.TickTrackAlarmManager;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
import static android.os.Looper.getMainLooper;

public class HomeFragment extends Fragment implements AlarmSlideDeleteHelper.RecyclerItemTouchHelperListener {

    private TextView timeTextBig;
    final Handler someHandler = new Handler(getMainLooper());
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mma E, d MMMM ''yy");

    private static Vibrator hapticFeed;

    private static Context context;

    public static void openEditActivity(Context staticContext, int adapterPosition) {
        Intent intent = new Intent(staticContext, EditAlarmActivity.class);
        intent.putExtra("currentAlarmPosition",adapterPosition);
        staticContext.startActivity(intent);
    }

    public static void toggleAlarmOnOff(int adapterPosition, boolean check) {
        alarmDataArrayList.get(adapterPosition).setAlarmOnOff(check);
        storeAlarm();
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

    public static void onSaveAlarm(int alarmHour, int alarmMinute, int alarmTheme, ArrayList<Calendar> weeklyRepeat,
                                   ArrayList<Integer> customRepeat, String alarmRingTone, String alarmLabel, boolean alarmVibrate,  boolean alarmOnOff){

        AlarmData alarmData = new AlarmData();
        alarmData.setAlarmHour(alarmHour);
        alarmData.setAlarmMinute(alarmMinute);
        alarmData.setAlarmTheme(alarmTheme);
        alarmData.setRepeatCustomDates(weeklyRepeat);
        alarmData.setRepeatDaysInWeek(customRepeat);
        alarmData.setAlarmRingTone(alarmRingTone);
        alarmData.setAlarmLabel(alarmLabel);
        alarmData.setAlarmVibrate(alarmVibrate);
        alarmData.setAlarmOnOff(alarmOnOff);
        alarmDataArrayList.add(0,alarmData);
        alarmAdapter.notifyData(alarmDataArrayList);
        storeAlarm();
        //setAlarm();
    }

    private static void storeAlarm() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmDataArrayList);
        editor.putString("AlarmData", json);
        editor.apply();

        //TODO SET ALARM HERE
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

            setAlarm();

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

        //TickTrackAlarmManager.cancelAlarm(position);

    }

    public static void setAlarm(){
        loadAlarmData();
        for(int i = 0; i < alarmDataArrayList.size(); i++) {

            if (alarmDataArrayList.get(i).isAlarmOnOff()) {

                int hour, minute;
                hour = alarmDataArrayList.get(i).getAlarmHour();
                minute = alarmDataArrayList.get(i).getAlarmMinute();
                ArrayList<Calendar> customDateList = alarmDataArrayList.get(i).getRepeatCustomDates();
                ArrayList<Integer> weeklyRepeat = alarmDataArrayList.get(i).getRepeatDaysInWeek();

                System.out.println("========================================================================================");

                if(customDateList.size()>0 && !(weeklyRepeat.size() >0)){
                    for (int j = 0; j < customDateList.size(); j++) {

                        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, customDateList.get(j).get(Calendar.DAY_OF_MONTH));
                        cal.set(Calendar.MONTH, customDateList.get(j).get(Calendar.MONTH));
                        cal.set(Calendar.YEAR, customDateList.get(j).get(Calendar.YEAR));
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);
                        System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                        System.out.println(">>>>>>>>>>  Day     "+cal.get(Calendar.DAY_OF_MONTH));
                        System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                        System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                        System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                        System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));

                    }
                }
                else if(weeklyRepeat.size()>0 && !(customDateList.size() >0)){
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DATE,nextOccurrenceText(hour, minute, weeklyRepeat));

                    System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                    System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                    System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                    System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                    System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));
                }
                else{
                    if(isToday(hour,minute)){
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DATE,getToday());
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);

                        System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                        System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                        System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                        System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                        System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));
                    } else{
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DATE,getTomorrow());
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);

                        System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                        System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                        System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                        System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                        System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));
                    }
                }

                System.out.println("========================================================================================");
            }
        }
    }
    private static int getToday() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }
    private static int nextOccurrenceText(int setHour, int setMinute, ArrayList<Integer> receivedOnDays){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY,setHour);
        today.set(Calendar.MINUTE,setMinute);
        int dayNumber = today.get(Calendar.DAY_OF_WEEK);
        Collections.sort(receivedOnDays);

        if(receivedOnDays.size()>0){
            if(receivedOnDays.contains(dayNumber)){
                if(isToday(setHour, setMinute)){
                    return today.get(Calendar.DATE);
                } else{
                    if(dayNumber==Collections.max(receivedOnDays)){
                        today.set(Calendar.DAY_OF_WEEK, Collections.min(receivedOnDays));
                        return today.get(Calendar.DATE)+7;
                    } else{
                        today.set(Calendar.DAY_OF_WEEK, nextOccurrenceRight(receivedOnDays,dayNumber));
                        return today.get(Calendar.DATE);
                    }
                }
            } else{
                if(dayNumber>Collections.max(receivedOnDays)){
                    today.set(Calendar.DAY_OF_WEEK, Collections.min(receivedOnDays));
                    return today.get(Calendar.DATE);
                } else if(dayNumber<Collections.max(receivedOnDays) && dayNumber>Collections.min(receivedOnDays)){
                    today.set(Calendar.DAY_OF_WEEK, nextOccurrenceRight(receivedOnDays,dayNumber));
                    return today.get(Calendar.DATE);
                } else if(dayNumber<Collections.min(receivedOnDays)){
                    today.set(Calendar.DAY_OF_WEEK, Collections.min(receivedOnDays));
                    return today.get(Calendar.DATE);
                }
            }
        } else{
            if(isToday(setHour, setMinute)){
                return today.get(Calendar.DATE);
            }
        }
        return today.get(Calendar.DATE)+1;
    }
    private static boolean isToday(int hour, int minute){
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int time24 = calendar.get(Calendar.HOUR_OF_DAY);
        int timeMinute = calendar.get(Calendar.MINUTE);

        if(time24<hour){
            return true;
        }
        else if(time24==hour){
            return timeMinute < minute;
        }
        return false;
    }
    private static int getTomorrow(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE)+1;
    }
    private static int nextOccurrenceRight(ArrayList<Integer> receivedDays, int today){
        int nextOccurrenceValue=0;
        int checkCount = 0;

        if(receivedDays!=null){
            for(int i = 0; i < receivedDays.size(); i++){
                while(today<receivedDays.get(i) && checkCount==0){
                    nextOccurrenceValue=receivedDays.get(i);
                    checkCount++;
                }
            }
        }
        return nextOccurrenceValue;
    }

}