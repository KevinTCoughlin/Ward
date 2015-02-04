package com.kevintcoughlin.sightstone.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevintcoughlin.sightstone.R;
import com.kevintcoughlin.sightstone.models.news.Item;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private ArrayList<Item> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.title) TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public NewsAdapter(ArrayList<Item> items) {
        mDataSet = items;
    }

    @Override public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = mDataSet.get(position);
        final Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        final Matcher m = p.matcher(item.getDescription());
        String imageSrc = "";
        if (m.find()) {
            imageSrc = m.group(0);
        }

        holder.mTextView.setText(item.getTitle());
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }
}