package com.kevintcoughlin.sightstone;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.sightstone.adapters.MatchSummariesAdapter;
import com.kevintcoughlin.sightstone.http.RiotGamesClient;
import com.kevintcoughlin.sightstone.http.RiotGamesService;
import com.kevintcoughlin.sightstone.models.MatchSummary;
import com.kevintcoughlin.sightstone.models.Summoner;

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

public final class MatchHistoryActivity extends ActionBarActivity implements Callback<Map<String, List<MatchSummary>>> {
    @InjectView(R.id.toolbar_actionbar) Toolbar mToolbar;
    @InjectView(R.id.list) RecyclerView mRecyclerView;

    private final String TAG = "Match History";
    private final String MATCHES_KEY = "matches"; // @TODO: Move into API service
    private final String ACTION_PAGINATION = "Pagination";
    private MatchSummariesAdapter mAdapter;
    private ArrayList<MatchSummary> mMatchSummaries = new ArrayList<>();
    private Context mContext;
    private Tracker mTracker;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);

        mContext = this.getApplicationContext();
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MatchSummariesAdapter(this, mMatchSummaries);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new InfiniteRecyclerOnScrollListener(mLayoutManager) {
            @Override public void onLoadMore(final int currentPage) {
                final int index = currentPage * RiotGamesService.MATCH_HISTORY_LIMIT - 1;
                final Summoner summoner = Parcels.unwrap(getIntent().getParcelableExtra(Summoner.TAG));

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

        final Summoner summoner = Parcels.unwrap(getIntent().getParcelableExtra(Summoner.TAG));
        RiotGamesClient.getClient(summoner.getRegion()).listMatchesById(summoner.getRegion(), summoner.getId(), this);

        mTracker = ((WardApplication) getApplication()).getTracker();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override public void success(Map<String, List<MatchSummary>> matches, Response response) {
        final List<MatchSummary> matchHistory = matches.get(MATCHES_KEY);
        if (matchHistory == null || matchHistory.size() <= 0) {
            Toast.makeText(this, getString(R.string.no_recent_games), Toast.LENGTH_SHORT).show();
        } else {
            Collections.reverse(matchHistory);
            mMatchSummaries.addAll(matchHistory);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override public void failure(RetrofitError error) {
        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
