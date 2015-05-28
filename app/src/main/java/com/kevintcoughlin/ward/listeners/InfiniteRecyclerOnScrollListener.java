package com.kevintcoughlin.ward.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class InfiniteRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
	private int currentPage = 0;
	private int previousTotal = 0;
	private boolean loading = true;

	private LinearLayoutManager mLinearLayoutManager;

	public InfiniteRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
		this.mLinearLayoutManager = linearLayoutManager;
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		super.onScrolled(recyclerView, dx, dy);

		final int visibleItemCount = recyclerView.getChildCount();
		final int totalItemCount = mLinearLayoutManager.getItemCount();
		final int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
		final int visibleThreshold = 3;

		if (loading && (totalItemCount > previousTotal)) {
			loading = false;
			previousTotal = totalItemCount;
		} else if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
			loading = true;
			currentPage++;
			onLoadMore(currentPage);
		}
	}

	public abstract void onLoadMore(int currentPage);
}
