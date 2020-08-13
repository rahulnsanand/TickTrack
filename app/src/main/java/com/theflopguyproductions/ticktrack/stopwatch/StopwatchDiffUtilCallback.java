package com.theflopguyproductions.ticktrack.stopwatch;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class StopwatchDiffUtilCallback extends DiffUtil.Callback {

    ArrayList<StopwatchLapData> newList, oldList;

    public StopwatchDiffUtilCallback(ArrayList<StopwatchLapData> newList, ArrayList<StopwatchLapData> oldList) {
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
        return newList.get(newItemPosition).getLapNumber()==oldList.get(oldItemPosition).getLapNumber();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).getElapsedTimeInMillis()==oldList.get(oldItemPosition).getElapsedTimeInMillis();
    }

}
