package com.theflopguyproductions.ticktrack.ui.home.activity.alarm;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlarmCreator extends AppCompatActivity {

    ImageButton alarmBack, calendarDownButton, calendarUpButton;
    Button alarmSave;
    TextView tomorrowRingText, nextOccurrence;
    ChipGroup dayChipGroup;
    CalendarView calendarView;
    ArrayList<Date> selectedDates;
    Map<String, Boolean> repeatDays;
    public static int hour, minute;
    SimpleDateFormat format = new SimpleDateFormat("EE, d MMMM, ''yy");

    ToggleButton sundayCheck, mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck, saturdayCheck;

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

        repeatDays = new HashMap<>();

        CompoundButton.OnCheckedChangeListener toggleListener = (compoundButton, b) -> {
                    switch(compoundButton.getId()) {
                        case R.id.sundayToggle:
                            setRepeatDays(compoundButton, "Sunday");
                            break;
                        case R.id.mondayToggle:
                            setRepeatDays(compoundButton, "Monday");
                            break;
                        case R.id.tuesdayToggle:
                            setRepeatDays(compoundButton, "Tuesday");
                            break;
                        case R.id.wednesdayToggle:
                            setRepeatDays(compoundButton, "Wednesday");
                            break;
                        case R.id.thursdayToggle:
                            setRepeatDays(compoundButton, "Thursday");
                            break;
                        case R.id.fridayToggle:
                            setRepeatDays(compoundButton, "Friday");
                            break;
                        case R.id.saturdayToggle:
                            setRepeatDays(compoundButton, "Saturday");
                            break;
                        default:
                            return;
                    }
                };

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
    }


    private void setRepeatDays(CompoundButton compoundButton, String day){
        if(compoundButton.isChecked()){
            repeatDays.put(day,true);
        }
        else{
            repeatDays.put(day,false);
        }
        getRepeatDays();
    }

    private void getRepeatDays(){
        for(String day : repeatDays.keySet()){
            if(repeatDays.get(day)!=null){
                if(repeatDays.get(day)){
                    System.out.println(day+":"+repeatDays.get(day));
                }
            }
        }
    }


    private void showOccurrence() {
        repeatDays.clear();
        resetRepeatDays();
        nextOccurrence.setVisibility(View.VISIBLE);
        dayChipGroup.setVisibility(View.GONE);
        calendarView.setVisibility(View.GONE);
        calendarUpButton.setVisibility(View.GONE);
        calendarDownButton.setVisibility(View.VISIBLE);
        tomorrowRingText.setVisibility(View.INVISIBLE);
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

    private void hideCalendar() {
        nextOccurrence.setVisibility(View.GONE);
        dayChipGroup.setVisibility(View.VISIBLE);
        calendarView.setVisibility(View.GONE);
        calendarUpButton.setVisibility(View.GONE);
        calendarDownButton.setVisibility(View.VISIBLE);
        tomorrowRingText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void showCalendar(){
        nextOccurrence.setVisibility(View.GONE);
        dayChipGroup.setVisibility(View.GONE);
        calendarView.setVisibility(View.VISIBLE);
        calendarDownButton.setVisibility(View.GONE);
        calendarUpButton.setVisibility(View.VISIBLE);
    }
}