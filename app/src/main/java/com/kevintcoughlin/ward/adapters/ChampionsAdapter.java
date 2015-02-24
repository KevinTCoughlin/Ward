package com.kevintcoughlin.ward.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kevintcoughlin.ward.BuildConfig;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.models.DataDragonChampion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class ChampionsAdapter extends RecyclerView.Adapter<ChampionsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<DataDragonChampion> mChampions;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.champion_artwork) ImageView mChampionArtwork;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public ChampionsAdapter(final Context context, final ArrayList<DataDragonChampion> champions) {
        mContext = context;
        mChampions = champions;
    }

    @Override public ChampionsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.champions_item, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (BuildConfig.DEBUG) {
            Picasso.with(mContext).setIndicatorsEnabled(true);
            Picasso.with(mContext).setLoggingEnabled(true);
        }
        final DataDragonChampion champion = mChampions.get(position);

        Picasso.with(mContext)
                .load("http://ddragon.leagueoflegends.com/cdn/5.2.1/img/champion/" + full + ".png")
                .fit()
                .centerCrop()
                .into(holder.mChampionArtwork);
    }

    @Override public int getItemCount() {
        return mChampions.size();
    }
}