package com.theflopguyproductions.ticktrack.timer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterAdapter;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TimeAgo;

import java.util.ArrayList;
import java.util.Random;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.RecyclerItemViewHolder> {

    int mLastPosition = 0;
    private ArrayList<TimerData> timerDataArrayList;

    public TimerAdapter(ArrayList<TimerData> myList) {
        this.timerDataArrayList = myList;
    }

    @NonNull
    public TimerAdapter.RecyclerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        if(viewType == R.layout.timer_item_layout){
            //TODO CHANGE ITEM LAYOUT
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_item_layout, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer_layout, parent, false);
        }

        return new TimerAdapter.RecyclerItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerItemViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        int theme = TickTrackDatabase.getThemeMode((Activity) holder.context);

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

            holder.timerTitle.setText(TimeAgo.getTimerTitle(timerDataArrayList.get(position).getTimerHour(),timerDataArrayList.get(position).getTimerMinute(),
                    timerDataArrayList.get(position).getTimerSecond()));

            if(!timerDataArrayList.get(position).getTimerLabel().equals("Set label")) {
                holder.timerLabel.setText(timerDataArrayList.get(position).getTimerLabel());
            } else {
                holder.timerLabel.setVisibility(View.INVISIBLE);
            }

            if(timerDataArrayList.get(position).isTimerOn()){
                holder.timerDurationLeft.setVisibility(View.VISIBLE);
                holder.timerDurationLeft.setText("DURATION LEFT");
                holder.timerPauseResetButton.setVisibility(View.VISIBLE);
            } else {
                holder.timerDurationLeft.setVisibility(View.INVISIBLE);
                holder.timerDurationLeft.setText("DURATION LEFT");
                holder.timerPauseResetButton.setVisibility(View.INVISIBLE);
            }



            holder.itemColor = timerDataArrayList.get(position).timerFlag;

            setColor(holder);
            setTheme(holder, theme);

            holder.timerLayout.setOnClickListener(v -> {
                TimerFragment.startTimerActivity(position, (Activity) holder.context);
                Toast.makeText(holder.context, "Position:" + position, Toast.LENGTH_SHORT).show();
            });
        }

        mLastPosition = position;
    }

    private void setTheme(TimerAdapter.RecyclerItemViewHolder holder, int theme) {
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

    private void setColor(TimerAdapter.RecyclerItemViewHolder holder) {
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
            holder.timerFlag.setVisibility(View.INVISIBLE);
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


    public static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        private TextView timerTitle, timerLabel, timerDurationLeft;
        public ConstraintLayout timerLayout;
        private int itemColor;
        private ImageView timerFlag;
        private Context context;
        private TextView footerCounterTextView;
        private Button timerPauseResetButton;

        public RecyclerItemViewHolder(@NonNull View parent) {
            super(parent);

            timerTitle = parent.findViewById(R.id.timerTitleItemTextView);
            timerLabel = parent.findViewById(R.id.timerLabelItemTextView);
            timerDurationLeft = parent.findViewById(R.id.timerDurationLeftItemTextView);
            timerLayout = parent.findViewById(R.id.timerItemRootLayout);
            timerFlag = parent.findViewById(R.id.timerFlagItemImageView);
            timerPauseResetButton = parent.findViewById(R.id.timerPauseResetItemButton);
            footerCounterTextView = parent.findViewById(R.id.recylerFooterTextView);

            context=parent.getContext();

        }
    }

}
