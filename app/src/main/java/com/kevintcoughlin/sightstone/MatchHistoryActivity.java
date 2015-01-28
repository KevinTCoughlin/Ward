package com.kevintcoughlin.sightstone;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.kevintcoughlin.sightstone.adapters.MatchSummariesAdapter;
import com.kevintcoughlin.sightstone.http.RiotGamesClient;
import com.kevintcoughlin.sightstone.models.Champion;
import com.kevintcoughlin.sightstone.models.MatchSummary;
import com.kevintcoughlin.sightstone.models.Summoner;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    private LinearLayoutManager mLayoutManager;
    private MatchSummariesAdapter mAdapter;
    private ArrayList<MatchSummary> mMatchSummaries = new ArrayList<>();
    private HashMap<String, Champion> mChampions;
    private final String region = "na"; // @TODO: make configurable

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MatchSummariesAdapter(this, mMatchSummaries);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new InfiniteRecyclerOnScrollListener(mLayoutManager) {
            @Override public void onLoadMore(final int currentPage) {

            }
        });

        final Summoner summoner = Parcels.unwrap(getIntent().getParcelableExtra("summoner"));
        RiotGamesClient.getClient().listMatchesById(region, summoner.getId(), this);
    }

    @Override public void success(Map<String, List<MatchSummary>> matches, Response response) {
        ArrayList<MatchSummary> matchHistory = (ArrayList<MatchSummary>) matches.get("matches");
        if (matchHistory == null || matchHistory.size() <= 0) {
            Toast.makeText(this, "No recent games found.", Toast.LENGTH_SHORT).show();
        } else {
            // @TODO: Sort by created timestamp?
            Collections.reverse(matchHistory);
            mMatchSummaries.addAll(matchHistory);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override public void failure(RetrofitError error) {
        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
