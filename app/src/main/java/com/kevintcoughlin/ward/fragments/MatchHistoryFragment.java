package com.kevintcoughlin.ward.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.WardApplication;
import com.kevintcoughlin.ward.adapters.MatchSummariesAdapter;
import com.kevintcoughlin.ward.http.RiotGamesClient;
import com.kevintcoughlin.ward.http.RiotGamesService;
import com.kevintcoughlin.ward.listeners.InfiniteRecyclerOnScrollListener;
import com.kevintcoughlin.ward.models.MatchSummary;
import com.kevintcoughlin.ward.models.Summoner;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class MatchHistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Callback<Map<String, List<MatchSummary>>> {
    @InjectView(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.list) RecyclerView mRecyclerView;
    public static final String TAG = "Match History";
    private final String MATCHES_KEY = "matches"; // @TODO: Move into API service
    private final String ACTION_PAGINATION = "Pagination";
    private MatchSummariesAdapter mAdapter;
    private ArrayList<MatchSummary> mMatchSummaries = new ArrayList<>();
    private Context mContext;
    private Tracker mTracker;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_match_history, container, false);
        ButterKnife.inject(this, view);

        mContext = this.getActivity().getApplicationContext();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MatchSummariesAdapter(mContext, mMatchSummaries);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new InfiniteRecyclerOnScrollListener(mLayoutManager) {
            @Override public void onLoadMore(final int currentPage) {
                final int index = currentPage * RiotGamesService.MATCH_HISTORY_LIMIT - 1;
                final Summoner summoner = Parcels.unwrap(getArguments().getParcelable(Summoner.TAG));

                RiotGamesClient.getClient(summoner.getRegion()).listMatchesById(summoner.getRegion(), summoner.getId(), index, new Callback<Map<String, List<MatchSummary>>>() {
                    @Override public void success(Map<String, List<MatchSummary>> matches, Response response) {
                        final List<MatchSummary> matchHistory =  matches.get(MATCHES_KEY);
                        if (matchHistory == null || matchHistory.size() <= 0) {
                            Toast.makeText(mContext, getString(R.string.no_more_recent_games), Toast.LENGTH_SHORT).show();
                        } else {
                            Collections.reverse(matchHistory);
                            mMatchSummaries.addAll(matchHistory);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override public void failure(RetrofitError error) {
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

        final Summoner summoner = Parcels.unwrap(getArguments().getParcelable(Summoner.TAG));
        getActivity().setTitle(summoner.getName());
        RiotGamesClient.getClient(summoner.getRegion()).listMatchesById(summoner.getRegion(), summoner.getId(), this);

        return view;
    }

    @Override public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTracker = ((WardApplication) getActivity().getApplication()).getTracker();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.AppViewBuilder().build());
    }


    @Override public void success(Map<String, List<MatchSummary>> matches, Response response) {
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

    @Override public void failure(RetrofitError error) {
        Toast.makeText(mContext, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override public void onRefresh() {
        final Summoner summoner = Parcels.unwrap(getArguments().getParcelable(Summoner.TAG));
        RiotGamesClient.getClient(summoner.getRegion()).listMatchesById(summoner.getRegion(), summoner.getId(), this);
    }
}
