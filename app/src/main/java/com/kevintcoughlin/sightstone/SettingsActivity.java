package com.kevintcoughlin.sightstone;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public final  class SettingsActivity extends PreferenceActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}