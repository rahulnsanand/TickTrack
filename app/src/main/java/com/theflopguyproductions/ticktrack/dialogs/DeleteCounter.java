package com.theflopguyproductions.ticktrack.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;

import java.util.Objects;

public class DeleteCounter  extends Dialog {
    public Activity activity;
    int position;
    private String counterName;
    RecyclerView.ViewHolder viewHolder;
    TextView dialogMessage;

    public DeleteCounter(Activity activity, int position, String counterName, RecyclerView.ViewHolder viewHolder){
        super(activity);
        this.viewHolder = viewHolder;
        this.counterName = counterName;
        this.position = position;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_counter_deletor, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        yesButton = (Button) view.findViewById(R.id.acceptDeleteCounterButton);
        noButton = (Button) view.findViewById(R.id.dismissDeleteCounterButton);
        dialogMessage = (TextView) view.findViewById(R.id.deleteCounterTextView);

        dialogMessage.setText("Delete counter "+counterName+"?");
        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

        yesButton.setOnClickListener(view12 -> {
            CounterFragment.deleteCounter(position, activity, counterName);
            dismiss();
        });
        noButton.setOnClickListener(view1 -> {
            CounterFragment.refreshRecyclerView();
            dismiss();
        });
        setOnCancelListener(dialogInterface -> {
            CounterFragment.refreshRecyclerView();
            dismiss();
        });
    }

    public Button yesButton;
    public Button noButton;

}
