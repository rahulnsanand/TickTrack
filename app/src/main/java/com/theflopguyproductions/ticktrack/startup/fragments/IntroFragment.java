package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;

public class IntroFragment extends Fragment {

    private OnGetStartedClickListener getStartedClickListener;

    private Button getStartedButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_intro, container, false);
        getStartedButton = root.findViewById(R.id.TickTrackIntroGetStartedButton);

        getStartedButton.setOnClickListener(view -> getStartedClickListener.onGetStartedClick());

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            getStartedClickListener = (OnGetStartedClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + OnGetStartedClickListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        getStartedClickListener = null;
    }
    public interface OnGetStartedClickListener {
        void onGetStartedClick();
    }
}
