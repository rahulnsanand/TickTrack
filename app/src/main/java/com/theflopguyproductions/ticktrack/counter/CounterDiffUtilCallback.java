package com.theflopguyproductions.ticktrack.counter;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class CounterDiffUtilCallback extends DiffUtil.Callback {

    ArrayList<CounterData> newList, oldList;

    public CounterDiffUtilCallback(ArrayList<CounterData> newList, ArrayList<CounterData> oldList) {
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
        return newList.get(newItemPosition).counterID.equals(oldList.get(oldItemPosition).counterID);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).counterValue == oldList.get(oldItemPosition).counterValue;
    }

}
