package com.theflopguyproductions.ticktrack.utils.firebase;

import android.content.Context;
import android.net.ConnectivityManager;

public class InternetChecker {

    public static boolean isOnline(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try{
            return cm.getActiveNetworkInfo() != null &&
                    cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception error){
            error.printStackTrace();
            return false;
        }
    }
}