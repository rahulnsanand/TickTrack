package com.theflopguyproductions.ticktrack.stopwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class StopwatchAdapter extends RecyclerView.Adapter<StopwatchAdapter.StopwatchViewHolder>{

    private ArrayList<StopwatchLapData> stopwatchLapData;


    public StopwatchAdapter(ArrayList<StopwatchLapData> stopwatchLapData){
        this.stopwatchLapData = stopwatchLapData;
    }

    private void setTheme(StopwatchViewHolder holder, int theme) {
        if(theme == 1){
            holder.lapNumberText.setTextColor(holder.context.getResources().getColor(R.color.Gray));
            holder.lapElapsedTimeMillis.setTextColor(holder.context.getResources().getColor(R.color.Gray));
        } else {
            holder.lapNumberText.setTextColor(holder.context.getResources().getColor(R.color.LightText));
            holder.lapElapsedTimeMillis.setTextColor(holder.context.getResources().getColor(R.color.LightText));
        }
        holder.lapTimeMillis.setTextColor(holder.context.getResources().getColor(R.color.Accent));
    }

    @NonNull
    @Override
    public StopwatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new StopwatchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stopwatch_flag_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StopwatchViewHolder holder, int position) {
        int currentTheme = holder.tickTrackDatabase.getThemeMode();

        holder.lapNumberText.setText("#"+stopwatchLapData.get(position).getLapNumber());
        holder.lapElapsedTimeMillis.setText(getFormattedHourMinute(stopwatchLapData.get(position).getElapsedTimeInMillis()));
        holder.lapTimeMillis.setText(getFormattedHourMinute(stopwatchLapData.get(position).getLapTimeInMillis()));

        setTheme(holder, currentTheme);
    }

    static String getFormattedHourMinute(long elapsedTime) {
        final StringBuilder displayTime = new StringBuilder();
        int seconds = (int) ((elapsedTime / 1000) % 60);
        int minutes = (int) (elapsedTime / (60 * 1000) % 60);
        int hours = (int) (elapsedTime / (60 * 60 * 1000));
        int milliseconds = (int) ((elapsedTime % 1000) / 10);

        NumberFormat f = new DecimalFormat("00");
        if (minutes == 0) {
            displayTime.append(f.format(seconds)).append(":").append(f.format(milliseconds));
        }
        else if (hours == 0) {
            displayTime.append(f.format(minutes)).append(":").append(f.format(seconds)).append(":").append(f.format(milliseconds));
        }
        else {
            displayTime.append(hours).append(":").append(f.format(minutes)).append(":").append(f.format(seconds)).append(":").append(f.format(milliseconds));
        }
        return displayTime.toString();
    }

    public void diffUtilsChangeData(ArrayList<StopwatchLapData> stopwatchLapData){

        StopwatchDiffUtilCallback stopwatchDiffUtilCallback = new StopwatchDiffUtilCallback(stopwatchLapData, this.stopwatchLapData);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(stopwatchDiffUtilCallback, false);
        diffResult.dispatchUpdatesTo(this);
        this.stopwatchLapData = stopwatchLapData;

    }

    @Override
    public int getItemCount() {
        return stopwatchLapData.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class StopwatchViewHolder extends RecyclerView.ViewHolder {

        private TickTrackDatabase tickTrackDatabase;
        private TextView lapNumberText, lapTimeMillis, lapElapsedTimeMillis;
        private Context context;


        public StopwatchViewHolder(@NonNull View itemView) {
            super(itemView);
            lapNumberText = itemView.findViewById(R.id.stopwatchLapNumberTextView);
            lapTimeMillis = itemView.findViewById(R.id.stopwatchLapTimeDifferenceTextView);
            lapElapsedTimeMillis = itemView.findViewById(R.id.stopwatchLapTimeStampTextView);
            context=itemView.getContext();
            tickTrackDatabase = new TickTrackDatabase(context);
        }
    }
}
