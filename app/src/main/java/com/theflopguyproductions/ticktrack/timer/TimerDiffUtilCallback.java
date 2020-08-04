package com.theflopguyproductions.ticktrack.timer;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class TimerDiffUtilCallback extends DiffUtil.Callback  {

    ArrayList<TimerData> newList, oldList;

    public TimerDiffUtilCallback(ArrayList<TimerData> newList, ArrayList<TimerData> oldList) {
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
        return newList.get(newItemPosition).timerID.equals(oldList.get(oldItemPosition).timerID);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).isTimerOn() == oldList.get(oldItemPosition).isTimerOn() &&
                newList.get(newItemPosition).isTimerPause() == oldList.get(oldItemPosition).isTimerPause() &&
                newList.get(newItemPosition).isTimerReset() == oldList.get(oldItemPosition).isTimerReset();
    }
}
