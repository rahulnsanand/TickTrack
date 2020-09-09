package com.theflopguyproductions.ticktrack.timer.data;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class QuickTimerAdapter extends RecyclerView.Adapter<QuickTimerAdapter.timerDataViewHolder> {

    private ArrayList<TimerData> timerDataArrayList;
    private Handler timerStatusUpdateHandler = new Handler();
    private Handler timerElapsedBlinkHandler = new Handler();
    private Handler timerProgressHandler = new Handler();

    public QuickTimerAdapter(Context context, ArrayList<TimerData> timerDataArrayList) {
        this.timerDataArrayList = timerDataArrayList;
    }

    @NonNull
    public timerDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.quick_timer_item_layout, parent, false);
        return new timerDataViewHolder(itemView);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull timerDataViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        timerStatusUpdateHandler.removeCallbacks(holder.timerRunnable);
        timerElapsedBlinkHandler.removeCallbacks(holder.blinkRunnable);
        timerProgressHandler.removeCallbacks(holder.progressRunnable);
    }

    @Override
    public void onBindViewHolder(@NonNull timerDataViewHolder holder, int position) {

        int theme = holder.tickTrackDatabase.getThemeMode();
        setTheme(holder, theme);
        if(timerDataArrayList.get(holder.getAdapterPosition()).isQuickTimer() && !timerDataArrayList.get(holder.getAdapterPosition()).isTimerRinging()){

            holder.timerResetButton.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.ic_round_refresh_white_24));

            long endTime = timerDataArrayList.get(holder.getAdapterPosition()).getTimerAlarmEndTimeInMillis();
            long startTime = timerDataArrayList.get(holder.getAdapterPosition()).getTimerStartTimeInMillis();
            timerStatusUpdateHandler.post(holder.timerRunnable);
            holder.timerRunnable = () -> {
                long durationLeft = endTime - (System.currentTimeMillis()-startTime);
                holder.timerText.setText(updateTimerTextView(durationLeft));
                timerStatusUpdateHandler.postDelayed(holder.timerRunnable, 100);
            };

        } if(timerDataArrayList.get(holder.getAdapterPosition()).isQuickTimer() && timerDataArrayList.get(holder.getAdapterPosition()).isTimerRinging()){

            holder.timerResetButton.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.ic_stop_white_24));

            timerStatusUpdateHandler.removeCallbacks(holder.timerRunnable);

        }
    }

    private String updateTimerTextView(long countdownValue){
        int hours = (int) TimeUnit.MILLISECONDS.toHours(countdownValue);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(countdownValue) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(countdownValue)));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(countdownValue) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(countdownValue)));

        return String.format(Locale.getDefault(),"%02d:%02d:%02d", hours,minutes,seconds);
    }
    private void setTheme(timerDataViewHolder holder, int theme) {
        if(theme == 1){
            holder.timerLayout.setBackgroundResource(R.drawable.recycler_layout_light);
            holder.timerText.setTextColor(holder.context.getResources().getColor(R.color.Gray));
        } else {
            holder.timerLayout.setBackgroundResource(R.drawable.recycler_layout_dark);
            holder.timerText.setTextColor(holder.context.getResources().getColor(R.color.LightText));
        }
    }

    @Override
    public int getItemCount() {
        return timerDataArrayList.size();
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

    public static class timerDataViewHolder extends RecyclerView.ViewHolder {

        private TextView timerText;
        public ConstraintLayout timerLayout;
        private ImageView timerFlag;
        private Context context;
        private FloatingActionButton timerResetButton;
        private Runnable timerRunnable, blinkRunnable, progressRunnable;
        private long stopTimeRetrieve;
        private boolean isBlink = false;
        private TickTrackProgressBar timerProgressbar;

        TickTrackDatabase tickTrackDatabase;

        public timerDataViewHolder(@NonNull View parent) {
            super(parent);

            timerText = parent.findViewById(R.id.quickTimerItemTimerText);
            timerResetButton = parent.findViewById(R.id.quickTimerItemResetFAB);
            timerLayout = parent.findViewById(R.id.quickTimerItemRootLayout);
            timerFlag = parent.findViewById(R.id.quickTimerItemImageView);
            timerProgressbar = parent.findViewById(R.id.quickTimerItemTimerProgress);

            context=parent.getContext();
            tickTrackDatabase = new TickTrackDatabase(context);

        }
    }

}
