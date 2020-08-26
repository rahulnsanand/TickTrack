package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.theflopguyproductions.ticktrack.R;

public class BackupRestoreFragment extends Fragment {


    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if(account == null || account.isExpired()){
            //TODO MOVE TO SIGN IN PAGE
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_backup_restore, container, false);

        return root;
    }

    private SetupBackupAccountClickListener setupBackupAccountClickListener;
    public interface SetupBackupAccountClickListener {
        void onBackupSetClickListener();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            setupBackupAccountClickListener = (SetupBackupAccountClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + SetupBackupAccountClickListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        setupBackupAccountClickListener = null;
    }

}
