package com.kevintcoughlin.ward.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.*;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.android.gms.analytics.HitBuilders;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.adapters.NewsAdapter;
import com.kevintcoughlin.ward.http.LeagueOfLegendsNewsClient;
import com.kevintcoughlin.ward.models.news.Item;
import com.kevintcoughlin.ward.models.news.Rss;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NewsFragment extends TrackedFragment implements RecyclerView.OnItemTouchListener, Callback<Rss>, SwipeRefreshLayout.OnRefreshListener {
	public static final String TAG = "News";
	private final String region = "na";
	private final String language = "en";
	@InjectView(R.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@InjectView(R.id.list)
	RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private ArrayList<Item> mNewsDataSet = new ArrayList<>();
	private GestureDetectorCompat mDetector;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = inflater.inflate(R.layout.fragment_news, container, false);
		ButterKnife.inject(this, view);

		getActivity().setTitle(TAG);
		final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.purple_400, R.color.purple_500, R.color.purple_600);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new NewsAdapter(getActivity(), mNewsDataSet);
		mRecyclerView.setAdapter(mAdapter);
		mDetector = new GestureDetectorCompat(getActivity(), new RecyclerViewOnGestureListener());
		mRecyclerView.addOnItemTouchListener(this);
		LeagueOfLegendsNewsClient.getClient(region, language).getFeed(this);

		return view;
	}

	@Override
	public void success(Rss rss, Response response) {
		for (final Item item : rss.getChannel().getItems()) {
			// @TODO: Move this parsing
			final Pattern p = Pattern.compile("src=\"(.*?)\"");
			final Matcher m = p.matcher(item.getDescription());
			if (m.find()) {
				item.imageUrl = m.group(1);
			}
			item.setDescription(stripHtml(item.getDescription()));
		}
		if (!mNewsDataSet.isEmpty()) {
			mNewsDataSet.clear();
			mSwipeRefreshLayout.setRefreshing(false);
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

	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
		mDetector.onTouchEvent(e);
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {
	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

	}

	@Override
	public void onRefresh() {
		LeagueOfLegendsNewsClient.getClient(region, language).getFeed(this);
	}

	private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			final View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			final int position = mRecyclerView.getChildPosition(view);
			if (position > -1) {
				final String link = mNewsDataSet.get(position).getLink();
				final String ACTION_VIEW_LINK = "View";
				mTracker.send(new HitBuilders.EventBuilder()
						.setCategory(TAG)
						.setAction(ACTION_VIEW_LINK)
						.setLabel(link)
						.build());

				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
			}
			return super.onSingleTapConfirmed(e);
		}
	}
}

