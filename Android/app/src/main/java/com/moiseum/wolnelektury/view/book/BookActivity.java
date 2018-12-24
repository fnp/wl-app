package com.moiseum.wolnelektury.view.book;

import android.content.Context;
import android.os.Bundle;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractActivity;
import com.moiseum.wolnelektury.base.AbstractIntent;

/**
 * Created by Piotr Ostrowski on 17.11.2017.
 */

public class BookActivity extends AbstractActivity {

	private static final String BOOK_FRAGMENT_TAG = "BookFragmentTag";
	static final String BOOK_SLUG_KEY = "BookSlugKey";
	static final String BOOK_TYPE_KEY = "BookTypeKey";

	public static class BookIntent extends AbstractIntent {

		public BookIntent(String slug, BookType type, Context context) {
			super(context, BookActivity.class);
			putExtra(BOOK_SLUG_KEY, slug);
			putExtra(BOOK_TYPE_KEY, type.name());
		}
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_blank;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {
		setTitle("");
		if (!getIntent().hasExtra(BOOK_SLUG_KEY)) {
			throw new IllegalStateException("Missing either slug or full ebook model.");
		}
		if (!getIntent().hasExtra(BOOK_TYPE_KEY)) {
			throw new IllegalStateException("Missing book type.");
		}

		String bookSlug = getIntent().getStringExtra(BOOK_SLUG_KEY);
		BookType type = BookType.valueOf(getIntent().getStringExtra(BOOK_TYPE_KEY));

		BookFragment bookFragment = (BookFragment) getSupportFragmentManager().findFragmentByTag(BOOK_FRAGMENT_TAG);
		if (bookFragment == null) {
			bookFragment = BookFragment.newInstance(bookSlug, type);
			getSupportFragmentManager().beginTransaction().add(R.id.flContainer, bookFragment, BOOK_FRAGMENT_TAG).commit();
		}
	}
}
