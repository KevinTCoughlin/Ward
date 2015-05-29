package com.kevintcoughlin.ward.fragments;

import android.app.Fragment;
import android.os.Bundle;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.ward.WardApplication;

/**
 * Fragment that tracks screen views to Google Analytics.
 * Created by kevincoughlin on 5/28/15.
 */
public class TrackedFragment extends Fragment {
	protected static final String TAG = TrackedFragment.class.getSimpleName();
	protected Tracker mTracker;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mTracker = ((WardApplication) getActivity().getApplication()).getTracker();
		mTracker.setScreenName(TAG);
		mTracker.send(new HitBuilders.AppViewBuilder().build());
	}
}
