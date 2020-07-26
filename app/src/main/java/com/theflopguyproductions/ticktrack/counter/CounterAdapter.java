package com.theflopguyproductions.ticktrack.counter;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CounterAdapter extends RecyclerView.Adapter<CounterAdapter.RecyclerItemViewHolder> {

    private ArrayList<CounterData> counterDataArrayList;
    int mLastPosition = 0;

    public CounterAdapter(ArrayList<CounterData> myList) {
        this.counterDataArrayList = myList;
    }

    @NonNull
    @Override
    public CounterAdapter.RecyclerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.counter_item_layout, parent, false);
        RecyclerItemViewHolder holder = new RecyclerItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CounterAdapter.RecyclerItemViewHolder holder, int position) {
        holder.countValue.setText(""+counterDataArrayList.get(position).getCounterValue());
        holder.counterLabel.setText(counterDataArrayList.get(position).getCounterLabel());

        if(counterDataArrayList.get(position).getCounterTimestamp()!=null){
            holder.lastModified.setText("Last modified : "+timestampReadableFormat.format(counterDataArrayList.get(position).getCounterTimestamp()));
        }

        holder.itemColor = counterDataArrayList.get(position).getCounterFlag();
        setColor(holder);

        mLastPosition = position;
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
        return(null != counterDataArrayList?counterDataArrayList.size():0);
    }

    public void notifyData(ArrayList<CounterData> myList) {
        this.counterDataArrayList = myList;
        notifyDataSetChanged();
    }

    public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        private TextView countValue, lastModified, counterLabel;
        private ConstraintLayout counterLayout;
        private int itemColor;
        private ImageView counterFlag;

        public RecyclerItemViewHolder(@NonNull View parent) {
            super(parent);

            countValue = parent.findViewById(R.id.counterValueItemTextView);
            counterLabel = parent.findViewById(R.id.counterLabelItemTextView);
            lastModified = parent.findViewById(R.id.counterLastUpdateItemTextView);
            counterLayout = parent.findViewById(R.id.counterItemRootLayout);
            counterFlag = parent.findViewById(R.id.counterFlagItemImageView);

            counterLayout.setOnClickListener(v -> {
//                MainActivityToChange.counterActivity(getAdapterPosition());
                Toast.makeText(itemView.getContext(), "Position:" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            });
            counterLayout.setOnLongClickListener(view -> {
//                    Toast.makeText(itemView.getContext(), myList.get(getAdapterPosition()).getCounterLabel(), Toast.LENGTH_SHORT).show();

                return false;
            });

        }



    }
}
