package com.kevintcoughlin.ward.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.kevintcoughlin.ward.BuildConfig;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.models.MatchSummary;
import com.kevintcoughlin.ward.models.ParticipantStats;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

public final class MatchSummariesAdapter extends RecyclerView.Adapter<MatchSummariesAdapter.ViewHolder> {
	private final String VERSION = "5.1.1";
	private Context mContext;
	private ArrayList<MatchSummary> mMatchSummaries;
	public MatchSummariesAdapter(final Context context, final ArrayList<MatchSummary> matchSummaries) {
		mContext = context;
		mMatchSummaries = matchSummaries;
	}

	@Override
	public MatchSummariesAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_history_item, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		final MatchSummary matchSummary = mMatchSummaries.get(position);
		final ParticipantStats stats = matchSummary.getParticipants().get(0).getStats();
		final int resultColor = (stats.isWinner()) ? mContext.getResources().getColor(R.color.win) : mContext.getResources().getColor(R.color.loss);
		final String statsString = String.format("%d/%d/%d", stats.getKills(), stats.getDeaths(), stats.getAssists());
		final CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(matchSummary.getMatchCreation(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS);
		final String durationString = String.valueOf(matchSummary.getMatchDuration() / 60) + " mins";

//		holder.mChampionNameView.setText(o.getString("name"));
		holder.mTimeView.setText(relativeTime);
		holder.mStatsTextView.setText(statsString);
		holder.mDurationTextView.setText(durationString);
		holder.mChampionArtworkImageView.setBorderColor(resultColor);
		holder.mChampionArtworkImageView.setBorderWidth(8);

		if (BuildConfig.DEBUG) {
			Picasso.with(mContext).setIndicatorsEnabled(true);
			Picasso.with(mContext).setLoggingEnabled(true);
		}

//		Picasso.with(mContext)
//				.load("http://ddragon.leagueoflegends.com/cdn/" + VERSION + "/img/champion/" + o.getString("key") + "" +
//						".png")
//				.fit()
//				.centerCrop()
//				.into(holder.mChampionArtworkImageView);
	}

	@Override
	public int getItemCount() {
		return mMatchSummaries.size();
	}

	public final static class ViewHolder extends RecyclerView.ViewHolder {
		@InjectView(R.id.champion_name) TextView mChampionNameView;
		@InjectView(R.id.champion_artwork) CircleImageView mChampionArtworkImageView;
		@InjectView(R.id.stats) TextView mStatsTextView;
		@InjectView(R.id.time) TextView mTimeView;
		@InjectView(R.id.duration) TextView mDurationTextView;

		public ViewHolder(View v) {
			super(v);
			ButterKnife.inject(this, v);
		}
	}
}
