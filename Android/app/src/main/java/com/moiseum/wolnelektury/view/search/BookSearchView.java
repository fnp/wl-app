package com.moiseum.wolnelektury.view.search;

import android.content.Context;

import com.moiseum.wolnelektury.base.mvp.PaginableLoadingView;
import com.moiseum.wolnelektury.connection.models.BookModel;
import com.moiseum.wolnelektury.connection.models.CategoryModel;
import com.moiseum.wolnelektury.view.search.dto.FilterDto;

import java.util.List;

/**
 * Created by piotrostrowski on 16.11.2017.
 */

public interface BookSearchView extends PaginableLoadingView<List<BookModel>> {

	void presentBookDetails(String bookSlug);

	void applyFilters(List<CategoryModel> filters);

	void displayFiltersView(FilterDto filters);

	void updateFavouriteState(boolean state, Integer clickedPosition);

	Context getContext();
}
