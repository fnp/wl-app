package com.moiseum.wolnelektury.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.components.recycler.RecyclerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author golonkos
 */

public class ProgressRecyclerView<T> extends FrameLayout {

	public interface ProgressRecycleViewRetryListener {
		void onRetryClicked();
	}

	@BindView(R.id.rvList)
	RecyclerView rvList;
	@BindView(R.id.tvEmpty)
	TextView tvEmpty;
	@BindView(R.id.pbLoading)
	ProgressBar pbLoading;
	@BindView(R.id.ibRetry)
	ImageButton ibRetry;

	private RecyclerAdapter<T, ?> adapter;
	private ProgressRecycleViewRetryListener listener;

	public ProgressRecyclerView(@NonNull Context context) {
		this(context, null);
	}

	public ProgressRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ProgressRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_recyclerview, this, true);
		ButterKnife.bind(this, view);

		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressRecyclerView);
		try {
			if (a.hasValue(R.styleable.ProgressRecyclerView_emptyText)) {
				tvEmpty.setText(a.getString(R.styleable.ProgressRecyclerView_emptyText));
			}
		} finally {
			a.recycle();
		}
	}

	public void setAdapter(RecyclerAdapter<T, ?> adapter) {
		this.adapter = adapter;
		rvList.setAdapter(adapter);
	}

	public void setItems(List<T> items) {
		if (adapter == null) {
			throw new UnsupportedOperationException("Adapter not set");
		}
		adapter.setItems(items);
		tvEmpty.setVisibility(items.isEmpty() ? VISIBLE : GONE);
	}

	public void addItems(List<T> items) {
		if (adapter == null) {
			throw new UnsupportedOperationException("Adapter not set");
		}
		if (items.size() > 0) {
			adapter.addItems(items);
		}
	}

	public void setProgressVisible(boolean visible) {
		pbLoading.setVisibility(visible ? VISIBLE : GONE);
		if (visible) {
			tvEmpty.setVisibility(GONE);
		}
	}

	public void showRetryButton(ProgressRecycleViewRetryListener listener) {
		this.listener = listener;
		tvEmpty.setVisibility(GONE);
		ibRetry.setVisibility(VISIBLE);
	}

	public void setEmptyText(@StringRes int stringResId) {
		tvEmpty.setText(stringResId);
	}

	public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
		rvList.setLayoutManager(layoutManager);
	}

	public void setHasFixedSize(boolean fixedSize) {
		rvList.setHasFixedSize(fixedSize);
	}

	public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
		rvList.addOnScrollListener(listener);
	}

	public void removeOnScrollListener(RecyclerView.OnScrollListener listener) {
		rvList.removeOnScrollListener(listener);
	}

	@OnClick(R.id.ibRetry)
	public void retryButtonClick() {
		if (listener != null) {
			listener.onRetryClicked();
		}
		ibRetry.setVisibility(GONE);
	}

	public void updateEmptyViewVisibility() {
		tvEmpty.setVisibility(ibRetry.getVisibility() == GONE && adapter.getItems().isEmpty() ? VISIBLE : GONE);
	}
}
