package com.theflopguyproductions.ticktrack.ui.home.activity.alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.dialogs.GetAlarmLabelDialog;
import com.theflopguyproductions.ticktrack.ui.dialogs.YesNoDialog;
import com.theflopguyproductions.ticktrack.ui.home.HomeFragment;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.CalendarView;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.EventDay;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.listeners.OnDayClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class CreateAlarmActivity extends AppCompatActivity {


    static int alarmSaveHour;
    static int alarmSaveMinute;
    static int alarmSaveTheme;
    static ArrayList<Date> repeatCustomDates;
    static ArrayList<Integer> repeatDaysInWeek;
    static String alarmSaveRingTone;
    static String alarmSaveLabel;
    static boolean alarmSaveVibrate;
    static Context context;


    public void setLabel(String text) {
        if(text.equals("")){
            alarmLabel.setText("Set alarm label");
        }
        else{
            alarmLabel.setText(text);
        }
    }
    public String getLabel(){
        if(alarmLabel.getText().toString().equals("Set alarm label")){
            return  "";
        }
        else{
            return alarmLabel.getText().toString();
        }
    }

    private void getCurrentCustomCheck() {
        selectedDates=new ArrayList<>();
        if(calendarView.getSelectedDates().size()>0){
            for (Calendar calendar : calendarView.getSelectedDates()) {
                selectedDates.add(calendar.getTime());
                onCalendarValueChosen();
            }
        }
    }

    public void saveAlarm() {
        alarmSaveVibrate = isVibrateOn; //Works
        alarmSaveLabel = getLabel(); //Works
        alarmSaveRingTone  = alarmRingTone; //Works
        repeatDaysInWeek = getRepeatDays(); //Works
        getCurrentCustomCheck();
        repeatCustomDates = selectedDates; //Works
        alarmSaveTheme = alarmColor; //Works
        alarmSaveMinute = timePicker.getCurrentMinute(); //Works
        alarmSaveHour = timePicker.getCurrentHour(); //Works

        if(alarmSaveLabel.equals("Set alarm label")){
            alarmSaveLabel="";
        }

        HomeFragment.onSaveAlarm(alarmSaveHour,alarmSaveMinute,alarmSaveTheme,repeatCustomDates,repeatDaysInWeek,alarmSaveRingTone,alarmSaveLabel,alarmSaveVibrate, true);
        finish();
    }

    public void discardAlarm() {
        finish();
    }

    private void showYesNoDialog(){
        YesNoDialog dialog = new YesNoDialog(this);
        dialog.show();
    }
    public void showLabelDialog() {
        GetAlarmLabelDialog dialog = new GetAlarmLabelDialog(this);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_creator);
        initVariables();
        initDefaultLayoutVisible();

        initThemeChecker();

        initWeeklyToggleListener();

        initTimePickerListener();

        setValueListeners();

        saveButton.setOnClickListener(view -> {
            saveAlarm();
        });

        alarmBack.setOnClickListener(view -> {
            finish();
        });

    }

    private void setValueListeners() {
        calendarDownButton.setOnClickListener(view -> {
            showCalendar();
        });
        calendarUpButton.setOnClickListener(view -> {
            selectedDates=new ArrayList<>();
            if(calendarView.getSelectedDates().size()>0){
                for (Calendar calendar : calendarView.getSelectedDates()) {
                    selectedDates.add(calendar.getTime());
                    onCalendarValueChosen();
                }
                nextOccurrence.setText("Next ring: "+format.format(selectedDates.get(0)));
            }
            else{
                onCalendarValueNotChosen();
            }
        });
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            Calendar calendar;
            @Override
            public void onDayClick(EventDay eventDay) {
                calendar = eventDay.getCalendar();
            }
        });
        alarmVibrateLayout.setOnClickListener(view -> toggleCheckBox());
        vibrateCheckBox.setOnCheckedChangeListener((compoundButton, b) -> isVibrateOn = compoundButton.isChecked());
        alarmRingtoneButton.setOnClickListener(view -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            startActivityForResult(intent, 5);
        });
        alarmLabelButton.setOnClickListener(view -> showLabelDialog());

    }

    private void showCalendar(){
        calendarView.setVisibility(View.VISIBLE);
        calendarDownButton.setVisibility(View.INVISIBLE);
        calendarUpButton.setVisibility(View.VISIBLE);
        nextOccurrence.setVisibility(View.GONE);
        dayChipGroup.setVisibility(View.GONE);
    }

    private void onCalendarValueChosen() {
        repeatDays.clear();
        resetWeeklyRepeatChips();
        repeatLabel.setText("Custom repeat");
        calendarView.setVisibility(View.GONE);
        tomorrowRingText.setVisibility(View.INVISIBLE);
        calendarUpButton.setVisibility(View.INVISIBLE);
        calendarDownButton.setVisibility(View.VISIBLE);
        nextOccurrence.setVisibility(View.VISIBLE);
        dayChipGroup.setVisibility(View.GONE);
    }

    private void onCalendarValueNotChosen() {
        repeatLabel.setText("Repeat");
        calendarView.setVisibility(View.GONE);
        calendarUpButton.setVisibility(View.INVISIBLE);
        calendarDownButton.setVisibility(View.VISIBLE);
        tomorrowRingText.setVisibility(View.VISIBLE);
        nextOccurrence.setVisibility(View.GONE);
        dayChipGroup.setVisibility(View.VISIBLE);
    }

    private void resetWeeklyRepeatChips(){
        sundayCheck.setChecked(false);
        mondayCheck.setChecked(false);
        tuesdayCheck.setChecked(false);
        wednesdayCheck.setChecked(false);
        thursdayCheck.setChecked(false);
        fridayCheck.setChecked(false);
        saturdayCheck.setChecked(false);
    }

    private void initDefaultLayoutVisible() {

        dayChipGroup.setVisibility(View.VISIBLE);
        calendarUpButton.setVisibility(View.GONE);
        isVibrateOn = vibrateCheckBox.isChecked();
        alarmLabel.setText("Set alarm label");

    }

    private void initVariables() {
        alarmBack = findViewById(R.id.alarmActivityBackButton);
        calendarDownButton = findViewById(R.id.calendarDownButton);
        calendarUpButton = findViewById(R.id.calendarUpButton);
        calendarView = findViewById(R.id.calendarView);
        nextOccurrence = findViewById(R.id.calendarNextOccurence);
        dayChipGroup = findViewById(R.id.daysRepeatChipGroup);
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
        alarmLabel = findViewById(R.id.alarmLabel);
        alarmLabelButton = findViewById(R.id.alarmLabelBackground);
        alarmRingtoneButton = findViewById(R.id.ringtoneBackground);
        alarmVibrateLayout = findViewById(R.id.vibrateBackground);
        alarmValue = findViewById(R.id.alarmRingtoneValue);
        chipGroup = findViewById(R.id.alarmThemeGroup);
        context = this;

        repeatDays = new HashMap<>();
        alarmColor = 0;
        calendarView.setMinimumDate(yesterday());
    }

    private void initWeeklyToggleListener() {
        toggleListener = (compoundButton, b) -> {
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

        sundayCheck.setOnCheckedChangeListener(toggleListener);
        mondayCheck.setOnCheckedChangeListener(toggleListener);
        tuesdayCheck.setOnCheckedChangeListener(toggleListener);
        wednesdayCheck.setOnCheckedChangeListener(toggleListener);
        thursdayCheck.setOnCheckedChangeListener(toggleListener);
        fridayCheck.setOnCheckedChangeListener(toggleListener);
        saturdayCheck.setOnCheckedChangeListener(toggleListener);
    }

    private void initTimePickerListener() {
        timePicker.setOnTimeChangedListener((timePicker, hourOfDay, minuteOfDay) -> {
            timePickerHour = hourOfDay;
            timePickerMinute = minuteOfDay;
            tomorrowRingText.animate().alpha(0.0f).setDuration(60).withEndAction(() -> {
                tomorrowRingText.setText(nextOccurrenceText());
                tomorrowRingText.animate().alpha(1.0f).setDuration(60);
            });
        });
    }

    private void setRepeatDays(CompoundButton compoundButton, int day){
        if(compoundButton.isChecked()){
            repeatDays.put(day,true);
        }
        else{
            repeatDays.put(day,false);
        }
        tomorrowRingText.animate().alpha(0.0f).setDuration(60).withEndAction(() -> {
            tomorrowRingText.setText(nextOccurrenceText());
            tomorrowRingText.animate().alpha(1.0f).setDuration(60);
        });
    }

    private String nextOccurrenceText(){
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
            if(!isWeekly()){
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

    private boolean isWeekly(){
        return mondayCheck.isChecked() || tuesdayCheck.isChecked() || wednesdayCheck.isChecked() ||
                thursdayCheck.isChecked() || fridayCheck.isChecked() || saturdayCheck.isChecked() || sundayCheck.isChecked();
    }

    private boolean isToday(int hour, int minute){
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

    private static ArrayList<Integer> getRepeatDays(){
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

    private void initThemeChecker() {
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = chipGroup.findViewById(checkedId);
            if(chip != null){
                Toast.makeText(this, chip.getText().toString(),Toast.LENGTH_LONG).show();
                if(chip.getText().toString().equals("Nebula")){
                    alarmColor = 1;
                }
                else if(chip.getText().toString().equals("Vortex")){
                    alarmColor = 3;
                }
                else if(chip.getText().toString().equals("Gargantua")){
                    alarmColor = 2;
                }
                else if(chip.getText().toString().equals("Unicorn")){
                    alarmColor = 4;
                }
                else if(chip.getText().toString().equals("Default")){
                    alarmColor = 0;
                } else{
                    alarmColor = 0;
                }
            }
        });
    }

    private Calendar yesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal;
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

    private void toggleCheckBox() {
        if(isVibrateOn){
            vibrateCheckBox.setChecked(false);
        }
        else{
            vibrateCheckBox.setChecked(true);
        }
    }

    public String getAlarmRingtone(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri alarmRingtoneUri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (alarmRingtoneUri != null) {
                this.alarmRingTone = alarmRingtoneUri.toString();
                alarmValue.setText(getAlarmRingtone(alarmRingtoneUri));
            } else {
                this.alarmRingTone = null;
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    ImageButton alarmBack, calendarDownButton, calendarUpButton, saveButton;
    ConstraintLayout alarmLabelButton, alarmRingtoneButton, alarmVibrateLayout;
    TextView tomorrowRingText, nextOccurrence, repeatLabel, alarmLabel, alarmValue;
    ChipGroup dayChipGroup;
    CalendarView calendarView;
    static ArrayList<Date> selectedDates;
    static Map<Integer, Boolean> repeatDays;
    TimePicker timePicker;
    public static int alarmColor,timePickerHour, timePickerMinute;
    CheckBox vibrateCheckBox;
    ToggleButton sundayCheck, mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck, saturdayCheck;
    static boolean isVibrateOn;
    ChipGroup chipGroup;
    String alarmRingTone;
    CompoundButton.OnCheckedChangeListener toggleListener;
    SimpleDateFormat format = new SimpleDateFormat("EE, d MMMM ''yy");

}