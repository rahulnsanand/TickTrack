package com.theflopguyproductions.ticktrack.testToDelete;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.annimon.stream.Stream;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.CalendarView;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.DatePicker;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.builders.DatePickerBuilder;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.listeners.OnSelectDateListener;
import com.theflopguyproductions.ticktrack.ui.utils.datepicker.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarTrials extends AppCompatActivity implements OnSelectDateListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_trials);

        Button openManyDaysPicker = (Button) findViewById(R.id.openManyDayPickerButton);
//        openManyDaysPicker.setOnClickListener(v -> startActivity(new Intent(this, ManyDaysPickerActivity.class)));

    }



    private void openManyDaysPicker() {
        Calendar min = Calendar.getInstance();
        min.add(Calendar.DAY_OF_MONTH, -5);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.DAY_OF_MONTH, 3);

        List<Calendar> selectedDays = new ArrayList<>(getDisabledDays());
        selectedDays.add(min);
        selectedDays.add(max);

        DatePickerBuilder manyDaysBuilder = new DatePickerBuilder(this, this)
                .setPickerType(CalendarView.MANY_DAYS_PICKER)
                .setHeaderColor(android.R.color.holo_green_dark)
                .setSelectionColor(android.R.color.holo_green_dark)
                .setTodayLabelColor(android.R.color.holo_green_dark)
                .setDialogButtonsColor(android.R.color.holo_green_dark)
                .setSelectedDays(selectedDays)
                .setNavigationVisibility(View.GONE)
                .setDisabledDays(getDisabledDays());

        DatePicker manyDaysPicker = manyDaysBuilder.build();
        manyDaysPicker.show();
    }

    private List<Calendar> getDisabledDays() {
        Calendar firstDisabled = DateUtils.getCalendar();
        firstDisabled.add(Calendar.DAY_OF_MONTH, 2);

        Calendar secondDisabled = DateUtils.getCalendar();
        secondDisabled.add(Calendar.DAY_OF_MONTH, 1);

        Calendar thirdDisabled = DateUtils.getCalendar();
        thirdDisabled.add(Calendar.DAY_OF_MONTH, 18);

        List<Calendar> calendars = new ArrayList<>();
        calendars.add(firstDisabled);
        calendars.add(secondDisabled);
        calendars.add(thirdDisabled);
        return calendars;
    }

    @Override
    public void onSelect(List<Calendar> calendars) {
        Stream.of(calendars).forEach(calendar ->
                Toast.makeText(getApplicationContext(),
                        calendar.getTime().toString(),
                        Toast.LENGTH_SHORT).show());
    }
}