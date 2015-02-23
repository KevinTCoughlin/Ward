package com.kevintcoughlin.ward.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevintcoughlin.ward.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class DrawerNavigationAdapter extends ArrayAdapter<String> {
    private String[] mDataSet;
    private Context mContext;

    public DrawerNavigationAdapter(Context context, String[] objects) {
        super(context, R.layout.drawer_navigation_list_item, objects);
        mDataSet = objects;
        mContext = context;
    }

    @Override public int getCount() {
        return mDataSet.length;
    }

    @Override public String getItem(int position) {
        return mDataSet[position];
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_navigation_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int iconId = 0;
        switch(position) {
            case 0:
                iconId = R.drawable.ic_whatshot_white_24dp;
                break;
            case 1:
                iconId = R.drawable.ic_people_white_24dp;
                break;
            case 2:
                iconId = R.drawable.ic_settings_white_24dp;
                break;
        }
        viewHolder.mTextView.setText(mDataSet[position]);
        viewHolder.mIconView.setImageDrawable(mContext.getResources().getDrawable(iconId));
        viewHolder.mIconView.setColorFilter(mContext.getResources().getColor(R.color.purple_500));
        return convertView;
    }

    @Override public boolean hasStableIds() {
        return true;
    }

    @Override public boolean isEmpty() {
        return (mDataSet.length == 0);
    }

    final static class ViewHolder {
        @InjectView(R.id.title) TextView mTextView;
        @InjectView(R.id.icon) ImageView mIconView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
