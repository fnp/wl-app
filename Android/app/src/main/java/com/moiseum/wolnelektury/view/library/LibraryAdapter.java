package com.moiseum.wolnelektury.view.library;

import android.content.Context;
import android.view.ViewGroup;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.components.recycler.RecyclerAdapter;
import com.moiseum.wolnelektury.connection.models.BookModel;

/**
 * @author golonkos
 */

public class LibraryAdapter extends RecyclerAdapter<BookModel, BookViewHolder> {

	public LibraryAdapter(Context context) {
		super(context, Selection.NONE);
	}

	@Override
	protected String getItemId(BookModel item) {
		return null;
	}

	@Override
	public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new BookViewHolder(inflate(R.layout.book_item, parent));
	}
}
