package com.theflopguyproductions.ticktrack.timer.ringer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.TimerAdapter;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.TimerDiffUtilCallback;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TimeAgo;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RingerAdapter extends RecyclerView.Adapter<RingerAdapter.RingerDataViewHolder> {

    private ArrayList<TimerData> timerDataArrayList;
    private Handler timerStopHandler = new Handler();

    public RingerAdapter(Context context, ArrayList<TimerData> timerDataArrayList) {
        this.timerDataArrayList = timerDataArrayList;
    }

    @NonNull
    public RingerDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stop_alarm, parent, false);

        return new RingerDataViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull RingerDataViewHolder holder, int position) {

        int theme = holder.tickTrackDatabase.getThemeMode();

        holder.backgroundProgressBar.setInstantProgress(1);
        holder.foregroundProgressBar.setProgress(1);


        if(!timerDataArrayList.get(holder.getAdapterPosition()).getTimerLabel().equals("Set label")) {
            holder.timerLabel.setText(timerDataArrayList.get(holder.getAdapterPosition()).getTimerLabel());
        } else {
            holder.timerLabel.setVisibility(View.GONE);
        }

        long stopTimeRetrieve = timerDataArrayList.get(holder.getAdapterPosition()).getTimerEndedTimeInMillis();
        holder.UpdateTime = (System.currentTimeMillis() - stopTimeRetrieve) / 1000F;

        holder.timerRunnable = () -> {
            if(timerDataArrayList.get(holder.getAdapterPosition()).isTimerRinging()){
                holder.UpdateTime += 1;
                holder.timerValue.setText(updateTimerTextView(holder.UpdateTime));
                timerStopHandler.postDelayed(holder.timerRunnable, 1000);
            }
        };

        if(timerDataArrayList.get(holder.getAdapterPosition()).isTimerRinging()){
            timerStopHandler.postDelayed(holder.timerRunnable, 0);
        } else {
            timerStopHandler.removeCallbacks(holder.timerRunnable);
        }

        setTheme(holder, theme);
    }

    private String updateTimerTextView(float countdownValue){
        float hours = countdownValue /3600;
        float minutes = countdownValue /60%60;
        float seconds = countdownValue %60;

        return String.format(Locale.getDefault(),"- %02d:%02d:%02d",(int)hours,(int)minutes,(int)seconds);
    }
    private void setTheme(RingerDataViewHolder holder, int theme) {
        if(theme == 1){
//            holder.rootLayout.setBackgroundResource(R.drawable.recycler_layout_light);
            holder.timerLabel.setTextColor(holder.context.getResources().getColor(R.color.Gray));
        } else {
//            holder.rootLayout.setBackgroundResource(R.drawable.recycler_layout_dark);
            holder.timerLabel.setTextColor(holder.context.getResources().getColor(R.color.LightText));
        }
    }

    @Override
    public int getItemCount() {
        return timerDataArrayList.size() ;
    }
    @Override
    public int getItemViewType(int position) {
        return (position == timerDataArrayList.size()) ? R.layout.recycler_footer_layout : R.layout.timer_item_layout;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void diffUtilsChangeData(ArrayList<TimerData> timerDataArrayList){

        TimerDiffUtilCallback timerDiffUtilCallback = new TimerDiffUtilCallback(timerDataArrayList, this.timerDataArrayList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(timerDiffUtilCallback, false);
        diffResult.dispatchUpdatesTo(this);
        this.timerDataArrayList = timerDataArrayList;

    }

    public static class RingerDataViewHolder extends RecyclerView.ViewHolder {

        private TextView timerLabel, timerValue;
        private TickTrackProgressBar backgroundProgressBar, foregroundProgressBar;
        private ConstraintLayout rootLayout;
        private Context context;
        private TickTrackDatabase tickTrackDatabase;
        private Runnable timerRunnable;
        private float UpdateTime = 0L;

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
    }
}
