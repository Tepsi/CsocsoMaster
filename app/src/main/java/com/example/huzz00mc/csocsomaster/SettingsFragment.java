package com.example.huzz00mc.csocsomaster;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;

import java.util.Map;

public class SettingsFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
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

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = getPreferenceManager().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Map<String, ?> preferencesMap = sharedPreferences.getAll();

        // get the preference that has been changed
        Object changedPreference = preferencesMap.get(key);
        // and if it's an instance of EditTextPreference class, update its summary

        if (preferencesMap.get(key) instanceof EditTextPreference) {
            updateSummary((EditTextPreference) changedPreference);
        }

        if (key == "pref_keep_score") {
            ((MainActivity) getActivity()).matchFragment.setScoreVisibility();
        }
    }

    private void updateSummary(EditTextPreference changedPreference) {
        changedPreference.setSummary(changedPreference.getText());
    }


}
