package com.example.group2_bigproject;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    // Sharedpref file name
    private static final String PREF_NAME = "session";
    //Initialising the SharedPreferences
    SharedPreferences pref;
    private Context context;
    public SharedPreferencesHelper(Context context) {
        this.context = context;
        pref = (this.context)
                .getSharedPreferences(PREF_NAME, 0);
    }

    public void startSession(String userID) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userID", userID);
        editor.apply();
    }

    public String getSessionID() {
        return pref.getString("userID", null);
    }

    public void endSession() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
