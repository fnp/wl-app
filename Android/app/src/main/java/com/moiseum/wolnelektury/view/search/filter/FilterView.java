package com.moiseum.wolnelektury.view.search.filter;

import com.moiseum.wolnelektury.connection.models.CategoryModel;
import com.moiseum.wolnelektury.view.search.dto.FilterDto;

import java.util.List;

/**
 * Created by piotrostrowski on 25.11.2017.
 */

public interface FilterView {
	void displayEpochs(List<CategoryModel> data);

	void displayGenres(List<CategoryModel> data);

	void displayKinds(List<CategoryModel> data);

	void applyFilters(FilterDto filterDto);

	void showEpochsError();

	void showGenresError();

	void showKindsError();
}
