package com.moiseum.wolnelektury.view.main;

import android.support.v4.app.Fragment;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.view.AboutFragment;
import com.moiseum.wolnelektury.view.book.list.BookListType;
import com.moiseum.wolnelektury.view.book.list.BooksListFragment;
import com.moiseum.wolnelektury.view.library.LibraryFragment;
import com.moiseum.wolnelektury.view.news.NewsListFragment;
import com.moiseum.wolnelektury.view.search.BookSearchFragment;
import com.moiseum.wolnelektury.view.settings.SettingsFragment;

import java.util.Arrays;
import java.util.List;

/**
 * @author golonkos
 */

public enum NavigationElement {

	LIBRARY {
		@Override
		public int getTitle() {
			return R.string.nav_wolne_lektury;
		}

		@Override
		public Fragment getFragment() {
			return LibraryFragment.newInstance();
		}

		@Override
		public int getIcon() {
			return R.drawable.ic_menu_library;
		}

		@Override
		public boolean requiresLogin() {
			return false;
		}
	},

	PREMIUM {
		@Override
		public int getTitle() {
			return R.string.nav_premium;
		}

		@Override
		public Fragment getFragment() {
			// This in intentional. We have to handle this separately.
			return null;
		}

		@Override
		public int getIcon() {
			return R.drawable.ic_menu_star;
		}

		@Override
		public boolean requiresLogin() {
			return false;
		}
	},

	NOW_READING {
		@Override
		public int getTitle() {
			return R.string.nav_reading;
		}

		@Override
		public Fragment getFragment() {
			return BooksListFragment.newInstance(BookListType.READING);
		}

		@Override
		public int getIcon() {
			return R.drawable.ic_book;
		}

		@Override
		public boolean requiresLogin() {
			return true;
		}
	},

	FAVOURITES {
		@Override
		public int getTitle() {
			return R.string.nav_favourites;
		}

		@Override
		public Fragment getFragment() {
			return BooksListFragment.newInstance(BookListType.FAVOURITES);
		}

		@Override
		public int getIcon() {
			return R.drawable.ic_menu_fav;
		}

		@Override
		public boolean requiresLogin() {
			return true;
		}
	},

	COMPLETED {
		@Override
		public int getTitle() {
			return R.string.nav_completed;
		}

		@Override
		public Fragment getFragment() {
			return BooksListFragment.newInstance(BookListType.COMPLETED);
		}

		@Override
		public int getIcon() {
			return R.drawable.ic_accept;
		}

		@Override
		public boolean requiresLogin() {
			return true;
		}
	},

	AUDIOBOOKS {
		@Override
		public int getTitle() {
			return R.string.nav_audiobooks;
		}

		@Override
		public Fragment getFragment() {
			return BooksListFragment.newInstance(BookListType.AUDIOBOOKS);
		}

		@Override
		public int getIcon() {
			return R.drawable.ic_menu_audiobook;
		}

		@Override
		public boolean requiresLogin() {
			return false;
		}
	},

	DOWNLOADED {
		@Override
		public int getTitle() {
			return R.string.nav_my_collection;
		}

		@Override
		public Fragment getFragment() {
			return BooksListFragment.newInstance(BookListType.DOWNLOADED);
		}

		@Override
		public int getIcon() {
			return R.drawable.ic_menu_downloaded;
		}

		@Override
		public boolean requiresLogin() {
			return false;
		}
	},

	SEARCH {
		@Override
		public int getTitle() {
			return R.string.nav_catalog;
		}

		@Override
		public Fragment getFragment() {
			return BookSearchFragment.newInstance();
		}

		@Override
		public int getIcon() {
			return R.drawable.ic_menu_search;
		}

		@Override
		public boolean requiresLogin() {
			return false;
		}
	},

	ABOUT {
		@Override
		public int getTitle() {
			return R.string.nav_about;
		}

		@Override
		public Fragment getFragment() {
			return AboutFragment.newInstance();
		}

		@Override
		public int getIcon() {
			return R.drawable.ic_about;
		}

		@Override
		public boolean requiresLogin() {
			return false;
		}
	},

	NEWS {
		@Override
		public int getTitle() {
			return R.string.nav_news;
		}

		@Override
		public Fragment getFragment() {
			return NewsListFragment.newInstance();
		}

		@Override
		public int getIcon() {
			return R.drawable.ic_news;
		}

		@Override
		public boolean requiresLogin() {
			return false;
		}
	},

	SETTINGS {
		@Override
		public int getTitle(){return R.string.settings;}

		@Override
		public Fragment getFragment() {return SettingsFragment.newInstance();}

		@Override
		public int getIcon(){return R.drawable.ic_settings;}

		@Override
		public boolean requiresLogin() {
			return false;
		}
	},

	SUPPORT_US {
		@Override
		public int getTitle() {
			return -1;
		}

		@Override
		public Fragment getFragment() {
			return null;
		}

		@Override
		public int getIcon() {
			return -1;
		}

		@Override
		public boolean requiresLogin() {
			return false;
		}
	},

	SEPARATOR {
		@Override
		public int getTitle() {
			return -1;
		}

		@Override
		public Fragment getFragment() {
			return null;
		}

		@Override
		public int getIcon() {
			return -1;
		}

		@Override
		public boolean requiresLogin() {
			return false;
		}
	};

	public abstract int getTitle();

	public abstract Fragment getFragment();

	public abstract int getIcon();

	public abstract boolean requiresLogin();

	public static List<NavigationElement> valuesForNavigation() {
		return Arrays.asList(/*SUPPORT_US, */LIBRARY, SEPARATOR,/*PREMIUM, */SEARCH, AUDIOBOOKS, NOW_READING, FAVOURITES, COMPLETED, SEPARATOR, DOWNLOADED, SEPARATOR, NEWS, SETTINGS, ABOUT);
	}
}
