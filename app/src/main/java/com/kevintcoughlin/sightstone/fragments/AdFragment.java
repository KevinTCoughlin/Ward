package com.kevintcoughlin.sightstone.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kevintcoughlin.sightstone.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AdFragment extends Fragment {
    @InjectView(R.id.adView) AdView mAdView;

    public AdFragment() {
    }

    @Override public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("C6D397172C2598AF256CF30C6393FBFC")
                .build();

        mAdView.loadAd(adRequest);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_ad, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}
