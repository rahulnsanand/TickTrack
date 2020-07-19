package com.theflopguyproductions.ticktrack.ui.home.activity.alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterData;
import com.theflopguyproductions.ticktrack.ui.dialogs.GetAlarmLabelDialog;
import com.theflopguyproductions.ticktrack.ui.dialogs.GetAlarmLabelDialogEdit;
import com.theflopguyproductions.ticktrack.ui.dialogs.YesNoDialog;
import com.theflopguyproductions.ticktrack.ui.home.AlarmAdapter;
import com.theflopguyproductions.ticktrack.ui.home.AlarmData;
import com.theflopguyproductions.ticktrack.ui.home.HomeFragment;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.CalendarView;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.EventDay;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.listeners.OnDayClickListener;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditAlarmActivity extends AppCompatActivity {

    ImageButton alarmBack, calendarDownButton, calendarUpButton, saveButton;
    ConstraintLayout alarmLabelButton, alarmRingtoneButton, alarmVibrateLayout;
    TextView tomorrowRingText, nextOccurrence, repeatLabel, alarmLabel, alarmValue, alarmRingToneText;
    ChipGroup dayChipGroup;
    CalendarView calendarView;
    static ArrayList<Calendar> selectedDates;
    static Map<Integer, Boolean> repeatDays;
    TimePicker timePicker;
    public static int alarmColor,timePickerHour, timePickerMinute, currentPosition;
    CheckBox vibrateCheckBox;
    ToggleButton sundayCheck, mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck, saturdayCheck;
    static boolean isVibrateOn;
    ChipGroup chipGroup;
    String alarmRingTone;
    CompoundButton.OnCheckedChangeListener toggleListener;
    SimpleDateFormat format = new SimpleDateFormat("EE, d MMMM ''yy");
    private static ArrayList<AlarmData> alarmDataArrayList;

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

    public void saveAlarm() {
        updateArrayList();
        updateData();
        finish();
        setAlarm();
    }

    private void updateArrayList() {

        alarmDataArrayList.get(currentPosition).setAlarmVibrate(isVibrateOn);
        alarmDataArrayList.get(currentPosition).setAlarmRingTone(alarmRingTone);
        alarmDataArrayList.get(currentPosition).setRepeatDaysInWeek(getRepeatDays());
        getCurrentCustomCheck();
        alarmDataArrayList.get(currentPosition).setRepeatCustomDates(selectedDates);
        alarmDataArrayList.get(currentPosition).setAlarmTheme(alarmColor);
        alarmDataArrayList.get(currentPosition).setAlarmMinute(timePicker.getCurrentMinute());
        alarmDataArrayList.get(currentPosition).setAlarmHour(timePicker.getCurrentHour());
        alarmDataArrayList.get(currentPosition).setAlarmOnOff(true);
        if(getLabel().equals("Set alarm label")){
            alarmDataArrayList.get(currentPosition).setAlarmLabel("");
        } else{
            alarmDataArrayList.get(currentPosition).setAlarmLabel(getLabel());
        }

    }

    private void getCurrentCustomCheck() {
        selectedDates=new ArrayList<>();
        if(calendarView.getSelectedDates().size()>0){
            for (Calendar calendar : calendarView.getSelectedDates()) {
                selectedDates.add(calendar);
                onCalendarValueChosen();
            }
        }
    }

    public void discardAlarm() {
        finish();
    }

    private void showYesNoDialog(){
        YesNoDialog dialog = new YesNoDialog(this);
        dialog.show();
    }
    public void showLabelDialog() {
        GetAlarmLabelDialogEdit dialog = new GetAlarmLabelDialogEdit(this);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        initVariables();
        initWeeklyToggleListener();
        initTimePickerListener();
        retrieveData();
        presetValues();
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
                    selectedDates.add(calendar);
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

    }

    private void initVariables() {
        alarmBack = findViewById(R.id.alarmEditActivityBackButton);
        calendarDownButton = findViewById(R.id.calendarEditDownButton);
        calendarUpButton = findViewById(R.id.calendarEditUpButton);
        calendarView = findViewById(R.id.calendarEditView);
        nextOccurrence = findViewById(R.id.calendarEditNextOccurence);
        dayChipGroup = findViewById(R.id.daysEditRepeatChipGroup);
        tomorrowRingText = findViewById(R.id.alarmEditNextOccurenceText);
        sundayCheck = findViewById(R.id.sundayEditToggle);
        mondayCheck = findViewById(R.id.mondayEditToggle);
        tuesdayCheck = findViewById(R.id.tuesdayEditToggle);
        wednesdayCheck = findViewById(R.id.wednesdayEditToggle);
        thursdayCheck = findViewById(R.id.thursdayEditToggle);
        fridayCheck = findViewById(R.id.fridayEditToggle);
        saturdayCheck = findViewById(R.id.saturdayEditToggle);
        saveButton = findViewById(R.id.saveEditButton);
        timePicker = findViewById(R.id.timeEditPicker);
        repeatLabel = findViewById(R.id.repeatEditText);
        vibrateCheckBox = findViewById(R.id.vibrateEditCheckBox);
        alarmLabel = findViewById(R.id.alarmEditLabel);
        alarmLabelButton = findViewById(R.id.alarmEditLabelBackground);
        alarmRingtoneButton = findViewById(R.id.ringtoneEditBackground);
        alarmVibrateLayout = findViewById(R.id.vibrateEditBackground);
        alarmValue = findViewById(R.id.alarmEditRingtoneValue);
        chipGroup = findViewById(R.id.alarmEditThemeGroup);
        alarmRingToneText = findViewById(R.id.alarmEditRingtoneValue);

    }

    public void retrieveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("AlarmData", null);
        Type type = new TypeToken<ArrayList<AlarmData>>() {}.getType();
        alarmDataArrayList = gson.fromJson(json, type);
        currentPosition = getIntent().getIntExtra("currentAlarmPosition",0);

        alarmRetHour = alarmDataArrayList.get(currentPosition).getAlarmHour();
        alarmRetMinute = alarmDataArrayList.get(currentPosition).getAlarmMinute();
        alarmRetTheme = alarmDataArrayList.get(currentPosition).getAlarmTheme();
        repeatRetCustomDates = alarmDataArrayList.get(currentPosition).getRepeatCustomDates();
        repeatRetDaysInWeek = alarmDataArrayList.get(currentPosition).getRepeatDaysInWeek();
        alarmRetRingTone = alarmDataArrayList.get(currentPosition).getAlarmRingTone();
        alarmRetLabel = alarmDataArrayList.get(currentPosition).getAlarmLabel();
        alarmRetVibrate =  alarmDataArrayList.get(currentPosition).isAlarmVibrate();

        if(alarmRetLabel.equals("")){
            alarmRetLabel="Set alarm label";
        }
        if(repeatRetCustomDates==null){
            repeatRetCustomDates = new ArrayList<>();
        }

    }

    public void presetValues(){
        repeatDays = new HashMap<>();
        calendarView.setMinimumDate(yesterday());

        alarmColor = alarmRetTheme;
        timePicker.setCurrentHour(alarmRetHour);
        timePicker.setCurrentMinute(alarmRetMinute);
        timePickerHour = timePicker.getCurrentHour();
        timePickerMinute = timePicker.getCurrentMinute();

        if(repeatRetCustomDates.size()>0 && !(repeatRetDaysInWeek.size()>0)){
            tomorrowRingText.setVisibility(View.INVISIBLE);
            dayChipGroup.setVisibility(View.INVISIBLE);
            nextOccurrence.setVisibility(View.VISIBLE);
            calendarUpButton.setVisibility(View.INVISIBLE);
            calendarDownButton.setVisibility(View.VISIBLE);
            repeatLabel.setText("Custom repeat");

            nextOccurrence.setText("Next ring: "+format.format(repeatRetCustomDates.get(0).getTime()));
            setCalendarSelection(repeatRetCustomDates);
        }
        else if(repeatRetDaysInWeek.size()>0 && !(repeatRetCustomDates.size() >0)){
            tomorrowRingText.setVisibility(View.VISIBLE);
            nextOccurrence.setVisibility(View.INVISIBLE);
            dayChipGroup.setVisibility(View.VISIBLE);
            calendarView.setVisibility(View.GONE);
            calendarUpButton.setVisibility(View.INVISIBLE);
            calendarDownButton.setVisibility(View.VISIBLE);
            repeatLabel.setText("Repeat");
            for(int i = 0; i < repeatRetDaysInWeek.size(); i ++){
                repeatDays.put(repeatRetDaysInWeek.get(i),true);
            }
            setChipSelected(repeatRetDaysInWeek);
            tomorrowRingText.setText(nextOccurrenceText());
        }
        else{
            initDefaultLayoutVisible();
        }

        alarmRingTone = alarmRetRingTone;
        alarmLabel.setText(alarmRetLabel);
        vibrateCheckBox.setChecked(alarmRetVibrate);
        if(alarmRingTone!=null) {
            Uri alarmPresetTone = Uri.parse(alarmRingTone);
            Ringtone ringtonePresetAlarm = RingtoneManager.getRingtone(getApplicationContext(), alarmPresetTone);
            alarmRingToneText.setText(ringtonePresetAlarm.getTitle(this));
        }
        else{
            Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone ringtoneAlarm = RingtoneManager.getRingtone(getApplicationContext(), alarmTone);
            alarmRingToneText.setText(ringtoneAlarm.getTitle(this));
        }
    }

    public void setChipSelected(ArrayList<Integer> repeatDaysInWeek){

        for(int i = 0 ; i < repeatDaysInWeek.size(); i ++){
            int currentDayLoop = repeatDaysInWeek.get(i);

            if(currentDayLoop==1){
                sundayCheck.setChecked(true);
            } else if(currentDayLoop==2){
                mondayCheck.setChecked(true);
            } else if(currentDayLoop==3){
                tuesdayCheck.setChecked(true);
            } else if(currentDayLoop==4){
                wednesdayCheck.setChecked(true);
            } else if(currentDayLoop==5){
                thursdayCheck.setChecked(true);
            } else if(currentDayLoop==6){
                fridayCheck.setChecked(true);
            } else if(currentDayLoop==7){
                saturdayCheck.setChecked(true);
            }

        }

    }

    public void setCalendarSelection(ArrayList<Calendar> repeatCustomDates){
        List<Calendar> selectedDates = new ArrayList<>(repeatCustomDates);
        calendarView.setSelectedDates(selectedDates);
    }

    private void initWeeklyToggleListener() {
        toggleListener = (compoundButton, b) -> {
            switch(compoundButton.getId()) {
                case R.id.sundayEditToggle:
                    setRepeatDays(compoundButton, Calendar.SUNDAY);
                    break;
                case R.id.mondayEditToggle:
                    setRepeatDays(compoundButton, Calendar.MONDAY);
                    break;
                case R.id.tuesdayEditToggle:
                    setRepeatDays(compoundButton, Calendar.TUESDAY);
                    break;
                case R.id.wednesdayEditToggle:
                    setRepeatDays(compoundButton, Calendar.WEDNESDAY);
                    break;
                case R.id.thursdayEditToggle:
                    setRepeatDays(compoundButton, Calendar.THURSDAY);
                    break;
                case R.id.fridayEditToggle:
                    setRepeatDays(compoundButton, Calendar.FRIDAY);
                    break;
                case R.id.saturdayEditToggle:
                    setRepeatDays(compoundButton, Calendar.SATURDAY);
                    break;
                default:
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
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmRingtoneUri);
                alarmValue.setText(ringtone.getTitle(getApplicationContext()));
            } else {
                Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                this.alarmRingTone = alarmTone.toString();
                Ringtone ringtoneAlarm = RingtoneManager.getRingtone(getApplicationContext(), alarmTone);
                alarmValue.setText(ringtoneAlarm.getTitle(this));
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
    private Calendar yesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal;
    }

    private void updateData() {
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmDataArrayList);
        editor.putString("AlarmData", json);
        editor.apply();

        //TODO SET ALARM HERE
    }

    public static void setAlarm(){

        if(alarmDataArrayList!=null){
            for(int i = 0; i < alarmDataArrayList.size(); i++){
                if(alarmDataArrayList.get(i).isAlarmOnOff()){
                    System.out.println(alarmDataArrayList.get(i).getAlarmHour()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                }
            }
        }
    }

    int alarmRetHour;
    int alarmRetMinute;
    int alarmRetTheme;
    ArrayList<Calendar> repeatRetCustomDates;
    ArrayList<Integer> repeatRetDaysInWeek;
    String alarmRetRingTone;
    String alarmRetLabel;
    boolean alarmRetVibrate;

}