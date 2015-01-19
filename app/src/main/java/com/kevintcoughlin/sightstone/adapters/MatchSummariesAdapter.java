package com.kevintcoughlin.sightstone.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevintcoughlin.sightstone.R;
import com.kevintcoughlin.sightstone.models.MatchSummary;
import com.kevintcoughlin.sightstone.models.ParticipantStats;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class MatchSummariesAdapter extends RecyclerView.Adapter<MatchSummariesAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MatchSummary> mMatchSummaries;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.result) TextView mTextView;
        @InjectView(R.id.splash) ImageView mSplashView;
        @InjectView(R.id.kills) TextView mKillsTextView;
        @InjectView(R.id.deaths) TextView mDeathsTextView;
        @InjectView(R.id.assists) TextView mAssistsTextView;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public MatchSummariesAdapter(Context context, ArrayList<MatchSummary> matchSummaries) {
        mContext = context;
        mMatchSummaries = matchSummaries;
    }

    @Override public final MatchSummariesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_history_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override public final void onBindViewHolder(ViewHolder holder, int position) {
        final MatchSummary matchSummary = mMatchSummaries.get(position);
        final ParticipantStats stats = matchSummary.getParticipants().get(0).getStats();
        final boolean won = stats.isWinner();
        final long kills = stats.getKills();
        final long deaths = stats.getDeaths();
        final long assists = stats.getAssists();

        holder.mTextView.setText((won) ? "WIN" : "LOSS");
        holder.mTextView.setTextColor((won) ? mContext.getResources().getColor(R.color.win) : mContext.getResources().getColor(R.color.loss));
        holder.mKillsTextView.setText(kills + " Kills");
        holder.mDeathsTextView.setText(deaths + " Deaths");
        holder.mAssistsTextView.setText(assists + " Assists");

        Picasso.with(mContext)
                .load("http://ddragon.leagueoflegends.com/cdn/img/champion/splash/Jax_0.jpg")
                .fit()
                .centerCrop()
                .into(holder.mSplashView);

    }

    @Override public final int getItemCount() {
        return mMatchSummaries.size();
    }
}
