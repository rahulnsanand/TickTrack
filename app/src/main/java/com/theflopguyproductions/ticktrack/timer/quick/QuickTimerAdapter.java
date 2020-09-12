package com.theflopguyproductions.ticktrack.timer.quick;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
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

    private ArrayList<QuickTimerData> timerDataArrayList;
    private Handler timerStatusUpdateHandler = new Handler();
    private Handler timerElapsedBlinkHandler = new Handler();
    private Handler timerProgressHandler = new Handler();
    private Handler timerRelapsedHandler = new Handler();
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackTimerDatabase tickTrackTimerDatabase;
    private Context context;

    public QuickTimerAdapter(Context context, ArrayList<QuickTimerData> timerDataArrayList) {
        this.timerDataArrayList = timerDataArrayList;
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
        timerRelapsedHandler.removeCallbacks(holder.elapsedRunnable);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull timerDataViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(!timerDataArrayList.get(holder.getAdapterPosition()).isTimerRinging()){

            holder.timerResetButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_round_refresh_white_24));
            System.out.println("THIS IF RAN");

            timerStatusUpdateHandler.post(holder.timerRunnable);
            holder.timerResetButton.setOnClickListener(view -> resetAndRemoveQuickTimer(holder.getAdapterPosition(), holder));

        } else {
            timerStatusUpdateHandler.removeCallbacks(holder.timerRunnable);
            holder.timerProgressbar.setProgress(1f);
            timerRelapsedHandler.post(holder.elapsedRunnable);
            timerElapsedBlinkHandler.post(holder.blinkRunnable);
            holder.timerResetButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_stop_white_24));

            holder.timerResetButton.setOnClickListener(view -> deleteQuickTimer(holder.getAdapterPosition(), holder));

        }

    }

    private float getCurrentStep(long currentValue, long maxLength){
        return ((currentValue-0f)/(maxLength-0f)) *(1f-0f)+0f;
    }
    @Override
    public void onBindViewHolder(@NonNull timerDataViewHolder holder, int position) {

        int theme = holder.tickTrackDatabase.getThemeMode();
        setTheme(holder, theme);

        long totalTimeInMillis = timerDataArrayList.get(holder.getAdapterPosition()).getTimerTotalTimeInMillis();
        long startTime = timerDataArrayList.get(holder.getAdapterPosition()).getTimerAlarmEndTimeInMillis();

        holder.timerRunnable = () -> {
            if(!timerDataArrayList.get(holder.getAdapterPosition()).isTimerOn()){
                timerStatusUpdateHandler.removeCallbacks(holder.timerRunnable);
                return;
            }
            long durationLeft = (startTime-SystemClock.elapsedRealtime());
            holder.timerText.setText(updateTimerTextView(durationLeft));
            timerStatusUpdateHandler.post(holder.timerRunnable);
            System.out.println("Timer Update Position: "+holder.getAdapterPosition());
            holder.timerProgressbar.setProgress(getCurrentStep(durationLeft, totalTimeInMillis));
        };

        long elapsedTime = timerDataArrayList.get(holder.getAdapterPosition()).getTimerEndedTimeInMillis();
        holder.elapsedRunnable = () -> {
            if(!timerDataArrayList.get(holder.getAdapterPosition()).isTimerOn()){
                timerRelapsedHandler.removeCallbacks(holder.elapsedRunnable);
                return;
            }
            long durationElapsed = SystemClock.elapsedRealtime() - elapsedTime;
            holder.timerText.setText("-"+updateTimerTextView(durationElapsed));
            timerRelapsedHandler.postDelayed(holder.elapsedRunnable, 100);
            System.out.println("Timer Update Position: "+holder.getAdapterPosition());
        };

        final boolean[] isBlink = {true};
        holder.blinkRunnable = () -> {
            if(!timerDataArrayList.get(holder.getAdapterPosition()).isTimerRinging()){
                timerElapsedBlinkHandler.removeCallbacks(holder.blinkRunnable);
                return;
            }
            if(isBlink[0]){
                holder.timerProgressbar.setVisibility(View.VISIBLE);
                isBlink[0] = false;
            } else {
                holder.timerProgressbar.setVisibility(View.INVISIBLE);
                isBlink[0] = true;
            }
            timerElapsedBlinkHandler.postDelayed(holder.blinkRunnable, 500);
        };

        holder.timerLayout.setOnLongClickListener(view -> deleteQuickTimer(holder.getAdapterPosition(), holder));

    }

    private void resetAndRemoveQuickTimer(int position, @NonNull timerDataViewHolder holder) {
        timerDataArrayList.get(position).setTimerOn(false);
        timerDataArrayList.get(position).setTimerPause(false);
        timerDataArrayList.get(position).setTimerEndedTimeInMillis(-1);
        timerDataArrayList.get(position).setTimerStartTimeInMillis(-1);
        tickTrackTimerDatabase.cancelAlarm(timerDataArrayList.get(position).getTimerIntID(), true);
        timerDataArrayList.remove(position);

        if(tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
            tickTrackTimerDatabase.stopNotificationService();
        }

        System.out.println("THIS IF CONDITION RAN BITCH"+position);

        timerStatusUpdateHandler.removeCallbacks(holder.timerRunnable);
        timerElapsedBlinkHandler.removeCallbacks(holder.blinkRunnable);
        timerProgressHandler.removeCallbacks(holder.progressRunnable);
        timerRelapsedHandler.removeCallbacks(holder.elapsedRunnable);
        System.out.println("THIS IF CONDITION RAN BITCH"+position);
        tickTrackDatabase.storeQuickTimerList(timerDataArrayList);
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    private boolean deleteQuickTimer(int position, @NonNull timerDataViewHolder holder) {
        timerDataArrayList.get(position).setTimerOn(false);
        timerDataArrayList.get(position).setTimerPause(false);
        timerDataArrayList.get(position).setTimerEndedTimeInMillis(-1);
        timerDataArrayList.get(position).setTimerStartTimeInMillis(-1);
        tickTrackTimerDatabase.cancelAlarm(timerDataArrayList.get(position).getTimerIntID(), true);
        timerDataArrayList.remove(position);

        if(tickTrackTimerDatabase.isMyServiceRunning(TimerRingService.class)){
            tickTrackTimerDatabase.stopRingService();
        }

        timerStatusUpdateHandler.removeCallbacks(holder.timerRunnable);
        timerElapsedBlinkHandler.removeCallbacks(holder.blinkRunnable);
        timerProgressHandler.removeCallbacks(holder.progressRunnable);
        timerRelapsedHandler.removeCallbacks(holder.elapsedRunnable);
        tickTrackDatabase.storeQuickTimerList(timerDataArrayList);
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

    public void diffUtilsChangeData(ArrayList<QuickTimerData> timerDataArrayList){

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
        private Runnable timerRunnable, blinkRunnable, progressRunnable, elapsedRunnable;
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

            timerProgressbar.setLinearProgress(true);
            timerProgressbar.setSpinSpeed(2.500f);

        }

    }

}
