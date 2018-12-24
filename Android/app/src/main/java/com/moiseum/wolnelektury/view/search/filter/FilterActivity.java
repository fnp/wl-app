package com.moiseum.wolnelektury.view.search.filter;

import android.content.Context;
import android.os.Bundle;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractActivity;
import com.moiseum.wolnelektury.base.AbstractIntent;
import com.moiseum.wolnelektury.view.search.dto.FilterDto;

import org.parceler.Parcels;

/**
 * Created by piotrostrowski on 25.11.2017.
 */

public class FilterActivity extends AbstractActivity {

	private static final String FILTER_FRAGMENT_TAG = "FilterFragmentTag";
	public static final String RESULT_FILTERS_KEY = "ResultFiltersKey";
	public static final String FILTERS_KEY = "FiltersKey";
	public static final int FILTERS_REQUEST_CODE = 105;

	public static class FilterIntent extends AbstractIntent {

		public FilterIntent(Context packageContext, FilterDto dto) {
			super(packageContext, FilterActivity.class);
			putExtra(FILTERS_KEY, Parcels.wrap(dto));
		}
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_blank;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		setTitle(R.string.filters);

		FilterFragment filterFragment = (FilterFragment) getSupportFragmentManager().findFragmentByTag(FILTER_FRAGMENT_TAG);
		if (filterFragment == null) {
			FilterDto filters = Parcels.unwrap(getIntent().getParcelableExtra(FILTERS_KEY));
			filterFragment = FilterFragment.newInstance(filters);
			getSupportFragmentManager().beginTransaction().add(R.id.flContainer, filterFragment, FILTER_FRAGMENT_TAG).commit();
		}
	}
}
