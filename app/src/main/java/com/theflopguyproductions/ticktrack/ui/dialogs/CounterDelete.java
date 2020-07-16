package com.theflopguyproductions.ticktrack.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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

public class CounterDelete  extends Dialog {
    public Activity activity;
    int position;
    private String counterName;
    RecyclerView.ViewHolder viewHolder;
    TextView dialogMessage;

    public CounterDelete(Activity activity, int position, String counterName, RecyclerView.ViewHolder viewHolder){
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

        View view = activity.getLayoutInflater().inflate(R.layout.alert_delete_dialog, new ConstraintLayout(activity), false);
        setContentView(view);

        yesButton = (Button) view.findViewById(R.id.yesButton);
        noButton = (Button) view.findViewById(R.id.noButton);
        dialogMessage = (TextView) view.findViewById(R.id.deleteDialogMessage);
        dialogMessage.setText("Delete counter "+counterName+"?");
        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

        yesButton.setOnClickListener(view12 -> {
            CounterFragment.yesToDelete(position, activity, counterName);
            dismiss();
        });
        noButton.setOnClickListener(view1 -> {
            CounterFragment.noToDelete(viewHolder);
            dismiss();
        });
        setOnCancelListener(dialogInterface -> {
            CounterFragment.noToDelete(viewHolder);
            dismiss();
        });
    }

    public Button yesButton;
    public Button noButton;

}
