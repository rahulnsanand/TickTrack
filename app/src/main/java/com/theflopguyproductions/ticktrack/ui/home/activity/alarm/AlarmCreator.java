package com.theflopguyproductions.ticktrack.ui.home.activity.alarm;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.dialogs.CounterCreator;
import com.theflopguyproductions.ticktrack.ui.dialogs.GetAlarmLabelDialog;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class AlarmCreator extends AppCompatActivity {

    ImageButton alarmBack, calendarDownButton, calendarUpButton, saveButton;
    ConstraintLayout alarmLabelButton, alarmRingtoneButton, alarmVibrateLayout;
    static TextView tomorrowRingText, nextOccurrence, repeatLabel, alarmLabel;
    ChipGroup dayChipGroup;
    CalendarView calendarView;
    ArrayList<Date> selectedDates;
    Map<Integer, Boolean> repeatDays;
    TimePicker timePicker;
    int timePickerHour, timePickerMinute;
    boolean isVibrateOn;
    CheckBox vibrateCheckBox;
    public static int hour, minute;
    SimpleDateFormat format = new SimpleDateFormat("EE, d MMMM, ''yy");
    String showCaseTimeChosen = "";

    ToggleButton sundayCheck, mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck, saturdayCheck;

    public static void yesToDelete(String text) {
        alarmLabel.setText(text);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_creator);

        alarmBack = findViewById(R.id.alarmActivityBackButton);
        alarmBack.setOnClickListener(view -> finish());
        calendarDownButton = findViewById(R.id.calendarDownButton);
        calendarUpButton = findViewById(R.id.calendarUpButton);
        calendarUpButton.setVisibility(View.GONE);
        calendarView = findViewById(R.id.calendarView);
        calendarView.setMinimumDate(yesterday());
        nextOccurrence = findViewById(R.id.calendarNextOccurence);
        dayChipGroup = findViewById(R.id.daysRepeatChipGroup);
        dayChipGroup.setVisibility(View.VISIBLE);
        tomorrowRingText = findViewById(R.id.alarmNextOccurenceText);
        sundayCheck = findViewById(R.id.sundayToggle);
        mondayCheck = findViewById(R.id.mondayToggle);
        tuesdayCheck = findViewById(R.id.tuesdayToggle);
        wednesdayCheck = findViewById(R.id.wednesdayToggle);
        thursdayCheck = findViewById(R.id.thursdayToggle);
        fridayCheck = findViewById(R.id.fridayToggle);
        saturdayCheck = findViewById(R.id.saturdayToggle);
        saveButton = findViewById(R.id.saveButton);
        timePicker = findViewById(R.id.timePicker);
        repeatLabel = findViewById(R.id.repeatText);
        vibrateCheckBox = findViewById(R.id.vibrateCheckBox);
        isVibrateOn = vibrateCheckBox.isChecked();
        alarmLabel = findViewById(R.id.alarmLabel);
        alarmLabel.setText("No label set");
        alarmLabelButton = findViewById(R.id.alarmLabelBackground);
        alarmRingtoneButton = findViewById(R.id.ringtoneBackground);
        alarmVibrateLayout = findViewById(R.id.vibrateBackground);

        repeatDays = new HashMap<>();

        CompoundButton.OnCheckedChangeListener toggleListener = (compoundButton, b) -> {
                    switch(compoundButton.getId()) {
                        case R.id.sundayToggle:
                            setRepeatDays(compoundButton, Calendar.SUNDAY);
                            break;
                        case R.id.mondayToggle:
                            setRepeatDays(compoundButton, Calendar.MONDAY);
                            break;
                        case R.id.tuesdayToggle:
                            setRepeatDays(compoundButton, Calendar.TUESDAY);
                            break;
                        case R.id.wednesdayToggle:
                            setRepeatDays(compoundButton, Calendar.WEDNESDAY);
                            break;
                        case R.id.thursdayToggle:
                            setRepeatDays(compoundButton, Calendar.THURSDAY);
                            break;
                        case R.id.fridayToggle:
                            setRepeatDays(compoundButton, Calendar.FRIDAY);
                            break;
                        case R.id.saturdayToggle:
                            setRepeatDays(compoundButton, Calendar.SATURDAY);
                            break;
                        default:
                            return;
                    }
                };

        timePicker.setOnTimeChangedListener((timePicker, hourOfDay, minuteOfDay) -> {
            showCaseTimeChosen = hourOfDay%12+":"+minuteOfDay+((hourOfDay>=12) ? " pm" : " am");
            timePickerHour = hourOfDay;
            timePickerMinute = minuteOfDay;
            tomorrowRingText.animate().alpha(0.0f).setDuration(60).withEndAction(() -> {
                tomorrowRingText.setText(getNextOccurrence());
                tomorrowRingText.animate().alpha(1.0f).setDuration(60);
            });
        });

        sundayCheck.setOnCheckedChangeListener(toggleListener);
        mondayCheck.setOnCheckedChangeListener(toggleListener);
        tuesdayCheck.setOnCheckedChangeListener(toggleListener);
        wednesdayCheck.setOnCheckedChangeListener(toggleListener);
        thursdayCheck.setOnCheckedChangeListener(toggleListener);
        fridayCheck.setOnCheckedChangeListener(toggleListener);
        saturdayCheck.setOnCheckedChangeListener(toggleListener);

        calendarDownButton.setOnClickListener(view -> {
            showCalendar();
        });

        calendarUpButton.setOnClickListener(view -> {
            selectedDates=new ArrayList<>();
            if(calendarView.getSelectedDates().size()>0){
                for (Calendar calendar : calendarView.getSelectedDates()) {
                    System.out.println(calendar.getTime().toString());
                    selectedDates.add(calendar.getTime());
                    showOccurrence();
                }
                nextOccurrence.setText("Next ring: "+format.format(selectedDates.get(0)));
            }
            else{
                hideCalendar();
            }
        });

        saveButton.setOnClickListener(view -> {
            //Save Instance
        });

        alarmLabelButton.setOnClickListener(view -> showLabelDialog());

        alarmRingtoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        alarmVibrateLayout.setOnClickListener(view -> toggleCheckBox());
        vibrateCheckBox.setOnCheckedChangeListener((compoundButton, b) -> isVibrateOn = compoundButton.isChecked());
    }

    private boolean isRepeating(){
        return mondayCheck.isChecked() && tuesdayCheck.isChecked() && wednesdayCheck.isChecked() &&
                thursdayCheck.isChecked() && fridayCheck.isChecked() && saturdayCheck.isChecked() && sundayCheck.isChecked();
    }

    private void toggleCheckBox() {
        if(isVibrateOn){
            vibrateCheckBox.setChecked(false);
        }
        else{
            vibrateCheckBox.setChecked(true);
        }
    }

    public void showLabelDialog() {
        GetAlarmLabelDialog dialog = new GetAlarmLabelDialog(this);
        dialog.show();
    }

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

    private void setRepeatDays(CompoundButton compoundButton, int day){
        if(compoundButton.isChecked()){
            repeatDays.put(day,true);
        }
        else{
            repeatDays.put(day,false);
        }
        tomorrowRingText.animate().alpha(0.0f).setDuration(60).withEndAction(() -> {
            tomorrowRingText.setText(getNextOccurrence());
            tomorrowRingText.animate().alpha(1.0f).setDuration(60);
        });
    }

    private ArrayList<Integer> getRepeatDays(){
        ArrayList<Integer> onDays = new ArrayList<>();
        for(int day : repeatDays.keySet()){
            if(repeatDays.get(day)!=null){
                if(repeatDays.get(day)){
                    onDays.add(day);
                }
            }
        }
        return onDays;
    }

    private void showOccurrence() {
        repeatDays.clear();
        resetRepeatDays();
        repeatLabel.setText("Custom");
        calendarView.setVisibility(View.GONE);

        calendarUpButton.setVisibility(View.INVISIBLE);
        calendarDownButton.setVisibility(View.VISIBLE);
        nextOccurrence.setVisibility(View.VISIBLE);
        dayChipGroup.setVisibility(View.GONE);
    }
    private void hideCalendar() {
        repeatLabel.setText("Repeat");
        calendarView.setVisibility(View.GONE);

        calendarUpButton.setVisibility(View.INVISIBLE);
        calendarDownButton.setVisibility(View.VISIBLE);
        tomorrowRingText.setVisibility(View.VISIBLE);
        nextOccurrence.setVisibility(View.GONE);
        dayChipGroup.setVisibility(View.VISIBLE);
    }
    private void showCalendar(){
        calendarView.setVisibility(View.VISIBLE);

        calendarDownButton.setVisibility(View.INVISIBLE);
        calendarUpButton.setVisibility(View.VISIBLE);
        nextOccurrence.setVisibility(View.GONE);
        dayChipGroup.setVisibility(View.GONE);
    }

    private String getNextOccurrence(){
        Calendar today = Calendar.getInstance();
        Date date = new Date();
        today.setTime(date);
        int dayNumber = today.get(Calendar.DAY_OF_WEEK);
        ArrayList<Integer> receivedOnDays = getRepeatDays();
        Collections.sort(receivedOnDays);

        if(receivedOnDays.size()>0){
            if(receivedOnDays.contains(dayNumber)){
                if(isToday(timePickerHour, timePickerMinute)){
                    return "This alarm will ring today at "+timePickerHour%12+":"+timePickerMinute+" "+((timePickerHour>=12) ? "pm" : "am");
                }
                else{
                    if(dayNumber==Collections.max(receivedOnDays)){
                        return "This alarm will ring next "+getDay(Collections.min(receivedOnDays));
                    }
                    else{
                        return "This alarm will ring next "+getDay(nextOccurrenceRight(receivedOnDays,dayNumber));
                    }
                }
            }
            else{
                if(dayNumber>Collections.max(receivedOnDays)){
                    return "This alarm will ring next "+getDay(Collections.min(receivedOnDays));
                }
                else if(dayNumber<Collections.max(receivedOnDays) && dayNumber>Collections.min(receivedOnDays)){
                    return "This alarm will ring next "+getDay(nextOccurrenceRight(receivedOnDays,dayNumber));
                }
                else if(dayNumber<Collections.min(receivedOnDays)){
                    return "This alarm will ring next "+getDay(Collections.min(receivedOnDays));
                }
            }
        }
        else{
            if(!isRepeating()){
                if(isToday(timePickerHour, timePickerMinute)){
                    return "This alarm will ring today";
                }
            }
        }
        return "This alarm will ring tomorrow";
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

    private void resetRepeatDays(){
        sundayCheck.setChecked(false);
        mondayCheck.setChecked(false);
        tuesdayCheck.setChecked(false);
        wednesdayCheck.setChecked(false);
        thursdayCheck.setChecked(false);
        fridayCheck.setChecked(false);
        saturdayCheck.setChecked(false);
    }

    private Calendar yesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public String getDay(int i){
        if(i==1)
            return "Sunday";
        else if(i==2)
            return "Monday";
        else if(i==3)
            return "Tuesday";
        else if(i==4)
            return "Wednesday";
        else if(i==5)
            return "Thursday";
        else if(i==6)
            return "Friday";
        else if(i==7)
            return "Saturday";
        else
            return "";
    }

}