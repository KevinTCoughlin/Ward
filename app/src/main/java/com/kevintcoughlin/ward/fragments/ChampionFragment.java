package com.kevintcoughlin.ward.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.kevintcoughlin.ward.R;

public final class ChampionFragment extends Fragment {
	public static final String TAG = "Champion";

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = inflater.inflate(R.layout.fragment_champion, container, false);
		ButterKnife.inject(this, view);

		getActivity().setTitle(TAG);

		return view;
	}

}
