package com.moiseum.wolnelektury.view.search.filter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.connection.models.CategoryModel;
import com.moiseum.wolnelektury.view.search.components.FiltersProgressFlowLayout;
import com.moiseum.wolnelektury.view.search.dto.FilterDto;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;

import static com.moiseum.wolnelektury.view.search.filter.FilterActivity.FILTERS_KEY;
import static com.moiseum.wolnelektury.view.search.filter.FilterActivity.RESULT_FILTERS_KEY;

/**
 * Created by piotrostrowski on 25.11.2017.
 */

public class FilterFragment extends PresenterFragment<FilterPresenter> implements FilterView {

	@BindView(R.id.swLecturesOnly)
	SwitchCompat swLecturesOnly;
	@BindView(R.id.swHasAudiobook)
	SwitchCompat swHasAudiobook;
	@BindView(R.id.pflEpochs)
	FiltersProgressFlowLayout pflEpochs;
	@BindView(R.id.pflGenres)
	FiltersProgressFlowLayout pflGenres;
	@BindView(R.id.pflKinds)
	FiltersProgressFlowLayout pflKinds;

	public static FilterFragment newInstance(FilterDto filters) {
		FilterFragment fragment = new FilterFragment();
		Bundle args = new Bundle();
		args.putParcelable(FILTERS_KEY, Parcels.wrap(filters));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected FilterPresenter createPresenter() {
		FilterDto filters = Parcels.unwrap(getArguments().getParcelable(FILTERS_KEY));
		return new FilterPresenter(this, filters);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_filter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		FilterDto filters = Parcels.unwrap(getArguments().getParcelable(FILTERS_KEY));
		swLecturesOnly.setChecked(filters.isLecture());
		swHasAudiobook.setChecked(filters.isAudiobook());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_filter, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_accept) {
			getPresenter().updateFilters(swLecturesOnly.isChecked(),
					swHasAudiobook.isChecked(),
					pflEpochs.getSelectedCategories(),
					pflGenres.getSelectedCategories(),
					pflKinds.getSelectedCategories());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void displayEpochs(List<CategoryModel> data) {
		pflEpochs.addCategories(data);
	}

	@Override
	public void displayGenres(List<CategoryModel> data) {
		pflGenres.addCategories(data);
	}

	@Override
	public void displayKinds(List<CategoryModel> data) {
		pflKinds.addCategories(data);
	}

	@Override
	public void applyFilters(FilterDto filterDto) {
		if (getActivity() != null) {
			Intent intent = new Intent();
			intent.putExtra(RESULT_FILTERS_KEY, Parcels.wrap(filterDto));

			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		}
	}

	@Override
	public void showEpochsError() {
		Toast.makeText(getContext(), R.string.load_epochs_failed, Toast.LENGTH_SHORT).show();
		pflEpochs.showRetryButton(new FiltersProgressFlowLayout.FiltersProgressFlowLayoutRetryListener() {
			@Override
			public void onRetryClicked() {
				getPresenter().loadEpochs();
			}
		});
	}

	@Override
	public void showGenresError() {
		Toast.makeText(getContext(), R.string.load_genres_failed, Toast.LENGTH_SHORT).show();
		pflGenres.showRetryButton(new FiltersProgressFlowLayout.FiltersProgressFlowLayoutRetryListener() {
			@Override
			public void onRetryClicked() {
				getPresenter().loadGenres();
			}
		});
	}

	@Override
	public void showKindsError() {
		Toast.makeText(getContext(), R.string.load_kinds_failed, Toast.LENGTH_SHORT).show();
		pflKinds.showRetryButton(new FiltersProgressFlowLayout.FiltersProgressFlowLayoutRetryListener() {
			@Override
			public void onRetryClicked() {
				getPresenter().loadKinds();
			}
		});
	}
}
