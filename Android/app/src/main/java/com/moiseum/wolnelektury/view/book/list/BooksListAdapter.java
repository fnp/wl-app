package com.moiseum.wolnelektury.view.book.list;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.components.recycler.RecyclerAdapter;
import com.moiseum.wolnelektury.components.recycler.ViewHolder;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;

import butterknife.BindView;

/**
 * Created by piotrostrowski on 16.11.2017.
 */

public class BooksListAdapter extends RecyclerAdapter<BookModel, BooksListAdapter.BookViewHolder> {

	public interface BooksListDeletionListener {
		void onDeleteBookClicked(BookModel book, int position);
	}

	private BooksListDeletionListener listener;
	private View.OnClickListener deleteButtonClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (listener != null) {
				int position = (int) v.getTag();
				BookModel book = getItem(position);
				listener.onDeleteBookClicked(book, position);
			}
		}
	};

	public BooksListAdapter(Context context) {
		super(context, RecyclerAdapter.Selection.NONE);
	}

	@Override
	protected String getItemId(BookModel item) {
		return item.getSlug();
	}

	@NonNull
	@Override
	public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new BookViewHolder(inflate(R.layout.list_search, parent));
	}

	@Override
	public void onBindViewHolder(BookViewHolder viewHolder, int position) {
		super.onBindViewHolder(viewHolder, position);
		viewHolder.ibDeleteEbook.setTag(position);
		viewHolder.ibDeleteEbook.setOnClickListener(deleteButtonClick);
	}

	public void setOnDeleteListener(BooksListDeletionListener listener) {
		this.listener = listener;
	}

	static class BookViewHolder extends ViewHolder<BookModel> {

		@BindView(R.id.ivBookCover)
		ImageView ivBookCover;
		@BindView(R.id.tvBookAuthor)
		TextView tvBookAuthor;
		@BindView(R.id.tvBookTitle)
		TextView tvBookTitle;
		@BindView(R.id.tvBookEpoch)
		TextView tvBookEpoch;
		@BindView(R.id.tvBookGenre)
		TextView tvBookGenre;
		@BindView(R.id.tvBookKind)
		TextView tvBookKind;
		@BindView(R.id.ibDeleteEbook)
		ImageButton ibDeleteEbook;
		@BindView(R.id.ivEbook)
		ImageView ivEbook;
		@BindView(R.id.tvEbookReaden)
		TextView tvEbookReaden;
		@BindView(R.id.ivAudioBook)
		ImageView ivAudioBook;
		@BindView(R.id.tvAudioBookReaden)
		TextView tvAudioBookReaden;
		private final SharedPreferencesUtils preferences = WLApplication.getInstance().getPreferences();

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
			} else {
				ivBookCover.setImageResource(R.drawable.list_nocover);
			}
			tvBookAuthor.setText(item.getAuthor());
			tvBookTitle.setText(item.getTitle());
			tvBookEpoch.setText(item.getEpoch());
			tvBookGenre.setText(item.getGenre());
			tvBookKind.setText(item.getKind());
			ibDeleteEbook.setVisibility(item.isDeletable() ? View.VISIBLE : View.GONE);
			if (item.getCurrentChapter() != 0 && item.getTotalChapters() != 0) {
				SpannableString progressSpannable = spanStringSize(item, false, R.string.reading_progress);
				tvEbookReaden.setVisibility(View.VISIBLE);
				tvEbookReaden.setText(progressSpannable);
			} else {
				tvEbookReaden.setVisibility(View.GONE);
			}
			if (item.isHasAudio()) {
				ivAudioBook.setVisibility(View.VISIBLE);
				if (item.getCurrentAudioChapter() != 0 && item.getTotalAudioChapters() != 0) {
					SpannableString progressSpannable = spanStringSize(item, true, R.string.listening_progress);
					tvAudioBookReaden.setVisibility(View.VISIBLE);
					tvAudioBookReaden.setText(progressSpannable);
				} else {
					tvAudioBookReaden.setVisibility(View.GONE);
				}
			} else {
				ivAudioBook.setVisibility(View.GONE);
				tvAudioBookReaden.setVisibility(View.GONE);
			}
		}

		private SpannableString spanStringSize(BookModel item, boolean isAudioPart, int resourceId) {
			float currentChapter = isAudioPart ? (float) item.getCurrentAudioChapter() : (float) item.getCurrentChapter();
			float totalChapter = isAudioPart ? (float) item.getTotalAudioChapters() : (float) item.getTotalChapters();
			int progress = (int) ((currentChapter / totalChapter) * 100.f);

			String progressText = getContext().getString(resourceId, progress);
			SpannableString progressSpannable = new SpannableString(progressText.toUpperCase());
			int size = ((int) getContext().getResources().getDimension(R.dimen.list_title_text_size));
			progressSpannable.setSpan(new AbsoluteSizeSpan(size), progressText.indexOf(" "), progressText.length() - 1, 0); // set size
			return progressSpannable;
		}
	}
}
