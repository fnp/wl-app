package com.moiseum.wolnelektury.view.search.filter;

import android.os.Bundle;

import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.base.mvp.FragmentPresenter;
import com.moiseum.wolnelektury.connection.RestClient;
import com.moiseum.wolnelektury.connection.RestClientCallback;
import com.moiseum.wolnelektury.connection.models.CategoryModel;
import com.moiseum.wolnelektury.connection.services.CategoriesService;
import com.moiseum.wolnelektury.view.search.dto.FilterDto;

import java.util.List;

import retrofit2.Call;

/**
 * Created by piotrostrowski on 25.11.2017.
 */

public class FilterPresenter extends FragmentPresenter<FilterView> {

	private FilterDto previousFilters;
	private RestClient restClient;

	private Call<List<CategoryModel>> epochsCall;
	private Call<List<CategoryModel>> genresCall;
	private Call<List<CategoryModel>> kindsCall;

	FilterPresenter(FilterView view, FilterDto filters) {
		super(view);
		this.previousFilters = filters;
		this.restClient = WLApplication.getInstance().getRestClient();
	}

	@Override
	public void onViewCreated(Bundle savedInstanceState) {
		super.onViewCreated(savedInstanceState);
		loadEpochs();
		loadGenres();
		loadKinds();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (epochsCall != null) {
			epochsCall.cancel();
		}
		if (genresCall != null) {
			genresCall.cancel();
		}
		if (kindsCall != null) {
			kindsCall.cancel();
		}
	}

	void loadKinds() {
		kindsCall = restClient.call(new RestClientCallback<List<CategoryModel>, CategoriesService>() {
			@Override
			public void onSuccess(List<CategoryModel> data) {
				matchFilters(previousFilters.getFilteredKinds(), data);
				getView().displayKinds(data);
			}

			@Override
			public void onFailure(Exception e) {
				getView().showKindsError();
			}

			@Override
			public void onCancel() {
				// nop.
			}

			@Override
			public Call<List<CategoryModel>> execute(CategoriesService service) {
				return service.getKinds(true);
			}
		}, CategoriesService.class);
	}

	void loadGenres() {
		genresCall = restClient.call(new RestClientCallback<List<CategoryModel>, CategoriesService>() {
			@Override
			public void onSuccess(List<CategoryModel> data) {
				matchFilters(previousFilters.getFilteredGenres(), data);
				getView().displayGenres(data);
			}

			@Override
			public void onFailure(Exception e) {
				getView().showGenresError();
			}

			@Override
			public void onCancel() {
				// nop.
			}

			@Override
			public Call<List<CategoryModel>> execute(CategoriesService service) {
				return service.getGenres(true);
			}
		}, CategoriesService.class);
	}

	void loadEpochs() {
		epochsCall = restClient.call(new RestClientCallback<List<CategoryModel>, CategoriesService>() {
			@Override
			public void onSuccess(List<CategoryModel> data) {
				matchFilters(previousFilters.getFilteredEpochs(), data);
				getView().displayEpochs(data);
			}

			@Override
			public void onFailure(Exception e) {
				getView().showEpochsError();
			}

			@Override
			public void onCancel() {
				// nop.
			}

			@Override
			public Call<List<CategoryModel>> execute(CategoriesService service) {
				return service.getEpochs(true);
			}
		}, CategoriesService.class);
	}

	private void matchFilters(List<CategoryModel> previousFilters, List<CategoryModel> currentFilters) {
		for (CategoryModel model : currentFilters) {
			for (CategoryModel prevModel : previousFilters) {
				if (model.getSlug().equals(prevModel.getSlug())) {
					model.setChecked(true);
					break;
				}
			}
		}
	}

	void updateFilters(boolean lecturesOnly, boolean audiobook, List<CategoryModel> epochs, List<CategoryModel> genres, List<CategoryModel> kinds) {
		FilterDto dto = new FilterDto();
		dto.setLecture(lecturesOnly);
		dto.setAudiobook(audiobook);
		dto.setFilteredEpochs(epochs);
		dto.setFilteredGenres(genres);
		dto.setFilteredKinds(kinds);
		getView().applyFilters(dto);
	}
}
