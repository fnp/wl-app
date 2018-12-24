package com.moiseum.wolnelektury.components.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public abstract class ViewHolder<T> extends RecyclerView.ViewHolder {

	public ViewHolder(View view) {
		super(view);
		ButterKnife.bind(this, view);
	}

	public View getView() {
		return itemView;
	}

	protected Context getContext() {
		return getView().getContext();
	}

	public abstract void bind(T item, boolean selected);
}
