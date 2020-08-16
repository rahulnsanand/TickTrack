package com.theflopguyproductions.ticktrack.timer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TimeAgo;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.timerDataViewHolder> {

    private ArrayList<TimerData> timerDataArrayList;
    private Handler timerStatusUpdateHandler = new Handler();

    public TimerAdapter(Context context, ArrayList<TimerData> timerDataArrayList) {
        this.timerDataArrayList = timerDataArrayList;
    }

    @NonNull
    public timerDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        if(viewType == R.layout.timer_item_layout){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_item_layout, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer_layout, parent, false);
        }

        return new timerDataViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull timerDataViewHolder holder, int position) {

        int theme = holder.tickTrackDatabase.getThemeMode();



        if(position == timerDataArrayList.size()) {
            if(theme == 1){
                holder.footerCounterTextView.setTextColor(holder.context.getResources().getColor(R.color.DarkText));
            } else {
                holder.footerCounterTextView.setTextColor(holder.context.getResources().getColor(R.color.LightText));
            }
            Resources resources = holder.context.getResources();
            String[] footerArray = resources.getStringArray(R.array.footer_string_array);
            int randomFooter = new Random().nextInt(footerArray.length);
            holder.footerCounterTextView.setText(footerArray[randomFooter]);
        } else {
            boolean isNotification = timerDataArrayList.get(holder.getAdapterPosition()).isTimerNotificationOn();
            holder.timerTitle.setText(TimeAgo.getTimerTitle(timerDataArrayList.get(position).getTimerHour(),timerDataArrayList.get(position).getTimerMinute(),
                    timerDataArrayList.get(position).getTimerSecond()));

            if(!timerDataArrayList.get(position).getTimerLabel().equals("Set label")) {
                holder.timerLabel.setText(timerDataArrayList.get(position).getTimerLabel());
            } else {
                holder.timerLabel.setVisibility(View.GONE);
            }

            holder.stopTimeRetrieve = timerDataArrayList.get(holder.getAdapterPosition()).getTimerAlarmEndTimeInMillis() - System.currentTimeMillis();

            holder.timerRunnable = () -> {
                if(isNotification){
                    if(holder.stopTimeRetrieve>0){
                        holder.stopTimeRetrieve -= 1000;
                        holder.timerDurationLeft.setText(updateTimerTextView(holder.stopTimeRetrieve));
                    } else {
                        holder.timerDurationLeft.setText("Timer elapsed");
                    }
                    timerStatusUpdateHandler.postDelayed(holder.timerRunnable, 1000);
                }
            };

            if(isNotification){
                timerStatusUpdateHandler.postDelayed(holder.timerRunnable, 0);
            } else {
                timerStatusUpdateHandler.removeCallbacks(holder.timerRunnable);
                if(timerDataArrayList.get(position).isTimerRinging()){
                    holder.timerDurationLeft.setText("Timer elapsed");
                } else {
                    holder.timerDurationLeft.setVisibility(View.INVISIBLE);
                }
            }

            holder.itemColor = timerDataArrayList.get(position).timerFlag;

            setColor(holder);
            setTheme(holder, theme);

            holder.timerLayout.setOnClickListener(v -> TimerFragment.startTimerActivity(holder.getAdapterPosition(), (Activity) holder.context));



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
            holder.timerLabel.setTextColor(holder.context.getResources().getColor(R.color.Gray));
            holder.timerTitle.setTextColor(holder.context.getResources().getColor(R.color.Gray));
        } else {
            holder.timerLayout.setBackgroundResource(R.drawable.recycler_layout_dark);
            holder.timerLabel.setTextColor(holder.context.getResources().getColor(R.color.LightText));
            holder.timerTitle.setTextColor(holder.context.getResources().getColor(R.color.LightText));
        }
        holder.timerDurationLeft.setTextColor(holder.context.getResources().getColor(R.color.Accent));
        holder.timerPauseResetButton.setTextColor(holder.context.getResources().getColor(R.color.Accent));
    }

    private void setColor(timerDataViewHolder holder) {
        if(holder.itemColor==1){
            holder.timerFlag.setImageResource(R.drawable.ic_flag_red);
        }
        else if(holder.itemColor==2){
            holder.timerFlag.setImageResource(R.drawable.ic_flag_green);
        }
        else if(holder.itemColor==3){
            holder.timerFlag.setImageResource(R.drawable.ic_flag_orange);
        }
        else if(holder.itemColor==4){
            holder.timerFlag.setImageResource(R.drawable.ic_flag_purple);
        }
        else if(holder.itemColor==5){
            holder.timerFlag.setImageResource(R.drawable.ic_flag_blue);
        } else {
            holder.timerFlag.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return timerDataArrayList.size() + 1;
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

    public static class timerDataViewHolder extends RecyclerView.ViewHolder {

        private TextView timerTitle, timerLabel, timerDurationLeft;
        public ConstraintLayout timerLayout;
        private int itemColor;
        private ImageView timerFlag;
        private Context context;
        private TextView footerCounterTextView;
        private Button timerPauseResetButton;
        private Runnable timerRunnable;
        private long stopTimeRetrieve;

        TickTrackDatabase tickTrackDatabase;

        public timerDataViewHolder(@NonNull View parent) {
            super(parent);

            timerTitle = parent.findViewById(R.id.timerTitleItemTextView);
            timerLabel = parent.findViewById(R.id.timerLabelItemTextView);
            timerDurationLeft = parent.findViewById(R.id.timerDurationLeftItemTextView);
            timerLayout = parent.findViewById(R.id.timerItemRootLayout);
            timerFlag = parent.findViewById(R.id.timerFlagItemImageView);
            timerPauseResetButton = parent.findViewById(R.id.timerPauseResetItemButton);
            footerCounterTextView = parent.findViewById(R.id.recylerFooterTextView);

            context=parent.getContext();
            tickTrackDatabase = new TickTrackDatabase(context);

        }
    }

}
