package com.theflopguyproductions.ticktrack.ui.counter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;

import java.util.ArrayList;

public class CounterAdapter extends RecyclerView.Adapter<CounterAdapter.RecyclerItemViewHolder>   {

    private ArrayList<CounterData> myList;
    int mLastPosition = 0;

    public CounterAdapter(ArrayList<CounterData> myList) {
        this.myList = myList;
    }


    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.counter_list_item, parent, false);
        RecyclerItemViewHolder holder = new RecyclerItemViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, final int position) {
        Log.d("onBindViewHolder ", myList.size() + "");
        holder.countValue.setText(myList.get(position).getCountValue());
        holder.counterLabel.setText(myList.get(position).getCounterLabel());
        holder.lastModified.setText(myList.get(position).getLastUpdateTimeStamp());
        holder.itemColor = myList.get(position).getLabelColor();
        setColor(holder);
        mLastPosition = position;
    }

    private void setColor(RecyclerItemViewHolder holder) {
        if(holder.itemColor==1){
            holder.coloredLayout.setBackgroundResource(R.drawable.round_rect_blue);
            System.out.println("Nebula - Blue");
        }
        if(holder.itemColor==2){
            holder.coloredLayout.setBackgroundResource(R.drawable.round_rect_black);
            System.out.println("Gargantua - Black");
        }
        if(holder.itemColor==3){
            holder.coloredLayout.setBackgroundResource(R.drawable.round_rect_green);
            System.out.println("Vortex - Green");
        }
        if(holder.itemColor==4){
            holder.coloredLayout.setBackgroundResource(R.drawable.round_rect_purple);
            System.out.println("Unicorn - Purple");
        }
    }

    @Override
    public int getItemCount() {
        return(null != myList?myList.size():0);
    }
    public void notifyData(ArrayList<CounterData> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.myList = myList;
        notifyDataSetChanged();
    }

    public static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView countValue;
        private final TextView counterLabel;
        private final TextView lastModified;
        private ConstraintLayout countLayout;

        private int itemColor;
        private ConstraintLayout coloredLayout;

        public RecyclerItemViewHolder(final View parent) {
            super(parent);
            countValue = (TextView) parent.findViewById(R.id.counterValue);
            counterLabel = (TextView) parent.findViewById(R.id.counterLabel);
            lastModified = (TextView) parent.findViewById(R.id.lastUpdated);
            countLayout = (ConstraintLayout) parent.findViewById(R.id.countRecycleLayout);
            coloredLayout = (ConstraintLayout) parent.findViewById(R.id.counterItem);
            countLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Position:" + Integer.toString(getPosition()), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


}
