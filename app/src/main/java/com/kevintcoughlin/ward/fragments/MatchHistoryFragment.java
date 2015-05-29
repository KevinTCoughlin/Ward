package com.kevintcoughlin.ward.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.android.gms.analytics.HitBuilders;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.adapters.MatchSummariesAdapter;
import com.kevintcoughlin.ward.http.RiotGamesClient;
import com.kevintcoughlin.ward.http.RiotGamesService;
import com.kevintcoughlin.ward.listeners.InfiniteRecyclerOnScrollListener;
import com.kevintcoughlin.ward.models.MatchSummary;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class MatchHistoryFragment extends TrackedFragment implements SwipeRefreshLayout.OnRefreshListener,
		Callback<Map<String, List<MatchSummary>>> {
	public static final String TAG = MatchHistoryFragment.class.getSimpleName();
	private final String MATCHES_KEY = "matches"; // @TODO: Move into API service
	private final String ACTION_PAGINATION = "Pagination";
	@InjectView(R.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@InjectView(R.id.list)
	RecyclerView mRecyclerView;
	private MatchSummariesAdapter mAdapter;
	private ArrayList<MatchSummary> mMatchSummaries = new ArrayList<>();
	private Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_match_history, container, false);
		ButterKnife.inject(this, view);

		final Bundle bundle = getArguments();
		if (bundle != null) {
			RiotGamesClient.getClient().listMatchesById(bundle.getString("region"), bundle.getLong("id"), this);
		}

		mContext = this.getActivity().getApplicationContext();
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.purple_400, R.color.purple_500, R.color.purple_600);
		mRecyclerView.setHasFixedSize(true);
		final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new MatchSummariesAdapter(mContext, mMatchSummaries);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.addOnScrollListener(new InfiniteRecyclerOnScrollListener(mLayoutManager) {
			@Override
			public void onLoadMore(final int currentPage) {
				final int index = currentPage * RiotGamesService.MATCH_HISTORY_LIMIT - 1;
				RiotGamesClient.getClient(bundle.getString("region")).listMatchesById(bundle.getString("region"),
						bundle.getLong("id"), index, new Callback<Map<String, List<MatchSummary>>>() {
					@Override
					public void success(Map<String, List<MatchSummary>> matches, Response response) {
						final List<MatchSummary> matchHistory = matches.get(MATCHES_KEY);
						if (matchHistory == null || matchHistory.size() <= 0) {
							Toast.makeText(mContext, getString(R.string.no_more_recent_games), Toast.LENGTH_SHORT).show();
						} else {
							Collections.reverse(matchHistory);
							mMatchSummaries.addAll(matchHistory);
							mAdapter.notifyDataSetChanged();
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Toast.makeText(mContext, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
					}
				});

				mTracker.send(new HitBuilders.EventBuilder()
						.setCategory(TAG)
						.setAction(ACTION_PAGINATION)
						.setLabel("Page")
						.setValue(currentPage)
						.build());
			}
		});

		getActivity().setTitle("Match History");

		return view;
	}

	@Override
	public void success(Map<String, List<MatchSummary>> matches, Response response) {
		final List<MatchSummary> matchHistory = matches.get(MATCHES_KEY);
		if (matchHistory == null || matchHistory.size() <= 0) {
			Toast.makeText(mContext, getString(R.string.no_recent_games), Toast.LENGTH_SHORT).show();
		} else {
			if (!mMatchSummaries.isEmpty()) {
				mMatchSummaries.clear();
				mSwipeRefreshLayout.setRefreshing(false);
			}
			Collections.reverse(matchHistory);
			mMatchSummaries.addAll(matchHistory);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void failure(RetrofitError error) {
		Toast.makeText(mContext, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRefresh() {
		final Bundle bundle = getArguments();
		if (bundle != null) {
			RiotGamesClient.getClient().listMatchesById(bundle.getString("region"), bundle.getLong("id"), this);
		}
	}
}
