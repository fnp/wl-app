package com.moiseum.wolnelektury.storage;

import android.app.Application;
import android.support.annotation.Nullable;

import com.moiseum.wolnelektury.BuildConfig;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.models.BookModel_;
import com.moiseum.wolnelektury.connection.models.MyObjectBox;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;
import io.objectbox.query.Query;

/**
 * @author golonkos
 */

public class BookStorage {


	public static class BookAddedEvent {
	}

	public static class BookDeletedEvent {
		private final String slug;

		public BookDeletedEvent(String slug) {
			this.slug = slug;
		}

		public String getSlug() {
			return slug;
		}
	}

	private BoxStore boxStore;

	public BookStorage(Application application) {
		boxStore = MyObjectBox.builder().androidContext(application).build();
		if (BuildConfig.DEBUG) {
			new AndroidObjectBrowser(boxStore).start(application);
		}
	}

	private Box<BookModel> getBox() {
		return boxStore.boxFor(BookModel.class);
	}

	public void add(BookModel book) {
		getBox().put(book);
		EventBus.getDefault().post(new BookAddedEvent());
	}

	public void update(BookModel book) {
		getBox().put(book);
	}

	@Nullable
	public BookModel find(String slug) {
		Query<BookModel> query = getBox().query().equal(BookModel_.slug, slug).build();
		return query.findFirst();
	}

	public boolean exists(String slug) {
		return find(slug) != null;
	}

	public void remove(String slug, boolean notify) {
		BookModel book = find(slug);
		if (book != null) {
			getBox().remove(book);
			if (notify) {
				EventBus.getDefault().post(new BookDeletedEvent(slug));
			}
		}
	}

	public List<BookModel> all() {
		return getBox().getAll();
	}

	public void removeAll() {
		getBox().removeAll();
	}
}
