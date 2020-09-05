package com.theflopguyproductions.ticktrack.widgets.timer.data;

import android.content.Context;

import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TimeAgo;
import com.theflopguyproductions.ticktrack.utils.helpers.UniqueIdGenerator;

import java.util.ArrayList;

public class QuickTimerPreset {

    private Context context;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackTimerDatabase tickTrackTimerDatabase;
    private ArrayList<TimerData> timerDataArrayList;

    public QuickTimerPreset(Context context) {
        this.context = context;
        tickTrackDatabase = new TickTrackDatabase(context);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(context);
    }

    public void setOneMinuteTimer(){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerHour(0);
        timerData.setTimerMinute(1);
        timerData.setTimerSecond(0);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(true);
        timerData.setTimerStartTimeInMillis(-1);
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(0,1,0,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
    }
    public void setTwoMinuteTimer(){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerHour(0);
        timerData.setTimerMinute(2);
        timerData.setTimerSecond(0);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(true);
        timerData.setTimerStartTimeInMillis(-1);
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(0,2,0,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
    }
    public void setFiveMinuteTimer(){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerHour(0);
        timerData.setTimerMinute(5);
        timerData.setTimerSecond(0);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(true);
        timerData.setTimerStartTimeInMillis(-1);
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(0,5,0,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
    }
    public void setTenMinuteTimer(){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerHour(0);
        timerData.setTimerMinute(10);
        timerData.setTimerSecond(0);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(true);
        timerData.setTimerStartTimeInMillis(-1);
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(0,10,0,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
    }
    public void setCustomTimer(int pickedHour, int pickedMinute, int pickedSecond){

        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerHour(pickedHour);
        timerData.setTimerMinute(pickedMinute);
        timerData.setTimerSecond(pickedSecond);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(true);
        timerData.setTimerStartTimeInMillis(-1);
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(pickedHour,pickedMinute,pickedSecond,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);

    }

}
