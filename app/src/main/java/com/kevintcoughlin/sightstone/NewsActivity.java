package com.kevintcoughlin.sightstone;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.kevintcoughlin.sightstone.adapters.NewsAdapter;
import com.kevintcoughlin.sightstone.http.LeagueOfLegendsNewsClient;
import com.kevintcoughlin.sightstone.models.news.Item;
import com.kevintcoughlin.sightstone.models.news.Rss;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewsActivity extends ActionBarActivity implements Callback<Rss> {
    @InjectView(R.id.toolbar_actionbar) Toolbar mToolbar;
    @InjectView(R.id.list) RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Item> mNewsDataSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);

        setTitle("News");
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NewsAdapter(mNewsDataSet);
        mRecyclerView.setAdapter(mAdapter);

        LeagueOfLegendsNewsClient.getClient().getFeed(this);
    }

    @Override public void success(Rss rss, Response response) {
        mNewsDataSet.addAll(rss.getChannel().getItems());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
