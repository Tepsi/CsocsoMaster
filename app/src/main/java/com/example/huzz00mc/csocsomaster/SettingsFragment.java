package com.example.huzz00mc.csocsomaster;

import android.os.Bundle;

public class SettingsFragment extends android.preference.PreferenceFragment {

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
