package com.kevintcoughlin.ward.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.WardApplication;
import com.kevintcoughlin.ward.adapters.SummonersAdapter;
import com.kevintcoughlin.ward.http.RiotGamesClient;
import com.kevintcoughlin.ward.models.Summoner;
import com.melnykov.fab.FloatingActionButton;
import com.parse.*;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class FavoriteSummonersFragment extends Fragment implements RecyclerView.OnItemTouchListener, FloatingActionButton.OnClickListener {
	public static final String TAG = "Summoners";
	private final String ACTION_ADD = "Add";
	private final String ACTION_REMOVE = "Remove";
	private final String ACTION_SEARCH = "Search";
	private final String ACTION_MATCH_HISTORY = "Match History";
	@InjectView(R.id.list) RecyclerView mRecyclerView;
	@InjectView(R.id.fab) FloatingActionButton mFab;
	private OnSummonerSelectedListener mListener;
	private SummonersAdapter mAdapter;
	private GestureDetectorCompat mDetector;
	private Context mContext;
	private Tracker mTracker;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSummonerSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnSummonerSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = inflater.inflate(R.layout.fragment_favorite_summoners, container, false);
		ButterKnife.inject(this, view);

		getActivity().setTitle(TAG);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

		final ParseQuery<ParseObject> query = ParseQuery.getQuery("Summoner");
		query.whereEqualTo("followedBy", ParseUser.getCurrentUser());
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> summoners, ParseException e) {
				if (e == null) {
					mAdapter = new SummonersAdapter(getActivity(), summoners);
					mAdapter.setHasStableIds(true);
					mRecyclerView.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
				} else {
					Timber.d("score", "Error: " + e.getMessage());
				}
			}
		});

		mFab.setOnClickListener(this);
		mDetector = new GestureDetectorCompat(mContext, new RecyclerViewOnGestureListener());
		mRecyclerView.addOnItemTouchListener(this);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getActivity();
		mTracker = ((WardApplication) getActivity().getApplication()).getTracker();
		mTracker.setScreenName(TAG);
		mTracker.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public void onClick(View v) {
		promptAddSummoner();
	}

	private void promptAddSummoner() {
		final LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.dialog_add_summoner, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		final String[] regionValues = getResources().getStringArray(R.array.region_values);
		final int position = Arrays.asList(regionValues).indexOf(prefs.getString("region", "na"));
		final Spinner spinner = (Spinner) v.findViewById(R.id.regions);
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.region_keys, android.R.layout.simple_spinner_dropdown_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(position);
		builder.setTitle(getString(R.string.follow));
		builder.setView(v);
		builder.setPositiveButton(getString(R.string.follow), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final EditText input = (EditText) v.findViewById(R.id.summoner_name);
				final String name = input.getText().toString();
				final int spinnerPosition = spinner.getSelectedItemPosition();
				final String region = regionValues[spinnerPosition];
				addSummoner(name, region);

				mTracker.send(new HitBuilders.EventBuilder()
						.setCategory(TAG)
						.setAction(ACTION_SEARCH)
						.setLabel(name)
						.build());
			}
		});
		builder.setNegativeButton(getString(R.string.nevermind), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
	}

	private void promptDeleteSummoner(final Summoner summoner) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(getString(R.string.unfollow));
		builder.setMessage(String.format(getString(R.string.confirm_unfollow_summoner), summoner.getName()));
		builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				removeSummoner(summoner.getId());

				mTracker.send(new HitBuilders.EventBuilder()
						.setCategory(TAG)
						.setAction(ACTION_REMOVE)
						.setLabel(summoner.getName())
						.build());
			}
		});
		builder.setNegativeButton(getString(R.string.nevermind), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
	}

	private void addSummoner(String name, final String region) {
		RiotGamesClient.getClient(region).listSummonersByNames(region, name, new Callback<Map<String, Summoner>>() {
			@Override
			public void success(Map<String, Summoner> stringSummonerMap, Response response) {
				for (final Map.Entry<String, Summoner> pair : stringSummonerMap.entrySet()) {
					final Summoner summoner = pair.getValue();
					final ParseObject s = new ParseObject("Summoner");
					s.put("id", summoner.getId());
					s.put("name", summoner.getName());
					s.put("profileIconId", summoner.getProfileIconId());
					s.put("revisionDate", summoner.getRevisionDate());
					s.put("summonerLevel", summoner.getSummonerLevel());
					s.put("region", region);
					s.addUnique("followedBy", ParseUser.getCurrentUser());
					s.saveEventually();
					mAdapter.add(s);
					mTracker.send(new HitBuilders.EventBuilder()
							.setCategory(TAG)
							.setAction(ACTION_ADD)
							.setLabel(s.getString("name"))
							.build());
				}
			}

			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(mContext, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void removeSummoner(long id) {
		// @TODO: implement
	}

	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
		return mDetector.onTouchEvent(e);
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {
	}

	public interface OnSummonerSelectedListener {
		void onSummonerSelectedListener(ParseObject object);
	}

	private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			final View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			final int position = mRecyclerView.getChildAdapterPosition(view);
			if (position > -1) {
				final ParseObject summoner = mAdapter.get(position);
				mListener.onSummonerSelectedListener(summoner);
				mTracker.send(new HitBuilders.EventBuilder()
						.setCategory(TAG)
						.setAction(ACTION_MATCH_HISTORY)
						.setLabel(summoner.getString("name"))
						.build());
			}
			return super.onSingleTapConfirmed(e);
		}

		public void onLongPress(MotionEvent e) {
			final View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			final int position = mRecyclerView.getChildAdapterPosition(view);
			// @TODO: re-implement
			//promptDeleteSummoner(summoner);
			super.onLongPress(e);
		}
	}
}
