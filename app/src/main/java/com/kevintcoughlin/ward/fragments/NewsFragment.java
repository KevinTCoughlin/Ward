package com.kevintcoughlin.ward.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.adapters.NewsAdapter;
import com.kevintcoughlin.ward.http.LeagueOfLegendsNewsClient;
import com.kevintcoughlin.ward.models.news.Item;
import com.kevintcoughlin.ward.models.news.Rss;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class NewsFragment extends Fragment implements RecyclerView.OnItemTouchListener, Callback<Rss> {
    @InjectView(R.id.list) RecyclerView mRecyclerView;
    public static final String TAG = "News";
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Item> mNewsDataSet = new ArrayList<>();
    private GestureDetectorCompat mDetector;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.inject(this, view);

        getActivity().setTitle(TAG);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NewsAdapter(getActivity(), mNewsDataSet);
        mRecyclerView.setAdapter(mAdapter);
        mDetector = new GestureDetectorCompat(getActivity(), new RecyclerViewOnGestureListener());
        mRecyclerView.addOnItemTouchListener(this);

        LeagueOfLegendsNewsClient.getClient().getFeed(this);

        return view;
    }

    @Override public void success(Rss rss, Response response) {
        final List<Item> items = rss.getChannel().getItems();
        for (Item item : items) {
            // @TODO: Move this parsing
            final Pattern p = Pattern.compile("src=\"(.*?)\"");
            final Matcher m = p.matcher(item.getDescription());
            if (m.find()) {
                item.imageUrl = m.group(1);
            }
            item.setDescription(stripHtml(item.getDescription()));
        }

        mNewsDataSet.addAll(rss.getChannel().getItems());
        mAdapter.notifyDataSetChanged();
    }

    // Strip HTML & new line chars from description
    // @TODO: Move this into a transform or the model
    private String stripHtml(final String s) {
        return Html.fromHtml(s).toString()
                .replace('\n', (char) 32)
                .replace((char) 160, (char) 32)
                .replace((char) 65532, (char) 32)
                .trim();
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mDetector.onTouchEvent(e);
        return false;
    }

    @Override public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

    private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override public boolean onSingleTapConfirmed(MotionEvent e) {
            final View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            final int position = mRecyclerView.getChildPosition(view);
            if (position > -1) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mNewsDataSet.get(position).getLink())));
            }
            return super.onSingleTapConfirmed(e);
        }
    }
}

