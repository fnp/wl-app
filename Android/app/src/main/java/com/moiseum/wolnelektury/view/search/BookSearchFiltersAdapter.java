package com.moiseum.wolnelektury.view.search;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.components.recycler.RecyclerAdapter;
import com.moiseum.wolnelektury.components.recycler.ViewHolder;
import com.moiseum.wolnelektury.connection.models.CategoryModel;

import butterknife.BindView;

/**
 * Created by Piotr Ostrowski on 27.11.2017.
 */

public class BookSearchFiltersAdapter extends RecyclerAdapter<CategoryModel, BookSearchFiltersAdapter.FilterViewHolder> {

	static class FilterViewHolder extends ViewHolder<CategoryModel> {

		@BindView(R.id.tvFilterName)
		TextView tvFilterName;

		FilterViewHolder(View view) {
			super(view);
		}

		@Override
		public void bind(CategoryModel item, boolean selected) {
			tvFilterName.setText(item.getName());
		}
	}

	public BookSearchFiltersAdapter(Context context) {
		super(context, Selection.NONE);
	}

	@Override
	protected String getItemId(CategoryModel item) {
		return item.getSlug();
	}

	@Override
	public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new FilterViewHolder(inflate(R.layout.filter_item, parent));
	}
}
