package com.moiseum.wolnelektury.components.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moiseum.wolnelektury.connection.models.CategoryModel;

import java.util.Collections;
import java.util.List;

/**
 * @author golonkos
 */

public abstract class RecyclerAdapter<T, VH extends ViewHolder> extends RecyclerView.Adapter<VH> {

	/**
	 * On click listener.
	 */
	public interface OnItemClickListener<T> {
		/**
		 * @param item clicked item
		 * @param view clicked view
		 */
		void onItemClicked(T item, View view, int position);
	}

	public enum Selection {
		NONE, SINGLE
	}

	private static final int NO_POSITION = -1;

	private LayoutInflater layoutInflater;
	private OnItemClickListener<T> onItemClickListener;

	private List<T> items = Collections.emptyList();

	private T selectedItem;
	private int selectedItemPosition = NO_POSITION;

	private Selection selection = Selection.NONE;

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (int) v.getTag();
			T item = getItem(position);
			onItemClicked(v, item, position);
		}
	};

	public RecyclerAdapter(Context context, Selection selection) {
		layoutInflater = LayoutInflater.from(context);
		this.selection = selection;
	}

	protected void onItemClicked(View view, T item, int position) {
		selectItemAndNotify(item, position);
		if (onItemClickListener != null) {
			onItemClickListener.onItemClicked(item, view, position);
		}
	}

	@Override
	public void onBindViewHolder(VH viewHolder, final int position) {
		viewHolder.itemView.setTag(position);
		viewHolder.itemView.setOnClickListener(onClickListener);
		T item = getItem(position);
		viewHolder.itemView.setSelected(isSelected(getItem(position)));
		viewHolder.bind(item, isSelected(item));
	}

	@Override
	public int getItemCount() {
		return items.size();
	}


	/**
	 * @param onItemClickListener item click listener
	 */
	public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}


	public OnItemClickListener<T> getOnItemClickListener() {
		return onItemClickListener;
	}

	public List<T> getItems() {
		return items;
	}

	/**
	 * @param items new items
	 */
	public void setItems(List<T> items) {
		this.items = items;
		notifyDataSetChanged();
	}

	public void addItems(List<T> items) {
		this.items.addAll(items);
		notifyDataSetChanged();
	}

	/**
	 * @param position position of element to remove
	 * @return removed item or null if element not found
	 */
	public T removeItem(int position) {
		if (position >= 0 && position < items.size()) {
			T item = items.remove(position);
			notifyItemRemoved(position);
			notifyItemRangeChanged(position, items.size());
			return item;
		}
		return null;
	}

	public void clear() {
		this.items.clear();
		notifyDataSetChanged();
	}

	protected void addItem(int position, T item) {
		items.add(position, item);
	}

	protected View inflate(int layoutResId, ViewGroup parent) {
		return layoutInflater.inflate(layoutResId, parent, false);
	}

	/**
	 * @param position position of element
	 * @return item from specific position
	 */
	public T getItem(int position) {
		return this.items.get(position);
	}

	protected abstract String getItemId(T item);

	private boolean isSelected(T item) {
		return selectedItem != null && getItemId(selectedItem).contains(getItemId(item));
	}

	public void selectItem(T item) {
		int position = NO_POSITION;
		for (int i = 0; i < items.size(); i++) {
			if (getItemId(item).equals(getItemId(items.get(i)))) {
				position = i;
				break;
			}
		}
		selectItemAndNotify(item, position);
	}

	private void selectItemAndNotify(T item, int position) {
		int selectedPositionToNotify = selectedItemPosition;
		setSelectedItem(item, position);

		if (selectedPositionToNotify != NO_POSITION) {
			notifyItemChanged(selectedPositionToNotify);
		}
		notifyItemChanged(position);
	}

	private void setSelectedItem(T item, int position) {
		switch (selection) {
			case SINGLE:
				selectedItem = item;
				selectedItemPosition = position;
				break;
			case NONE:
				break;
		}
	}


}

