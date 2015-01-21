package com.kevintcoughlin.sightstone.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevintcoughlin.sightstone.BuildConfig;
import com.kevintcoughlin.sightstone.R;
import com.kevintcoughlin.sightstone.database.CupboardSQLiteOpenHelper;
import com.kevintcoughlin.sightstone.models.Champion;
import com.kevintcoughlin.sightstone.models.MatchSummary;
import com.kevintcoughlin.sightstone.models.ParticipantStats;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import nl.qbusict.cupboard.DatabaseCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MatchSummariesAdapter extends RecyclerView.Adapter<MatchSummariesAdapter.ViewHolder> {
    private final String VERSION = "5.1.1";
    private Context mContext;
    private ArrayList<MatchSummary> mMatchSummaries;
    private CupboardSQLiteOpenHelper db;
    private DatabaseCompartment dbc;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.champion_name) TextView mTextView;
        @InjectView(R.id.champion_artwork) CircleImageView mChampionArtworkImageView;
        //@InjectView(R.id.kills) TextView mKillsTextView;
        //@InjectView(R.id.deaths) TextView mDeathsTextView;
        //@InjectView(R.id.assists) TextView mAssistsTextView;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public MatchSummariesAdapter(Context context, ArrayList<MatchSummary> matchSummaries) {
        mContext = context;
        mMatchSummaries = matchSummaries;
        db = new CupboardSQLiteOpenHelper(mContext);
        dbc = cupboard().withDatabase(db.getWritableDatabase());
    }

    @Override public MatchSummariesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_history_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        final MatchSummary matchSummary = mMatchSummaries.get(position);
        final Champion champion = dbc.get(Champion.class, matchSummary.getParticipants().get(0).getChampionId());
        final ParticipantStats stats = matchSummary.getParticipants().get(0).getStats();
        final boolean won = stats.isWinner();
        final long kills = stats.getKills();
        final long deaths = stats.getDeaths();
        final long assists = stats.getAssists();
        final int resultColor = (won) ? mContext.getResources().getColor(R.color.win) : mContext.getResources().getColor(R.color.loss);
        //holder.mTextView.setText();
        long createdAt = matchSummary.getMatchCreation();
        holder.mTextView.setText(DateUtils.getRelativeDateTimeString(mContext, createdAt, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));
        holder.mTextView.setTextColor(resultColor);
        holder.mChampionArtworkImageView.setBorderColor(resultColor);
        holder.mChampionArtworkImageView.setBorderWidth(8);
        //holder.mKillsTextView.setText(kills + " Kills");
        //holder.mDeathsTextView.setText(deaths + " Deaths");
        //holder.mAssistsTextView.setText(assists + " Assists");

        if (BuildConfig.DEBUG) {
            Picasso.with(mContext).setIndicatorsEnabled(true);
            Picasso.with(mContext).setLoggingEnabled(true);
        }

        Picasso.with(mContext)
                .load("http://ddragon.leagueoflegends.com/cdn/" + VERSION + "/img/champion/" + champion.getKey() + ".png")
                .fit()
                .centerCrop()
                .into(holder.mChampionArtworkImageView);

    }

    @Override public int getItemCount() {
        return mMatchSummaries.size();
    }
}
