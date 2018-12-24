package com.moiseum.wolnelektury.view.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.components.recycler.RecyclerAdapter;
import com.moiseum.wolnelektury.components.recycler.ViewHolder;
import com.moiseum.wolnelektury.connection.models.NewsModel;

import butterknife.BindView;

public class NewsListAdapter extends RecyclerAdapter<NewsModel, NewsListAdapter.NewsViewHolder> {

	static class NewsViewHolder extends ViewHolder<NewsModel> {

		@BindView(R.id.textViewDate)
		TextView txtDate;
		@BindView(R.id.textViewLead)
		TextView txtLead;
		@BindView(R.id.ivNewsThumb)
		ImageView newsImage;

		NewsViewHolder(View view) {
			super(view);
		}

		@Override
		public void bind(NewsModel item, boolean selected) {
			txtDate.setText(item.getTime());
			txtLead.setText(item.getTitle());
			Glide.with(getContext()).load(item.getThumbUrl()).placeholder(R.drawable.list_nocover).diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().into(newsImage);
		}
	}

	NewsListAdapter(Context context) {
		super(context, RecyclerAdapter.Selection.NONE);
	}

	@NonNull
	@Override
	public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = inflate(R.layout.news_item, parent);
		return new NewsViewHolder(view);
	}

	@Override
	protected String getItemId(NewsModel item) {
		return item.getKey();
	}
}