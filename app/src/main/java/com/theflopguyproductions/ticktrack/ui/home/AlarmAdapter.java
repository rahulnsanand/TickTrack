package com.theflopguyproductions.ticktrack.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.MainActivityToChange;
import com.theflopguyproductions.ticktrack.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.RecyclerItemViewHolder> {

    private ArrayList<AlarmData> myList;
    int mLastPosition = 0;

    public AlarmAdapter(ArrayList<AlarmData> myList) {
        this.myList = myList;
    }

    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item_layout, parent, false);
        RecyclerItemViewHolder holder = new RecyclerItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, final int position) {

        holder.alarmTime.setText(getHour12(myList.get(position).getAlarmHour())+":"+getMinute12(myList.get(position).getAlarmMinute()));
        if(myList.get(position).getAlarmLabel().equals("Set alarm label")){
            holder.alarmLabel.setText("");
        }
        else{
            holder.alarmLabel.setText(myList.get(position).getAlarmLabel());
        }
        holder.alarmMode.setChecked(true);

        String alarmAmPm = ((myList.get(position).getAlarmHour()>=12) ? "pm" : "am");
        holder.alarmAMPM.setText(alarmAmPm);

        holder.alarmOccurrence.setText("next occurrence");

        setColor(holder, myList.get(position).getAlarmTheme());
        mLastPosition = position;
    }

    private int nextOccurrenceRight(ArrayList<Integer> receivedDays, int today){
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

//    private String getNextOccurrence(ArrayList<Calendar> calendarRepeatDays, ArrayList<Calendar> calendarRepeatWeeks){
//
//        if(calendarRepeatDays.size()>0 && !(calendarRepeatWeeks.size() >0)){
//                //CUSTOM ALARM OCCURRENCE
//        }
//        else if(calendarRepeatWeeks.size()>0 && !(calendarRepeatDays.size() >0)){
//            //WEEKLY ALARM OCCURRENCE
//        }
//        else{
//
//        }
//    }

    private boolean isToday(int hour, int minute){
        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int time24 = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        int time12 = calendar.get(Calendar.HOUR);        // gets hour in 12h format
        int timeMinute = calendar.get(Calendar.MINUTE);

        if(time24<hour){
            return true;
        }
        else if(time24==hour){
            return timeMinute < minute;
        }
        return false;
    }


    private String getHour12(int hour){
        if(hour<10 && hour !=0){
            return "0"+hour;
        }
        if(hour>12 && hour<22){
            return "0"+hour%12;
        }
        if(hour==12){
            return ""+hour;
        }
        if(hour==24){
            return ""+12;
        }
        if(hour==0){
            return ""+12;
        }
        return ""+hour%12;
    }

    private static String getMinute12(int minute){
        if(minute<10){
            return "0"+minute;
        }
        return ""+minute;
    }

    private static final SimpleDateFormat timestampReadableFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private void setColor(RecyclerItemViewHolder holder, int itemColor) {
        if (itemColor == 1) {
            holder.alarmLayout.setBackgroundResource(R.drawable.round_rect_blue);
        }
        if (itemColor == 2) {
            holder.alarmLayout.setBackgroundResource(R.drawable.round_rect_black);
        }
        if (itemColor == 3) {
            holder.alarmLayout.setBackgroundResource(R.drawable.round_rect_green);
        }
        if (itemColor == 4) {
            holder.alarmLayout.setBackgroundResource(R.drawable.round_rect_purple);
        }
        if (itemColor == 0) {
            holder.alarmLayout.setBackgroundResource(R.drawable.round_rect_default);
        }
    }

    @Override
    public int getItemCount() {
        return (null != myList ? myList.size() : 0);
    }

    public void notifyData(ArrayList<AlarmData> myList) {
        this.myList = myList;
        notifyDataSetChanged();
    }

    public static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView alarmTime;
        private final TextView alarmLabel;
        private final TextView alarmOccurrence;
        private final TextView alarmAMPM;
        private final Switch alarmMode;

        public ConstraintLayout alarmLayout;
        public ConstraintLayout alarmBackgroundLayout;

        public RecyclerItemViewHolder(final View parent) {
            super(parent);

            alarmLabel = parent.findViewById(R.id.alarmLabel);
            alarmTime = parent.findViewById(R.id.alarmTime);
            alarmOccurrence = parent.findViewById(R.id.alarmOccurence);
            alarmAMPM = parent.findViewById(R.id.amPmText);
            alarmMode = parent.findViewById(R.id.alarmSwitch);

            alarmLayout = parent.findViewById(R.id.alarmLayout);
            alarmBackgroundLayout = parent.findViewById(R.id.alarmBackgroundLayout);

            alarmLayout.setOnClickListener(v -> {
                MainActivityToChange.editAlarmActivity(getAdapterPosition());
                Toast.makeText(itemView.getContext(), "Position:" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
