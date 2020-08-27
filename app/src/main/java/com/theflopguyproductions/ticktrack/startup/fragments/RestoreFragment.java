package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;

public class RestoreFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restore, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private RestoreCompleteListener restoreCompleteListener;
    public interface RestoreCompleteListener {
        void onRestoreCompleteListener();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            restoreCompleteListener = (RestoreCompleteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + RestoreCompleteListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        restoreCompleteListener = null;
    }
}