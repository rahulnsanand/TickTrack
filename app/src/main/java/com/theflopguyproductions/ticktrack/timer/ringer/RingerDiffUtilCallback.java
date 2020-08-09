package com.theflopguyproductions.ticktrack.timer.ringer;

import androidx.recyclerview.widget.DiffUtil;

import com.theflopguyproductions.ticktrack.timer.TimerData;

import java.util.ArrayList;

public class RingerDiffUtilCallback extends DiffUtil.Callback {

    ArrayList<TimerData> newList, oldList;

    public RingerDiffUtilCallback(ArrayList<TimerData> newList, ArrayList<TimerData> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).getTimerID()==oldList.get(oldItemPosition).getTimerID();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).getTimerEndedTimeInMillis() == oldList.get(oldItemPosition).getTimerEndedTimeInMillis();
    }
}
