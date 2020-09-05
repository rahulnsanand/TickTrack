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
    private ArrayList<TimerWidgetData> timerWidgetDataArrayList;

    public QuickTimerPreset(Context context) {
        this.context = context;
        tickTrackDatabase = new TickTrackDatabase(context);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(context);
    }

    private void setTimerWidgetData(int timerIntId, String timerStringId, int widgetId){
        timerWidgetDataArrayList = tickTrackDatabase.retrieveTimerWidgetList();

        for(int i=0; i<timerWidgetDataArrayList.size(); i++){
            if(timerWidgetDataArrayList.get(i).getTimerWidgetId()==widgetId){
                timerWidgetDataArrayList.get(i).setTimerIdString(timerStringId);
                timerWidgetDataArrayList.get(i).setTimerIdInteger(timerIntId);
            }
        }

        tickTrackDatabase.storeTimerWidgetList(timerWidgetDataArrayList);
    }

    public int setOneMinuteTimer(int widgetId){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerHour(0);
        timerData.setTimerMinute(1);
        timerData.setTimerSecond(0);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(true);
        timerData.setTimerStartTimeInMillis(System.currentTimeMillis()+TimeAgo.getTimerDataInMillis(0,1,0,0));
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(0,1,0,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);

        setTimerWidgetData(timerData.getTimerIntID(), timerData.getTimerID(), widgetId);

        return timerData.getTimerIntID();
    }
    public int setTwoMinuteTimer(int widgetId){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerHour(0);
        timerData.setTimerMinute(2);
        timerData.setTimerSecond(0);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(true);
        timerData.setTimerStartTimeInMillis(System.currentTimeMillis()+TimeAgo.getTimerDataInMillis(0,2,0,0));
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(0,2,0,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);

        setTimerWidgetData(timerData.getTimerIntID(), timerData.getTimerID(), widgetId);

        return timerData.getTimerIntID();
    }
    public int setFiveMinuteTimer(int widgetId){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerHour(0);
        timerData.setTimerMinute(5);
        timerData.setTimerSecond(0);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(true);
        timerData.setTimerStartTimeInMillis(System.currentTimeMillis()+TimeAgo.getTimerDataInMillis(0,5,0,0));
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(0,5,0,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);

        setTimerWidgetData(timerData.getTimerIntID(), timerData.getTimerID(), widgetId);

        return timerData.getTimerIntID();
    }
    public int setTenMinuteTimer(int widgetId){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerHour(0);
        timerData.setTimerMinute(10);
        timerData.setTimerSecond(0);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(true);
        timerData.setTimerStartTimeInMillis(System.currentTimeMillis()+TimeAgo.getTimerDataInMillis(0,10,0,0));
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(0,10,0,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);

        setTimerWidgetData(timerData.getTimerIntID(), timerData.getTimerID(), widgetId);

        return timerData.getTimerIntID();
    }
    public int setCustomTimer(int pickedHour, int pickedMinute, int pickedSecond, int widgetId){

        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerHour(pickedHour);
        timerData.setTimerMinute(pickedMinute);
        timerData.setTimerSecond(pickedSecond);
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(true);
        timerData.setTimerStartTimeInMillis(System.currentTimeMillis()+TimeAgo.getTimerDataInMillis(pickedHour,pickedMinute,pickedSecond,0));
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(pickedHour,pickedMinute,pickedSecond,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);

        setTimerWidgetData(timerData.getTimerIntID(), timerData.getTimerID(), widgetId);

        return timerData.getTimerIntID();
    }

}