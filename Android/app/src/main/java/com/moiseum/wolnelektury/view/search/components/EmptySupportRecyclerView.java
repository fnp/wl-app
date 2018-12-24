package com.moiseum.wolnelektury.view.search.components;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by piotrostrowski on 17.07.2017.
 */

public class EmptySupportRecyclerView extends RecyclerView {
	private View emptyView;

	private AdapterDataObserver emptyObserver = new AdapterDataObserver() {


		@Override
		public void onChanged() {
			RecyclerView.Adapter<?> adapter = getAdapter();
			if (adapter != null && emptyView != null) {
				if (adapter.getItemCount() == 0) {
					emptyView.setVisibility(View.VISIBLE);
					EmptySupportRecyclerView.this.setVisibility(View.GONE);
				} else {
					emptyView.setVisibility(View.GONE);
					EmptySupportRecyclerView.this.setVisibility(View.VISIBLE);
				}
			}

		}
	};

	public EmptySupportRecyclerView(Context context) {
		super(context);
	}

	public EmptySupportRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmptySupportRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setAdapter(Adapter adapter) {
		super.setAdapter(adapter);

		if (adapter != null) {
			adapter.registerAdapterDataObserver(emptyObserver);
		}

		emptyObserver.onChanged();
	}

	public void setEmptyView(View emptyView) {
		this.emptyView = emptyView;
	}
}