package com.moiseum.wolnelektury.view.book.list;

import android.support.annotation.StringRes;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.DataProvider;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.models.ReadingStateModel;
import com.moiseum.wolnelektury.connection.services.BooksService;

import java.util.List;

/**
 * @author golonkos
 */

public enum BookListType {

	DOWNLOADED {
		@Override
		public DataProvider<List<BookModel>, BooksService> getDataProvider() {
			return new DownloadedBooksDataProvider();
		}

		@Override
		public boolean isDeletable() {
			return true;
		}

		@Override
		public boolean isSearchable() {
			return false;
		}

		@Override
		public boolean isPageable() {
			return false;
		}

		@Override
		public String getNameForTracker() {
			return "DownloadedList";
		}

		@Override
		public int getActivityTitle() {
			return R.string.nav_downloaded;
		}

		@Override
		public int getEmptyListText() {
			return R.string.downloaded_empty_list;
		}
	},

	AUDIOBOOKS {
		@Override
		public DataProvider<List<BookModel>, BooksService> getDataProvider() {
			return new AudiobooksDataProvider();
		}

		@Override
		public boolean isDeletable() {
			return false;
		}

		@Override
		public boolean isSearchable() {
			return true;
		}

		@Override
		public boolean isPageable() {
			return true;
		}

		@Override
		public String getNameForTracker() {
			return "AudiobooksList";
		}

		@Override
		public int getActivityTitle() {
			return R.string.nav_audiobooks;
		}

		@Override
		public int getEmptyListText() {
			return R.string.audiobooks_empty_list;
		}
	},

	NEWEST {
		@Override
		public DataProvider<List<BookModel>, BooksService> getDataProvider() {
			return new NewestBooksDataProvider();
		}

		@Override
		public boolean isDeletable() {
			return false;
		}

		@Override
		public boolean isSearchable() {
			return false;
		}

		@Override
		public boolean isPageable() {
			return false;
		}

		@Override
		public String getNameForTracker() {
			return "NewestList";
		}

		@Override
		public int getActivityTitle() {
			return R.string.book_list_newest_title;
		}

		@Override
		public int getEmptyListText() {
			return R.string.newest_empty_list;
		}
	},

	RECOMMENDED {
		@Override
		public DataProvider<List<BookModel>, BooksService> getDataProvider() {
			return new RecommendedBooksDataProvider();
		}

		@Override
		public boolean isDeletable() {
			return false;
		}

		@Override
		public boolean isSearchable() {
			return false;
		}

		@Override
		public boolean isPageable() {
			return false;
		}

		@Override
		public String getNameForTracker() {
			return "RecommendedList";
		}

		@Override
		public int getActivityTitle() {
			return R.string.book_list_recommended_title;
		}

		@Override
		public int getEmptyListText() {
			return R.string.recommended_empty_list;
		}
	},

	READING {
		@Override
		public DataProvider<List<BookModel>, BooksService> getDataProvider() {
			return new ReadingStateDataProvider(ReadingStateModel.ReadingState.STATE_READING);
		}

		@Override
		public boolean isDeletable() {
			return false;
		}

		@Override
		public boolean isSearchable() {
			return false;
		}

		@Override
		public boolean isPageable() {
			return true;
		}

		@Override
		public String getNameForTracker() {
			return "NowReadingList";
		}

		@Override
		public int getActivityTitle() {
			return R.string.nav_reading;
		}

		@Override
		public int getEmptyListText() {
			return R.string.reading_empty_list;
		}
	},

	FAVOURITES {
		@Override
		public DataProvider<List<BookModel>, BooksService> getDataProvider() {
			return new FavouritesDataProvider();
		}
		@Override
		public boolean isDeletable() {
			return false;
		}

		@Override
		public boolean isSearchable() {
			return false;
		}

		@Override
		public boolean isPageable() {
			return true;
		}

		@Override
		public String getNameForTracker() {
			return "FavouritesList";
		}

		@Override
		public int getActivityTitle() {
			return R.string.nav_favourites;
		}

		@Override
		public int getEmptyListText() {
			return R.string.faviourites_empty_list;
		}
	},

	COMPLETED {
		@Override
		public DataProvider<List<BookModel>, BooksService> getDataProvider() {
			return new ReadingStateDataProvider(ReadingStateModel.ReadingState.STATE_COMPLETED);
		}

		@Override
		public boolean isDeletable() {
			return false;
		}

		@Override
		public boolean isSearchable() {
			return false;
		}

		@Override
		public boolean isPageable() {
			return true;
		}

		@Override
		public String getNameForTracker() {
			return "CompletedList";
		}

		@Override
		public int getActivityTitle() {
			return R.string.nav_completed;
		}

		@Override
		public int getEmptyListText() {
			return R.string.completed_empty_list;
		}
	};

	public abstract DataProvider<List<BookModel>, BooksService> getDataProvider();

	public abstract boolean isDeletable();

	public abstract boolean isSearchable();

	public abstract boolean isPageable();

	public abstract String getNameForTracker();

	@StringRes
	public abstract int getActivityTitle();

	@StringRes
	public abstract int getEmptyListText();
}
