package com.kevintcoughlin.sightstone.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevintcoughlin.sightstone.R;
import com.kevintcoughlin.sightstone.models.Summoner;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public final class SummonersAdapter extends CursorRecyclerViewAdapter<SummonersAdapter.ViewHolder> {
    private Context mContext;

    public final static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.name) TextView mTextView;
        @InjectView(R.id.avatar) CircleImageView mAvatarView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public SummonersAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override public SummonersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.summoners_item_view, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        final Summoner summoner = Summoner.fromCursor(cursor);
        holder.mTextView.setText(summoner.getName());
        Picasso.with(mContext)
                .load("http://ddragon.leagueoflegends.com/cdn/4.10.7/img/profileicon/" + summoner.getProfileIconId() + ".png")
                .into(holder.mAvatarView);
    }

}