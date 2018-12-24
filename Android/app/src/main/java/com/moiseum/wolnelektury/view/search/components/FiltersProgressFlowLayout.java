package com.moiseum.wolnelektury.view.search.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.connection.models.CategoryModel;
import com.nex3z.flowlayout.FlowLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by piotrostrowski on 26.11.2017.
 */

public class FiltersProgressFlowLayout extends RelativeLayout {

	public interface FiltersProgressFlowLayoutRetryListener {
		void onRetryClicked();
	}

	@BindView(R.id.flList)
	FlowLayout flList;
	@BindView(R.id.tvEmpty)
	TextView tvEmpty;
	@BindView(R.id.pbLoading)
	ProgressBar pbLoading;
	@BindView(R.id.ibRetry)
	ImageButton ibRetry;

	private List<CategoryModel> categories = new ArrayList<>();
	private CompoundButton.OnCheckedChangeListener checkChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			for (CategoryModel category : categories) {
				String slug = (String) buttonView.getTag();
				if (slug.equals(category.getSlug())) {
					category.setChecked(isChecked);
					return;
				}
			}
		}
	};
	private FiltersProgressFlowLayoutRetryListener listener;

	public FiltersProgressFlowLayout(@NonNull Context context) {
		this(context, null);
	}

	public FiltersProgressFlowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FiltersProgressFlowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_flowlayout, this, true);
		ButterKnife.bind(this, view);

		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FiltersProgressFlowLayout);
		try {
			if (a.hasValue(R.styleable.FiltersProgressFlowLayout_emptyLayoutText)) {
				tvEmpty.setText(a.getString(R.styleable.FiltersProgressFlowLayout_emptyLayoutText));
			}
		} finally {
			a.recycle();
		}
	}

	public void addCategories(List<CategoryModel> categories) {
		this.categories = categories;
		LayoutInflater inflater = LayoutInflater.from(getContext());
		for (CategoryModel categoryModel : categories) {
			AppCompatCheckBox checkBox = (AppCompatCheckBox) inflater.inflate(R.layout.checkbox, flList, false);
			checkBox.setText(categoryModel.getName());
			checkBox.setTag(categoryModel.getSlug());
			checkBox.setChecked(categoryModel.isChecked());
			checkBox.setOnCheckedChangeListener(checkChangeListener);
			flList.addView(checkBox);
		}
		pbLoading.setVisibility(GONE);
		flList.setVisibility(VISIBLE);
	}

	public List<CategoryModel> getSelectedCategories() {
		List<CategoryModel> selectedCategories = new ArrayList<>();
		for (CategoryModel categoryModel : categories) {
			if (categoryModel.isChecked()) {
				selectedCategories.add(categoryModel);
			}
		}
		return selectedCategories;
	}

	public void showRetryButton(FiltersProgressFlowLayoutRetryListener listener) {
		this.listener = listener;
		tvEmpty.setVisibility(GONE);
		pbLoading.setVisibility(GONE);
		ibRetry.setVisibility(VISIBLE);
	}

	@OnClick(R.id.ibRetry)
	public void retryButtonClick() {
		if (listener != null) {
			listener.onRetryClicked();
		}
		ibRetry.setVisibility(GONE);
		pbLoading.setVisibility(VISIBLE);
	}
}
