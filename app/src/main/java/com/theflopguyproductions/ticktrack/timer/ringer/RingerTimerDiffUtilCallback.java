package com.theflopguyproductions.ticktrack.timer.ringer;

import androidx.recyclerview.widget.DiffUtil;

import com.theflopguyproductions.ticktrack.timer.data.TimerData;

import java.util.ArrayList;

public class RingerTimerDiffUtilCallback extends DiffUtil.Callback  {

    ArrayList<TimerData> newList, oldList;

    public RingerTimerDiffUtilCallback(ArrayList<TimerData> newList, ArrayList<TimerData> oldList) {
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
        return newList.get(newItemPosition).getTimerID().equals(oldList.get(oldItemPosition).getTimerID()) &&
                newList.get(newItemPosition).getTimerIntID()==oldList.get(oldItemPosition).getTimerIntID();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).isTimerOn() == oldList.get(oldItemPosition).isTimerOn() &&
                newList.get(newItemPosition).isTimerPause() == oldList.get(oldItemPosition).isTimerPause() &&
                newList.get(newItemPosition).isTimerRinging() == oldList.get(oldItemPosition).isTimerRinging();
    }
}
