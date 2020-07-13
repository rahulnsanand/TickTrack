package com.theflopguyproductions.ticktrack.ui.counter;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.MainActivity;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.home.HomeFragment;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.OnActiveListener;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;

import java.util.ArrayList;

import static android.content.Context.VIBRATOR_SERVICE;

public class CounterFragment extends Fragment {

//    private SwipeButton plusButton;
//    private SwipeButton minusButton;
//    private TextView CounterText;
//    private int newCount=0;

    private Vibrator hapticFeed;

    private RecyclerView recyclerView;
    private CounterAdapter counterAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    ArrayList<CounterData> counterDataArrayList = new ArrayList<>();

    private static final String DISABLED_PARAM = "param2";

    public CounterFragment() {
    }

    public static CounterFragment newInstance(boolean disabled) {
        CounterFragment fragment = new CounterFragment();
        Bundle args = new Bundle();
        args.putBoolean(DISABLED_PARAM, disabled);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_counter, container, false);
//        CounterText = root.findViewById(R.id.counterText);
//        CounterText.setText(""+newCount);
//        plusButton = root.findViewById(R.id.plusbtn);
//        minusButton = root.findViewById(R.id.minusbtn);
        hapticFeed = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
//        plusButton.setOnActiveListener(new OnActiveListener() {
//            @Override
//            public void onActive() {
//                newCount+=1;
//                hapticFeed.vibrate(50);
//                CounterText.setText(""+newCount);
//            }
//
//        });
//        minusButton.setOnActiveListener(new OnActiveListener() {
//            @Override
//            public void onActive() {
//                if(newCount>=1){
//                    hapticFeed.vibrate(50);
//                    newCount-=1;
//                    CounterText.setText(""+newCount);
//                }
//            }
//
//        });

        recyclerView = root.findViewById(R.id.counterRecycleView);
        counterAdapter = new CounterAdapter(counterDataArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(counterAdapter);


        return root;
    }

    public void fabClicked() {
        CounterData counterData = new CounterData();
        counterData.setCounterLabel("LABEL");
        counterData.setCountValue("VALUE");
        counterData.setLastUpdateTimeStamp("TIMESTAMP");
        counterDataArrayList.add(counterData);
        counterAdapter.notifyData(counterDataArrayList);
    }
}