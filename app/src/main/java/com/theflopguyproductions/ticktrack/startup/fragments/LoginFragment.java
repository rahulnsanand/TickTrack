package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.FirebaseHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.InternetChecker;

public class LoginFragment extends Fragment {

    private TickTrackDatabase tickTrackDatabase;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private FirebaseHelper firebaseHelper;

    private Button signInButton, laterButton;
    private ConstraintLayout rootLayout, retrieveRootLayout;
    private TextView titleText, subTitleText, signInText, signInDetailText;
    private String receivedAction;

    public LoginFragment(String receivedAction) {
        this.receivedAction = receivedAction;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firebaseHelper.isUserSignedIn()){
            Intent intent = new Intent(requireContext(), StartUpActivity.class);
            tickTrackDatabase.storeStartUpFragmentID(4);
            startActivity(intent);
        }
    }

    private void initVariables(View root) {

        signInButton = root.findViewById(R.id.autoStartGoogleFragmentSignInButton);
        laterButton = root.findViewById(R.id.autoStartGoogleFragmentLaterButton);
        rootLayout = root.findViewById(R.id.autoStartGoogleFragmentRootLayout);
        retrieveRootLayout = root.findViewById(R.id.autoStartGoogleFragmentRetrieveRootLayout);
        titleText = root.findViewById(R.id.autoStartGoogleFragmentTitleText);
        subTitleText = root.findViewById(R.id.autoStartGoogleFragmentSubTitleText);
        signInText = root.findViewById(R.id.autoStartGoogleFragmentSignInTitle);
        signInDetailText = root.findViewById(R.id.autoStartGoogleFragmentSignInDetailText);

        retrieveRootLayout.setVisibility(View.GONE);

        tickTrackDatabase = new TickTrackDatabase(requireContext());
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(requireContext());
        firebaseHelper = new FirebaseHelper(requireActivity());
        firebaseHelper.setAction(receivedAction);
        System.out.println("LOGIN ACTIVITY RECEIVED "+receivedAction);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_login, container, false);

        initVariables(root);

        if(InternetChecker.isOnline(requireContext())){
            if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(receivedAction)){
                signInButton.setVisibility(View.GONE);
                laterButton.setVisibility(View.GONE);
                signInClick();
            } else {
                signInButton.setVisibility(View.VISIBLE);
                laterButton.setVisibility(View.VISIBLE);
            }
        } else {
            //TODO DISPLAY NO INTERNET SETUP
            if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(receivedAction)){
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            } else {
                signInButton.setVisibility(View.VISIBLE);
                laterButton.setVisibility(View.VISIBLE);
            }
        }

        signInButton.setOnClickListener(view -> signInClick());
        laterButton.setOnClickListener(view -> loginClickListeners.onLaterClickListener());

        return root;
    }

    private void signInClick(){
        signInButton.setEnabled(false);
        laterButton.setVisibility(View.GONE);
        Intent signInIntent = firebaseHelper.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(InternetChecker.isOnline(requireContext())){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                firebaseHelper.signIn(task, getActivity(), receivedAction);
            } else {
                signInButton.setEnabled(true);
                laterButton.setVisibility(View.VISIBLE);
                System.out.println(receivedAction+":"+requestCode);
                Toast.makeText(getContext(), "Sign in failed, try again", Toast.LENGTH_SHORT).show();
                if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(receivedAction)){
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                }
            }
        } else {
            Toast.makeText(requireContext(), "Kindly Connect To Internet", Toast.LENGTH_SHORT).show();
            firebaseHelper.signOut(getActivity());
            if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(receivedAction)){
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            } else {
                signInButton.setVisibility(View.VISIBLE);
                laterButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private LoginClickListeners loginClickListeners;

    public interface LoginClickListeners {
        void onLaterClickListener();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            loginClickListeners = (LoginClickListeners) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + LoginClickListeners.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        loginClickListeners = null;
    }
}
