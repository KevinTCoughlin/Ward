package com.kevintcoughlin.sightstone.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pavlospt.CircleView;
import com.kevintcoughlin.sightstone.R;
import com.kevintcoughlin.sightstone.models.MatchSummary;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class MatchSummariesAdapter extends RecyclerView.Adapter<MatchSummariesAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MatchSummary> mMatchSummaries;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.result) TextView mTextView;
        //@InjectView(R.id.result) CircleView mResultView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public MatchSummariesAdapter(Context context, ArrayList<MatchSummary> matchSummaries) {
        mContext = context;
        mMatchSummaries = matchSummaries;
    }

    @Override public MatchSummariesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_history_item_view, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        final MatchSummary matchSummary = mMatchSummaries.get(position);
        final boolean won = matchSummary.getParticipants().get(0).getStats().isWinner();
        //final int color = (won) ? mContext.getResources().getColor(R.color.green) : mContext.getResources().getColor(R.color.red);
        holder.mTextView.setText((won) ? "won" : "lost");
        //holder.mResultView.setBackgroundColor(color);
        //holder.mResultView.setFillColor(color);
        //holder.mResultView.setTitleText((won) ? "W" : "L");
        //holder.mResultView.setShowSubtitle(false);
        /*
        Picasso.with(mContext)
                .load("http://ddragon.leagueoflegends.com/cdn/4.10.7/img/champion/" + matchSummary.getParticipants().get(0).getChampionId() + ".png")
                .fit()
                .into(holder.mAvatarView);
        */
    }

    @Override public int getItemCount() {
        return mMatchSummaries.size();
    }
}
