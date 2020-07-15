package com.theflopguyproductions.ticktrack.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;

import java.lang.invoke.ConstantCallSite;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.RecyclerItemViewHolder> {

    private ArrayList<AlarmData> myList;
    int mLastPosition = 0;

    public AlarmAdapter(ArrayList<AlarmData> myList) {
        this.myList = myList;
    }

    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item_layout, parent, false);
        RecyclerItemViewHolder holder = new RecyclerItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, final int position) {

        holder.alarmTime.setText("" + myList.get(position).getAlarmTime());
        holder.alarmLabel.setText(myList.get(position).getAlarmLabel());
        holder.alarmMode.setChecked(myList.get(position).isAlarmMode());
        holder.alarmAMPM.setText(myList.get(position).getAlarmAmPm());
        holder.alarmOccurence.setText(myList.get(position).getAlarmNextOccurrence());
        holder.itemColor = myList.get(position).getAlarmColor();

        setColor(holder);
        mLastPosition = position;
    }

    private static final SimpleDateFormat timestampReadableFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private void setColor(RecyclerItemViewHolder holder) {
        if (holder.itemColor == 1) {
            holder.alarmLayout.setBackgroundResource(R.drawable.round_rect_blue);
        }
        if (holder.itemColor == 2) {
            holder.alarmLayout.setBackgroundResource(R.drawable.round_rect_black);
        }
        if (holder.itemColor == 3) {
            holder.alarmLayout.setBackgroundResource(R.drawable.round_rect_green);
        }
        if (holder.itemColor == 4) {
            holder.alarmLayout.setBackgroundResource(R.drawable.round_rect_purple);
        }
    }

    @Override
    public int getItemCount() {
        return (null != myList ? myList.size() : 0);
    }

    public void notifyData(ArrayList<AlarmData> myList) {
        this.myList = myList;
        notifyDataSetChanged();
    }


    public static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView alarmTime;
        private final TextView alarmLabel;
        private final TextView alarmOccurence;
        private final TextView alarmAMPM;
        private final Switch alarmMode;

        public ConstraintLayout alarmLayout;
        private int itemColor;
        public ConstraintLayout alarmBackgroundLayout;

        public RecyclerItemViewHolder(final View parent) {
            super(parent);

            alarmLabel = (TextView) parent.findViewById(R.id.alarmLabel);
            alarmTime = (TextView) parent.findViewById(R.id.alarmTime);
            alarmOccurence = (TextView) parent.findViewById(R.id.alarmOccurence);
            alarmAMPM = (TextView) parent.findViewById(R.id.amPmText);
            alarmMode = (Switch) parent.findViewById(R.id.alarmSwitch);

            alarmLayout = (ConstraintLayout) parent.findViewById(R.id.alarmLayout);
            alarmBackgroundLayout = (ConstraintLayout) parent.findViewById(R.id.alarmBackgroundLayout);

            alarmLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MainActivityToChange.counterActivity(getAdapterPosition());
                    Toast.makeText(itemView.getContext(), "Position:" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
//            countLayout.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    CounterFragment.deleteItem(getAdapterPosition());
//                    return false;
//                }
//            });
        }

    }
}
