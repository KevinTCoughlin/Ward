package com.kevintcoughlin.ward.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.kevintcoughlin.ward.R;

public final class DrawerNavigationAdapter extends ArrayAdapter<String> {
	private final String[] mDataSet;
	private final Context mContext;

	public DrawerNavigationAdapter(Context context, String[] objects) {
		super(context, R.layout.drawer_navigation_list_item, objects);
		mDataSet = objects;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mDataSet.length;
	}

	@Override
	public String getItem(int position) {
		return mDataSet[position];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.drawer_navigation_list_item, parent, false);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mTextView.setText(mDataSet[position]);
		return convertView;
	}

	@Override
	public boolean isEmpty() {
		return (mDataSet.length == 0);
	}

	public static final class ViewHolder {
		@InjectView(R.id.title) TextView mTextView;
		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
