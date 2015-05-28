package com.kevintcoughlin.ward.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.kevintcoughlin.ward.R;

public final class SettingsFragment extends PreferenceFragment {
	public static final String TAG = SettingsFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getActivity().setTitle("Settings");
	}
}