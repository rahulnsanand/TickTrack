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
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class QuickTimerAdapter extends RecyclerView.Adapter<QuickTimerAdapter.timerDataViewHolder> {

    private ArrayList<TimerData> timerDataArrayList;
    private ArrayList<TimerData> timerUnusedDataArrayList;
    private Handler timerStatusUpdateHandler = new Handler();
    private Handler timerElapsedBlinkHandler = new Handler();
    private Handler timerProgressHandler = new Handler();
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackTimerDatabase tickTrackTimerDatabase;
    private Context context;

    public QuickTimerAdapter(Context context, ArrayList<TimerData> timerDataArrayList, ArrayList<TimerData> timerUnusedDataArrayList) {
        this.timerDataArrayList = timerDataArrayList;
        this.timerUnusedDataArrayList = timerUnusedDataArrayList;
        this.context = context;
        tickTrackDatabase = new TickTrackDatabase(context);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(context);
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

        holder.timerProgressbar.spin();

        long endTime = timerDataArrayList.get(holder.getAdapterPosition()).getTimerAlarmEndTimeInMillis();
        long startTime = timerDataArrayList.get(holder.getAdapterPosition()).getTimerStartTimeInMillis();

        holder.timerRunnable = () -> {
            long durationLeft = endTime - (System.currentTimeMillis()-startTime);
            holder.timerText.setText(updateTimerTextView(durationLeft));
            timerStatusUpdateHandler.postDelayed(holder.timerRunnable, 100);
            System.out.println("Timer Update Position: "+holder.getAdapterPosition());
        };

        if(timerDataArrayList.get(holder.getAdapterPosition()).isQuickTimer() && !timerDataArrayList.get(holder.getAdapterPosition()).isTimerRinging()){

            holder.timerResetButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_round_refresh_white_24));
            System.out.println("THIS IF RAN");

            timerStatusUpdateHandler.post(holder.timerRunnable);

            holder.timerResetButton.setOnClickListener(view -> resetAndRemoveQuickTimer(holder.getAdapterPosition()));

        } if(timerDataArrayList.get(holder.getAdapterPosition()).isQuickTimer() && timerDataArrayList.get(holder.getAdapterPosition()).isTimerRinging()){

            holder.timerResetButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_stop_white_24));

            timerStatusUpdateHandler.removeCallbacks(holder.timerRunnable);
            holder.timerResetButton.setOnClickListener(view -> deleteQuickTimer(holder.getAdapterPosition()));

        }

        holder.timerLayout.setOnLongClickListener(view -> deleteQuickTimer(holder.getAdapterPosition()));

    }

    private void resetAndRemoveQuickTimer(int position) {
        for(int i=0; i<timerUnusedDataArrayList.size(); i++){
            if(timerUnusedDataArrayList.get(i).getTimerIntID()==timerDataArrayList.get(position).getTimerIntID()){
                timerUnusedDataArrayList.get(i).setTimerOn(false);
                timerUnusedDataArrayList.get(i).setTimerPause(false);
                timerUnusedDataArrayList.get(i).setTimerEndedTimeInMillis(-1);
                timerUnusedDataArrayList.get(i).setTimerStartTimeInMillis(-1);
                tickTrackDatabase.storeTimerList(timerUnusedDataArrayList);
                if(tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                    tickTrackTimerDatabase.stopNotificationService();
                }
                timerUnusedDataArrayList.remove(i);
            }
        }
        tickTrackDatabase.storeTimerList(timerUnusedDataArrayList);
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    private boolean deleteQuickTimer(int position) {
        for(int i=0; i<timerUnusedDataArrayList.size(); i++){
            if(timerUnusedDataArrayList.get(i).getTimerIntID()==timerDataArrayList.get(position).getTimerIntID()){
                timerUnusedDataArrayList.get(i).setTimerOn(false);
                timerUnusedDataArrayList.get(i).setTimerPause(false);
                timerUnusedDataArrayList.get(i).setTimerEndedTimeInMillis(-1);
                timerUnusedDataArrayList.get(i).setTimerStartTimeInMillis(-1);
                tickTrackDatabase.storeTimerList(timerUnusedDataArrayList);
                if(tickTrackTimerDatabase.isMyServiceRunning(TimerRingService.class)){
                    tickTrackTimerDatabase.stopRingService();
                }
                timerUnusedDataArrayList.remove(i);
            }
        }
        tickTrackDatabase.storeTimerList(timerUnusedDataArrayList);
        return true;
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

        QuickTimerDiffUtilCallback quickTimerDiffUtilCallback = new QuickTimerDiffUtilCallback(timerDataArrayList, this.timerDataArrayList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(quickTimerDiffUtilCallback, false);
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
