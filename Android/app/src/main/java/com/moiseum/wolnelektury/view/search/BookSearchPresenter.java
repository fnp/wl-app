package com.moiseum.wolnelektury.view.search;

import android.os.Bundle;
import android.os.Handler;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.RestClientCallback;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.models.CategoryModel;
import com.moiseum.wolnelektury.connection.services.BooksService;
import com.moiseum.wolnelektury.utils.StringUtils;
import com.moiseum.wolnelektury.events.BookFavouriteEvent;
import com.moiseum.wolnelektury.view.search.dto.FilterDto;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by piotrostrowski on 16.11.2017.
 */

class BookSearchPresenter extends FragmentPresenter<BookSearchView> {

	private static final String ONLY_LECTURES_SLUG = "only_lectures_slug";
	private static final String HAS_AUDIOBOOK_SLUG = "has_audiobook_slug";
	private static final long TWO_SECONDS = 2000;

	private final String lectureFilterName;
	private final String audiobookFilterName;

	private RestClient client = WLApplication.getInstance().getRestClient();

	private FilterDto filters;
	private String lastKey;
	private String searchQuery;
	private String temporarySearchText;
	private Integer clickedPosition;

	private Call<List<BookModel>> currentCall;

	private Handler searchHandler;
	private Runnable searchHandlerRunnable = new Runnable() {
		@Override
		public void run() {
			searchQuery = temporarySearchText;
			loadBooks(true);
		}
	};

	BookSearchPresenter(BookSearchView view) {
		super(view);
		lectureFilterName = getView().getContext().getString(R.string.lectures);
		audiobookFilterName = getView().getContext().getString(R.string.audiobook);

		filters = new FilterDto();
		lastKey = null;
		searchQuery = null;
	}

	@Override
	public void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
		loadBooks(true);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (currentCall != null) {
			currentCall.cancel();
		}
		if (searchHandler != null) {
			searchHandler.removeCallbacks(searchHandlerRunnable);
		}
		EventBus.getDefault().unregister(this);
	}

	@SuppressWarnings("unused")
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFavouriteStateChanged(BookFavouriteEvent event) {
		getView().updateFavouriteState(event.getState(), clickedPosition);
	}

	void bookSelected(BookModel item, int position) {
		clickedPosition = position;
		getView().presentBookDetails(item.getSlug());
	}

	void updateFilters(FilterDto dto) {
		filters = dto;
		List<CategoryModel> flatteredFilters = new ArrayList<>();
		if (dto.isLecture()) {
			CategoryModel lectureModel = new CategoryModel();
			lectureModel.setName(lectureFilterName);
			lectureModel.setSlug(ONLY_LECTURES_SLUG);
			flatteredFilters.add(lectureModel);
		}
		if (dto.isAudiobook()) {
			CategoryModel audiobookModel = new CategoryModel();
			audiobookModel.setName(audiobookFilterName);
			audiobookModel.setSlug(HAS_AUDIOBOOK_SLUG);
			flatteredFilters.add(audiobookModel);
		}
		flatteredFilters.addAll(dto.getFilteredEpochs());
		flatteredFilters.addAll(dto.getFilteredGenres());
		flatteredFilters.addAll(dto.getFilteredKinds());
		getView().applyFilters(flatteredFilters);
		loadBooks(true);
	}

	void removeFilter(CategoryModel item) {
		if (ONLY_LECTURES_SLUG.equals(item.getSlug())) {
			filters.setLecture(false);
		} else if (HAS_AUDIOBOOK_SLUG.equals(item.getSlug())) {
			filters.setAudiobook(false);
		} else if (filters.getFilteredEpochs().contains(item)) {
			filters.getFilteredEpochs().remove(item);
		} else if (filters.getFilteredGenres().contains(item)) {
			filters.getFilteredGenres().remove(item);
		} else if (filters.getFilteredKinds().contains(item)) {
			filters.getFilteredKinds().remove(item);
		}
		loadBooks(true);
	}

	void onFiltersClicked() {
		getView().displayFiltersView(filters);
	}

	void clearFilters() {
		filters = new FilterDto();
		lastKey = null;
		loadBooks(true);
	}

	void loadMoreBooks(final String key) {
		lastKey = key;
		loadBooks(false);
	}

	void onSearchChosen(String query) {
		if (query.length() == 0) {
			query = null;
		}
		searchQuery = query;
		loadBooks(true);
	}

	void onSearchChanged(String newText) {
		if (searchHandler != null) {
			searchHandler.removeCallbacks(searchHandlerRunnable);
		}

		if (newText.length() == 0) {
			onSearchChosen(newText);
		} else {
			temporarySearchText = newText;
			searchHandler = new Handler();
			searchHandler.postDelayed(searchHandlerRunnable, TWO_SECONDS);
		}
	}

	void retryInitialLoad() {
		loadBooks(true);
	}

	private void loadBooks(final boolean reset) {
		if (reset) {
			lastKey = null;
			getView().setInitialProgressVisible(true);
		} else {
			getView().setLoadMoreProgressVisible(true);
		}
		currentCall = client.call(new RestClientCallback<List<BookModel>, BooksService>() {
			@Override
			public void onSuccess(List<BookModel> data) {
				hideProgress();
				getView().setData(data, reset);
			}

			@Override
			public void onFailure(Exception e) {
				hideProgress();
				getView().showError(e, false);
			}

			@Override
			public void onCancel() {
				//nop
			}

			@Override
			public Call<List<BookModel>> execute(BooksService service) {
				return service.getSearchBooks(searchQuery,
						StringUtils.joinSlugs(filters.getFilteredEpochs(), ","),
						StringUtils.joinSlugs(filters.getFilteredGenres(), ","),
						StringUtils.joinSlugs(filters.getFilteredKinds(), ","),
						filters.isAudiobook() ? true : null,
						filters.isLecture() ? true : null,
						lastKey,
						RestClient.PAGINATION_LIMIT);
			}

			private void hideProgress() {
				if (reset) {
					getView().setInitialProgressVisible(false);
				} else {
					getView().setLoadMoreProgressVisible(false);
				}
			}
		}, BooksService.class);
	}
}
