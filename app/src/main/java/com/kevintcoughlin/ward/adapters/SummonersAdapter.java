package com.kevintcoughlin.ward.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.models.Summoner;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public final class SummonersAdapter extends CursorRecyclerViewAdapter<SummonersAdapter.ViewHolder> {
	private Context mContext;

	public SummonersAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		mContext = context;
	}

	@Override
	public SummonersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.summoners_item, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
		final Summoner summoner = Summoner.fromCursor(cursor);
		holder.mNameView.setText(summoner.getName());
		holder.mDescriptionView.setText(String.format("Level %d • %s", summoner.getSummonerLevel(), summoner.getRegion().toUpperCase()));
		Picasso.with(mContext)
				.load("http://ddragon.leagueoflegends.com/cdn/4.10.7/img/profileicon/" + summoner.getProfileIconId() + ".png")
				.into(holder.mAvatarView);
	}

	public final static class ViewHolder extends RecyclerView.ViewHolder {
		@InjectView(R.id.name)
		TextView mNameView;
		@InjectView(R.id.avatar)
		CircleImageView mAvatarView;
		@InjectView(R.id.description)
		TextView mDescriptionView;

		public ViewHolder(View v) {
			super(v);
			ButterKnife.inject(this, v);
		}
	}

}