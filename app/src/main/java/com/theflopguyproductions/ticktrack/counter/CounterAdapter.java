package com.theflopguyproductions.ticktrack.counter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CounterAdapter extends RecyclerView.Adapter<CounterAdapter.RecyclerItemViewHolder> {

    private ArrayList<CounterData> counterDataArrayList;
    int mLastPosition = 0;

    public CounterAdapter(ArrayList<CounterData> myList) {
        this.counterDataArrayList = myList;
    }

    @NonNull
    public RecyclerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        if(viewType == R.layout.counter_item_layout){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.counter_item_layout, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer_layout, parent, false);
        }

        return new RecyclerItemViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == counterDataArrayList.size()) ? R.layout.recycler_footer_layout : R.layout.counter_item_layout;
    }

    @Override
    public void onBindViewHolder(@NonNull CounterAdapter.RecyclerItemViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        int theme = TickTrackDatabase.getThemeMode((Activity) holder.context);

        if(position == counterDataArrayList.size()) {
            if(theme == 1){
                holder.footerCounterTextView.setTextColor(holder.context.getResources().getColor(R.color.DarkText));
            } else {
                holder.footerCounterTextView.setTextColor(holder.context.getResources().getColor(R.color.LightText));
            }
            holder.footerCounterTextView.setText("Gotta replace this");
        } else {
            holder.countValue.setText(""+counterDataArrayList.get(position).getCounterValue());
            holder.counterLabel.setText(counterDataArrayList.get(position).getCounterLabel());

            if(counterDataArrayList.get(position).getCounterTimestamp()!=null){
                holder.lastModified.setText("Last modified : "+timestampReadableFormat.format(counterDataArrayList.get(position).getCounterTimestamp()));
            }

            holder.itemColor = counterDataArrayList.get(position).getCounterFlag();
            setColor(holder);
            setTheme(holder, theme);

            holder.counterLayout.setOnClickListener(v -> {
                CounterFragment.startCounterActivity(position, (Activity) holder.context);
                Toast.makeText(holder.context, "Position:" + position, Toast.LENGTH_SHORT).show();
            });
            holder.counterLayout.setOnLongClickListener(view -> {
//                    Toast.makeText(itemView.getContext(), myList.get(getAdapterPosition()).getCounterLabel(), Toast.LENGTH_SHORT).show();
                return false;
            });
        }

        mLastPosition = position;
    }

    private void setTheme(RecyclerItemViewHolder holder, int theme) {
        if(theme == 1){
            holder.counterLayout.setBackgroundResource(R.color.Light);
            holder.counterLabel.setTextColor(holder.context.getResources().getColor(R.color.Gray));
            holder.lastModified.setTextColor(holder.context.getResources().getColor(R.color.Gray));
        } else {
            holder.counterLayout.setBackgroundResource(R.color.Gray);
            holder.counterLabel.setTextColor(holder.context.getResources().getColor(R.color.LightText));
            holder.lastModified.setTextColor(holder.context.getResources().getColor(R.color.LightText));
        }
        holder.countValue.setTextColor(holder.context.getResources().getColor(R.color.Accent));
    }

    private static final SimpleDateFormat timestampReadableFormat =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private void setColor(RecyclerItemViewHolder holder) {
        if(holder.itemColor==1){
            holder.counterFlag.setImageResource(R.drawable.ic_flag_red);
        }
        else if(holder.itemColor==2){
            holder.counterFlag.setImageResource(R.drawable.ic_flag_green);
        }
        else if(holder.itemColor==3){
            holder.counterFlag.setImageResource(R.drawable.ic_flag_orange);
        }
        else if(holder.itemColor==4){
            holder.counterFlag.setImageResource(R.drawable.ic_flag_blue);
        }
        else if(holder.itemColor==5){
            holder.counterFlag.setImageResource(R.drawable.ic_flag_purple);
        }
    }

    @Override
    public int getItemCount() {
        return counterDataArrayList.size() + 1;
    }

    public void notifyData(ArrayList<CounterData> myList) {
        this.counterDataArrayList = myList;
        notifyDataSetChanged();
    }

    public static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        private TextView countValue, lastModified, counterLabel;
        public ConstraintLayout counterLayout;
        private int itemColor;
        private ImageView counterFlag;
        private Context context;
        private TextView footerCounterTextView;

        public RecyclerItemViewHolder(@NonNull View parent) {
            super(parent);

            countValue = parent.findViewById(R.id.counterValueItemTextView);
            counterLabel = parent.findViewById(R.id.counterLabelItemTextView);
            lastModified = parent.findViewById(R.id.counterLastUpdateItemTextView);
            counterLayout = parent.findViewById(R.id.counterItemRootLayout);
            counterFlag = parent.findViewById(R.id.counterFlagItemImageView);
            footerCounterTextView = parent.findViewById(R.id.recylerFooterTextView);

            context=parent.getContext();

        }
    }
}
