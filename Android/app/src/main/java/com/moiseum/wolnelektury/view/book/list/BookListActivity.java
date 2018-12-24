package com.moiseum.wolnelektury.view.book.list;

import android.content.Context;
import android.os.Bundle;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractActivity;
import com.moiseum.wolnelektury.base.AbstractIntent;

import static com.moiseum.wolnelektury.view.book.list.BookListActivity.BookListIntent.PARAM_LIST_TYPE;


public class BookListActivity extends AbstractActivity {

	public static final String BOOK_LIST_FRAGMENT_TAG = "BookListFragmentTag";

	public static class BookListIntent extends AbstractIntent {

		static final String PARAM_LIST_TYPE = "PARAM_LIST_TYPE";

		public BookListIntent(BookListType type, Context context) {
			super(context, BookListActivity.class);
			putExtra(PARAM_LIST_TYPE, type);
		}

	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_blank;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {
		BookListType type = (BookListType) getIntent().getSerializableExtra(PARAM_LIST_TYPE);
		setTitle(type.getActivityTitle());

		BooksListFragment bookListFragment = (BooksListFragment) getSupportFragmentManager().findFragmentByTag(BOOK_LIST_FRAGMENT_TAG);
		if (bookListFragment == null) {
			BooksListFragment fragment = BooksListFragment.newInstance(type);
			getSupportFragmentManager().beginTransaction().add(R.id.flContainer, fragment, BOOK_LIST_FRAGMENT_TAG).commit();
		}
	}
}
