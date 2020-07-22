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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.activity.counter.CounterActivity;
import com.theflopguyproductions.ticktrack.ui.dialogs.CounterCreator;
import com.theflopguyproductions.ticktrack.ui.dialogs.CounterDelete;
import com.theflopguyproductions.ticktrack.ui.utils.deletehelper.CounterSlideDeleteHelper;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;

public class CounterFragment extends Fragment  implements CounterSlideDeleteHelper.RecyclerItemTouchHelperListener {



    private static Context context;
    private static Vibrator hapticFeed;
    private RecyclerView counterRecyclerView;
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

    RecyclerView.ViewHolder viewHolder;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_counter_tab, container, false);
        hapticFeed = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);

        context = getContext();
        loadCounterData();
        counterRecyclerView = root.findViewById(R.id.counterRecycleView);
        buildRecyclerView();

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new CounterSlideDeleteHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(counterRecyclerView);

        return root;
    }

    public void showNoticeDialog() {
        CounterCreator dialog = new CounterCreator(getActivity());
        dialog.show();
    }

    public void fabClicked() {
        showNoticeDialog();
    }

    public static void onDialogPositiveClick(String label, Timestamp lastUpdate, int color) {

        CounterData counterData = new CounterData();
        counterData.setCounterLabel(label);
        counterData.setCountValue(0);
        counterData.setTimestamp(lastUpdate);
        counterData.setLabelColor(color);
        counterDataArrayList.add(0,counterData);
        counterAdapter.notifyData(counterDataArrayList);
        StoreCounter();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCounterData();
        buildRecyclerView();
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
        counterRecyclerView.setItemAnimator(new DefaultItemAnimator());
        counterRecyclerView.setHasFixedSize(true);

        Collections.sort(counterDataArrayList);

        counterAdapter = new CounterAdapter(counterDataArrayList);
        counterRecyclerView.setLayoutManager(layoutManager);
        counterRecyclerView.setAdapter(counterAdapter);
        counterAdapter.notifyDataSetChanged();
    }

    private static void loadCounterData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("CounterData", null);
        Type type = new TypeToken<ArrayList<CounterData>>() {}.getType();
        counterDataArrayList = gson.fromJson(json, type);

        if(counterDataArrayList == null){
            counterDataArrayList = new ArrayList<>();
        }
    }

    String deletedCounter = null;

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CounterAdapter.RecyclerItemViewHolder) {
            deletedCounter = counterDataArrayList.get(viewHolder.getAdapterPosition()).getCounterLabel();
            position = viewHolder.getAdapterPosition();

            CounterDelete counterDelete = new CounterDelete(getActivity(), position, deletedCounter, viewHolder);
            counterDelete.show();

        }
    }

    public static void yesToDelete(int position, Activity activity, String CounterName) {
        deleteItem(position);
        Toast.makeText(activity, "Deleted Counter " + CounterName, Toast.LENGTH_SHORT).show();
    }

    public static void noToDelete(RecyclerView.ViewHolder viewHolder) {
        counterAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
    }

    public static void counterLayout(Context context, int position){
        Intent intent = new Intent(context, CounterActivity.class);
        intent.putExtra("currentCounterPosition",position);
        context.startActivity(intent);
    }

    public static void deleteItem(int position){
        counterDataArrayList.remove(position);
        StoreCounter();
        hapticFeed.vibrate(50);
        counterAdapter.notifyData(counterDataArrayList);
    }
}