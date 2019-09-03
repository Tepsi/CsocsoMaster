package com.example.huzz00mc.csocsomaster;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;

import java.util.Map;

public class SettingsFragment extends android.preference.PreferenceFragment {
    SharedPreferences sharedPreferences;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
