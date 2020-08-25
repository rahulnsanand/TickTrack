package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class GoogleSignInFragment extends Fragment {

    private TickTrackDatabase tickTrackDatabase;
    private Button signInButton, laterButton;

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if(account != null && !account.isExpired()){
            Intent intent = new Intent(requireContext(), StartUpActivity.class);
            tickTrackDatabase.storeStartUpFragmentID(3);
            startActivity(intent);
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_google, container, false);
        tickTrackDatabase = new TickTrackDatabase(requireContext());
        signInButton = root.findViewById(R.id.ticktrackGoogleFragmentSignInButton);
        laterButton = root.findViewById(R.id.ticktrackGoogleFragmentLaterButton);

        signInButton.setOnClickListener(view -> signInClickListener.onSignInClickListener());

        laterButton.setOnClickListener(view -> laterClickListener.onLaterClickListener());

        return root;
    }



    private SignInClickListener signInClickListener;
    private LaterClickListener laterClickListener;
    public interface SignInClickListener {
        void onSignInClickListener();
    }
    public interface LaterClickListener {
        void onLaterClickListener();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            signInClickListener = (SignInClickListener) context;
            laterClickListener = (LaterClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + SignInClickListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        signInClickListener = null;
        laterClickListener = null;
    }
}
