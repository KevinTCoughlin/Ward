package com.kevintcoughlin.ward.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.kevintcoughlin.ward.R;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public final class SummonersAdapter extends RecyclerView.Adapter<SummonersAdapter.SummonerViewHolder> {
	private final Context mContext;
	private final List<ParseObject> mItems;

	public SummonersAdapter(Context mContext, List<ParseObject> mItems) {
		this.mContext = mContext;
		this.mItems = mItems;
	}

	@Override
	public SummonerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.summoners_item, parent, false);
		return new SummonerViewHolder(v);
	}

	@Override
	public void onBindViewHolder(SummonerViewHolder holder, int position) {
		final ParseObject object = mItems.get(position);
		holder.mNameView.setText(object.getString("name"));
		holder.mDescriptionView.setText(String.format("Level %d • %s", object.getLong("summonerLevel"), object.getString("region")));
		Picasso.with(mContext)
				.load(String.format("http://ddragon.leagueoflegends.com/cdn/5.2.1/img/profileicon/%d.png", object.getInt("profileIconId")))
				.into(holder.mAvatarView);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	public void add(ParseObject object) {
		mItems.add(object);
		notifyItemInserted(mItems.size());
	}

	public void add(List<ParseObject> objects) {
		mItems.addAll(objects);
		notifyItemRangeInserted(mItems.size(), objects.size());
	}

	public void set(List<ParseObject> objects) {
		mItems.clear();
		mItems.addAll(objects);
		notifyDataSetChanged();

	}

	public ParseObject get(int position) {
		return mItems.get(position);
	}

	public final static class SummonerViewHolder extends RecyclerView.ViewHolder {
		@InjectView(R.id.name) TextView mNameView;
		@InjectView(R.id.avatar) CircleImageView mAvatarView;
		@InjectView(R.id.description) TextView mDescriptionView;

		public SummonerViewHolder(View v) {
			super(v);
			ButterKnife.inject(this, v);
		}
	}

}