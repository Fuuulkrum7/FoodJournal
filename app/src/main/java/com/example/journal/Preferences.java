package com.example.journal;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class Preferences extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
