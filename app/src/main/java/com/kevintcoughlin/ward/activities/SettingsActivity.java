package com.kevintcoughlin.ward.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.kevintcoughlin.ward.R;

public final  class SettingsActivity extends PreferenceActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}