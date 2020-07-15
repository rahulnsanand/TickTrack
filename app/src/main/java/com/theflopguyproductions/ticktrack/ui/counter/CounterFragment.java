package com.theflopguyproductions.ticktrack.ui.counter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.MainActivity;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.activity.counter.CounterActivity;
import com.theflopguyproductions.ticktrack.ui.dialogs.CounterCreator;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;

public class CounterFragment extends Fragment {

//    private SwipeButton plusButton;
//    private SwipeButton minusButton;
//    private TextView CounterText;
//    private int newCount=0;

    private Vibrator hapticFeed;
    private static Context context;
    private RecyclerView recyclerView;
    private static CounterAdapter counterAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private static ArrayList<CounterData> counterDataArrayList = new ArrayList<>();
    private static ConstraintLayout mainActivityLayout;

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

        View root = inflater.inflate(R.layout.fragment_counter_tab, container, false);

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


        context = getContext();

        loadRecycleCounter();
        recyclerView = root.findViewById(R.id.counterRecycleView);
        buildRecyclerView();



        return root;
    }

    public void showNoticeDialog() {
        CounterCreator dialog = new CounterCreator(getActivity());
        dialog.show();
    }

    public void fabClicked() {
        showNoticeDialog();
    }

    public static void onDialogPositiveClick(String label, String lastUpdate, int color) {

        CounterData counterData = new CounterData();
        counterData.setCounterLabel(label);
        counterData.setCountValue("0");
        counterData.setLastUpdateTimeStamp(lastUpdate);
        counterData.setLabelColor(color);
        counterDataArrayList.add(0,counterData);
        counterAdapter.notifyData(counterDataArrayList);
        StoreCounter();
    }

    private static void StoreCounter() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(counterDataArrayList);
        editor.putString("CounterData", json);
        editor.apply();
    }

    private void buildRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        counterAdapter = new CounterAdapter(counterDataArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(counterAdapter);
    }

    private static void loadRecycleCounter(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("CounterData", null);
        Type type = new TypeToken<ArrayList<CounterData>>() {}.getType();
        counterDataArrayList = gson.fromJson(json, type);

        if(counterDataArrayList == null){
            counterDataArrayList = new ArrayList<>();
        }
    }

    //static MainActivity mainActivity = new MainActivity();
    public void counterLayout(int position){
        System.out.println(counterDataArrayList.get(position).countValue);
        Intent intent = new Intent(getActivity(), CounterActivity.class);
        startActivity(intent);
    }


    public static void deleteItem(int position){
        counterDataArrayList.remove(position);
        StoreCounter();
        counterAdapter.notifyData(counterDataArrayList);
    }

}