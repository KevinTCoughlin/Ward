package com.kevintcoughlin.ward.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.models.news.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public final class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
	private ArrayList<Item> mDataSet;
	private Context mContext;

	public NewsAdapter(Context context, ArrayList<Item> items) {
		mContext = context;
		mDataSet = items;
	}

	@Override
	public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final Item item = mDataSet.get(position);
		holder.mTitleTextView.setText(item.getTitle());
		holder.mDescriptionTextView.setText(item.getDescription());
		// @TODO: large -> medium (small if available in image url)
		Picasso.with(mContext)
				.load("http://na.leagueoflegends.com/en" + item.imageUrl)
				.fit()
				.centerCrop()
				.into(holder.mImageView);
	}

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@InjectView(R.id.title)
		TextView mTitleTextView;
		@InjectView(R.id.description)
		TextView mDescriptionTextView;
		@InjectView(R.id.image)
		ImageView mImageView;

		public ViewHolder(View v) {
			super(v);
			ButterKnife.inject(this, v);
		}
	}
}