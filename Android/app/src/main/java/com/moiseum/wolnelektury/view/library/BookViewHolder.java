package com.moiseum.wolnelektury.view.library;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.components.recycler.ViewHolder;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.models.BookModel;

import butterknife.BindView;

/**
 * @author golonkos
 */

class BookViewHolder extends ViewHolder<BookModel> {

	private static final String DEFAULT_OVERLAY_COLOR = "#80db4b16";

	@BindView(R.id.ivBookCover)
	ImageView ivBookCover;
	@BindView(R.id.tvBookAuthor)
	TextView tvBookAuthor;
	@BindView(R.id.tvBookTitle)
	TextView tvBookTitle;
	@BindView(R.id.ivAudioBook)
	ImageView ivAudioBook;
	@BindView(R.id.llBookContent)
	LinearLayout llBookContent;

	BookViewHolder(View view) {
		super(view);
	}

	@Override
	public void bind(BookModel item, boolean selected) {
		if (item.getCoverThumb() != null) {
			String coverUrl = item.getCoverThumb();
			if (!coverUrl.contains(RestClient.MEDIA_URL) && !coverUrl.contains(RestClient.MEDIA_URL_HTTPS)) {
				coverUrl = RestClient.MEDIA_URL_HTTPS + coverUrl;
			}
			Glide.with(getContext()).load(coverUrl).placeholder(R.drawable.list_nocover).diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().into(ivBookCover);
		}
		tvBookAuthor.setText(item.getAuthor());
		tvBookTitle.setText(item.getTitle());
		ivAudioBook.setVisibility(item.isHasAudio() ? View.VISIBLE : View.GONE);
		String colorHash = item.getCoverColor() != null ? item.getCoverColor() : DEFAULT_OVERLAY_COLOR;
		llBookContent.setBackgroundColor(Color.parseColor(colorHash));
	}
}
