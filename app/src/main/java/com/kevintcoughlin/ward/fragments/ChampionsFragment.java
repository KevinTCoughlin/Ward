package com.kevintcoughlin.ward.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.adapters.ChampionsAdapter;
import com.kevintcoughlin.ward.http.DataDragonClient;
import com.kevintcoughlin.ward.http.RiotGamesClient;
import com.kevintcoughlin.ward.models.ChampionMetaData;
import com.kevintcoughlin.ward.models.DataDragonChampion;
import com.kevintcoughlin.ward.models.DataDragonChampionsData;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ChampionsFragment extends TrackedFragment implements Callback<DataDragonChampionsData>, RecyclerView.OnItemTouchListener {
	public static final String TAG = "Champions";
	private final ArrayList<DataDragonChampion> mChampions = new ArrayList<>();
	private final HashMap<Integer, DataDragonChampion> mChampionsData = new HashMap<>();
	@InjectView(R.id.list)
	RecyclerView mRecyclerView;
	private ChampionsAdapter mAdapter;
	private GestureDetectorCompat mDetector;
	private OnChampionSelectedListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnChampionSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnChampionSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = inflater.inflate(R.layout.fragment_champions, container, false);
		ButterKnife.inject(this, view);

		getActivity().setTitle(TAG);
		final int mNumColumns = 3;
		final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), mNumColumns);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new ChampionsAdapter(getActivity(), mChampions);
		mRecyclerView.setAdapter(mAdapter);
		mDetector = new GestureDetectorCompat(getActivity(), new RecyclerViewOnGestureListener());
		mRecyclerView.addOnItemTouchListener(this);

		DataDragonClient.getClient().getChampions(this);

		return view;
	}

	private void getFreeToPlay() {
		RiotGamesClient.getClient().listChampions("na", true, new Callback<Map<String, ChampionMetaData[]>>() {
			@Override
			public void success(Map<String, ChampionMetaData[]> stringChampionMetaDataMap, Response response) {
				for (final ChampionMetaData meta : stringChampionMetaDataMap.get("champions")) {
					final DataDragonChampion champion = mChampionsData.get(meta.id);
					champion.setFreeToPlay(meta.freeToPlay);
				}
				mAdapter.sort();
			}

			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void success(DataDragonChampionsData dataDragonChampionsData, Response response) {
		for (final Map.Entry<String, DataDragonChampion> champion : dataDragonChampionsData.data.entrySet()) {
			mChampionsData.put(Integer.valueOf(champion.getValue().getKey()), champion.getValue());
		}
		mChampions.addAll(mChampionsData.values());
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void failure(RetrofitError error) {
		Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
		return mDetector.onTouchEvent(e);
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {

	}

	public interface OnChampionSelectedListener {
		public void onChampionSelectedListener(DataDragonChampion champion);
	}

	private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			final View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			final int position = mRecyclerView.getChildPosition(view);
			if (position > -1) {
				mListener.onChampionSelectedListener(mChampions.get(position));
			}
			return super.onSingleTapConfirmed(e);
		}
	}
}
