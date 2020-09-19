package com.theflopguyproductions.ticktrack.timer.ringer;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.gtomato.android.ui.widget.CarouselView;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TimeAgo;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RingerAdapter extends CarouselView.Adapter<RingerAdapter.RingerDataViewHolder> {

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
    public void onViewDetachedFromWindow(@NonNull RingerDataViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        timerStopHandler.removeCallbacks(holder.timerRunnable);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RingerDataViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(timerDataArrayList.get(holder.getAdapterPosition()).isTimerRinging()){
            timerStopHandler.postDelayed(holder.timerRunnable, 0);
        } else {
            timerStopHandler.removeCallbacks(holder.timerRunnable);
        }

        int timerHour = timerDataArrayList.get(holder.getAdapterPosition()).getTimerHour();
        int timerMinute = timerDataArrayList.get(holder.getAdapterPosition()).getTimerMinute();
        int timerSecond = timerDataArrayList.get(holder.getAdapterPosition()).getTimerSecond();
        if(!timerDataArrayList.get(holder.getAdapterPosition()).isQuickTimer()){
            if(!timerDataArrayList.get(holder.getAdapterPosition()).getTimerLabel().equals("Set label")) {
                holder.timerLabel.setText(timerDataArrayList.get(holder.getAdapterPosition()).getTimerLabel());
            } else {
                holder.timerLabel.setText("Timer "+TimeAgo.getTimerTitle(timerHour, timerMinute, timerSecond));
            }
        } else {
            holder.timerLabel.setText("QuickTimer "+TimeAgo.getTimerTitle(timerHour, timerMinute, timerSecond));
            holder.timerLabel.setTextColor(holder.context.getResources().getColor(R.color.roboto_calendar_circle_1));
        }

        holder.foregroundProgressBar.setProgress(1);

    }

    @Override
    public void onBindViewHolder(@NonNull RingerDataViewHolder holder, int position) {

        int theme = holder.tickTrackDatabase.getThemeMode();

        int timerHour = timerDataArrayList.get(holder.getAdapterPosition()).getTimerHour();
        int timerMinute = timerDataArrayList.get(holder.getAdapterPosition()).getTimerMinute();
        int timerSecond = timerDataArrayList.get(holder.getAdapterPosition()).getTimerSecond();

        setTheme(holder, theme);

        if(!timerDataArrayList.get(holder.getAdapterPosition()).isQuickTimer()){
            if(!timerDataArrayList.get(holder.getAdapterPosition()).getTimerLabel().equals("Set label")) {
                holder.timerLabel.setText(timerDataArrayList.get(holder.getAdapterPosition()).getTimerLabel());
            } else {
                holder.timerLabel.setText("Timer "+TimeAgo.getTimerTitle(timerHour, timerMinute, timerSecond));
            }
        } else {
            holder.timerLabel.setText("QuickTimer "+TimeAgo.getTimerTitle(timerHour, timerMinute, timerSecond));
            holder.timerLabel.setTextColor(holder.context.getResources().getColor(R.color.roboto_calendar_circle_1));
        }

        holder.timerRunnable = () -> {
            if(timerDataArrayList.get(position).isTimerRinging()){
                long durationElapsed = SystemClock.elapsedRealtime() - timerDataArrayList.get(holder.getAdapterPosition()).getTimerEndedTimeInMillis();
                holder.timerValue.setText(updateTimerTextView(durationElapsed));
                timerStopHandler.postDelayed(holder.timerRunnable, 100);
            } else {
                timerStopHandler.removeCallbacks(holder.timerRunnable);
            }
        };
    }

    private String updateTimerTextView(long countdownValue){
        int hours = (int) TimeUnit.MILLISECONDS.toHours(countdownValue);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(countdownValue) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(countdownValue)));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(countdownValue) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(countdownValue)));

        return String.format(Locale.getDefault(),"- %02d:%02d:%02d", hours,minutes,seconds);
    }
    private void setTheme(RingerDataViewHolder holder, int theme) {
        holder.dataLayout.setBackgroundResource(R.drawable.fab_dark_background);
        holder.timerLabel.setTextColor(holder.context.getResources().getColor(R.color.Accent));
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

        RingerTimerDiffUtilCallback timerDiffUtilCallback = new RingerTimerDiffUtilCallback(timerDataArrayList, this.timerDataArrayList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(timerDiffUtilCallback, false);
        diffResult.dispatchUpdatesTo(this);
        this.timerDataArrayList = timerDataArrayList;

    }

    public static class RingerDataViewHolder extends RecyclerView.ViewHolder {

        private TextView timerLabel, timerValue;
        private TickTrackProgressBar  foregroundProgressBar;
        private ConstraintLayout rootLayout, dataLayout;
        private Context context;
        private TickTrackDatabase tickTrackDatabase;
        private Runnable timerRunnable;

        public RingerDataViewHolder(@NonNull View parent) {
            super(parent);

            timerLabel = parent.findViewById(R.id.timerStopActivityLabelTextView);
            timerValue = parent.findViewById(R.id.timerStopActivityTimerValueTextView);
            foregroundProgressBar = parent.findViewById(R.id.timerStopActivityProgressBar);
            rootLayout = parent.findViewById(R.id.timerStopActivityRootLayout);
            dataLayout = parent.findViewById(R.id.timerStopActivityDataLayout);

            context=parent.getContext();
            tickTrackDatabase = new TickTrackDatabase(context);

        }
    }
}
