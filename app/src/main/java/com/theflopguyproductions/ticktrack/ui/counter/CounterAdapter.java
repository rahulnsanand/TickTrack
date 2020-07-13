package com.theflopguyproductions.ticktrack.ui.counter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
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
        Log.d("onBindViewHoler ", myList.size() + "");
        holder.countValue.setText(myList.get(position).getCountValue());
        holder.counterLabel.setText(myList.get(position).getCounterLabel());
        holder.lastModified.setText(myList.get(position).getLastUpdateTimeStamp());
        mLastPosition = position;
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



    public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {


        private final TextView countValue;
        private final TextView counterLabel;
        private final TextView lastModified;
        private ConstraintLayout countLayout;



        public RecyclerItemViewHolder(final View parent) {
            super(parent);
            countValue = (TextView) parent.findViewById(R.id.counterValue);
            counterLabel = (TextView) parent.findViewById(R.id.counterLabel);
            lastModified = (TextView) parent.findViewById(R.id.lastUpdated);
            countLayout = (ConstraintLayout) parent.findViewById(R.id.countRecycleLayout);
            countLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Position:" + Integer.toString(getPosition()), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
