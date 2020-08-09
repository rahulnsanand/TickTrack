package com.theflopguyproductions.ticktrack.timer.ringer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.TimerDiffUtilCallback;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class RingerAdapter extends RecyclerView.Adapter<RingerAdapter.RingerDataViewHolder> {

    private ArrayList<TimerData> timerDataArrayList;
    private Context context;

    public RingerAdapter(Context context, ArrayList<TimerData> timerDataArrayList) {
        this.context = context;
        this.timerDataArrayList = timerDataArrayList;
    }

    @NonNull
    @Override
    public RingerDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_item_layout, parent, false);

        return new RingerDataViewHolder(itemView);

    }

    private int getCurrentTimerPosition(int timerID){
        for(int i = 0; i <timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i)
                    .getTimerID()==timerID){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull RingerDataViewHolder holder, int position) {

        timerDataArrayList = holder.tickTrackDatabase.retrieveTimerList();

        int theme = holder.tickTrackDatabase.getThemeMode();

        int currentPosition = getCurrentTimerPosition(timerDataArrayList.get(position).getTimerID());

        if(theme == 1){
            holder.rootLayout.setBackgroundColor(holder.context.getResources().getColor(R.color.DarkText));
        } else {
            holder.rootLayout.setBackgroundColor(holder.context.getResources().getColor(R.color.LightText));
        }

        if(currentPosition!=-1){

            timerDataArrayList.get(position).start(timerDataArrayList.get(position).getTimerAlarmEndTimeInMillis());
            timerDataArrayList.get(position).setStoppedTimerListener(holder);

            holder.timerLabel.setText(timerDataArrayList.get(currentPosition).getTimerLabel());
            holder.foregroundProgressBar.setProgress(1);
            holder.backgroundProgressBar.setProgress(1);

            holder.timerValue.setText("Timer Complete!");
        }
    }

    public void diffUtilsChangeData(ArrayList<TimerData> timerDataArrayList){

        RingerDiffUtilCallback ringerDiffUtilCallback = new RingerDiffUtilCallback(timerDataArrayList, this.timerDataArrayList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(ringerDiffUtilCallback, false);
        diffResult.dispatchUpdatesTo(this);
        this.timerDataArrayList = timerDataArrayList;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class RingerDataViewHolder extends RecyclerView.ViewHolder implements TimerData.stoppedTimerListener{

        private TextView timerLabel, timerValue;
        private TickTrackProgressBar backgroundProgressBar, foregroundProgressBar;
        private ConstraintLayout rootLayout;
        private Context context;
        TickTrackDatabase tickTrackDatabase;

        public RingerDataViewHolder(@NonNull View parent) {
            super(parent);

            timerLabel = parent.findViewById(R.id.timerStopActivityLabelTextView);
            timerValue = parent.findViewById(R.id.timerStopActivityTimerValueTextView);
            backgroundProgressBar = parent.findViewById(R.id.timerStopActivityBackgroundProgressBar);
            foregroundProgressBar = parent.findViewById(R.id.timerStopActivityProgressBar);
            rootLayout = parent.findViewById(R.id.timerStopActivityRootLayout);

            context=parent.getContext();
            tickTrackDatabase = new TickTrackDatabase(context);

        }

        private void updateStopTimeText(float UpdateTime) {

            float hours = UpdateTime /3600;
            float minutes = UpdateTime /60%60;
            float seconds = UpdateTime %60;

            String hourLeft = String.format(Locale.getDefault(),"-%02d:%02d:%02d",(int)hours,(int)minutes,(int)seconds);
            timerValue.setText(hourLeft);
        }

        @Override
        public void onTick(float currentValue) {
            ((Activity) context).runOnUiThread(() -> updateStopTimeText(currentValue));
        }
    }
}
