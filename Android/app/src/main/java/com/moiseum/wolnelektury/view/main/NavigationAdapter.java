package com.moiseum.wolnelektury.view.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.components.recycler.RecyclerAdapter;
import com.moiseum.wolnelektury.components.recycler.ViewHolder;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;

/**
 * @author golonkos
 */

public class NavigationAdapter extends RecyclerAdapter<NavigationElement, ViewHolder<NavigationElement>> {

	private static int TYPE_ITEM = 0;
	private static int TYPE_SEPARATOR = 1;
	private static int TYPE_SUPPORT = 2;
	private static int TYPE_BLANK = 3;

	private final SupportUsListener supportUsListener;

	private final SharedPreferencesUtils preferences = WLApplication.getInstance().getPreferences();

	NavigationAdapter(Context context, SupportUsListener supportUsListener) {
		super(context, Selection.SINGLE);
		this.supportUsListener = supportUsListener;
		setItems(NavigationElement.valuesForNavigation());
	}

	@Override
	public int getItemViewType(int position) {
		NavigationElement element = getItem(position);
		if (element.requiresLogin() && !preferences.isUserLoggedIn()) {
			return TYPE_BLANK;
		}

		switch (element) {
			case SEPARATOR:
				return TYPE_SEPARATOR;
			case SUPPORT_US:
				return TYPE_SUPPORT;
			default:
				return TYPE_ITEM;
		}
	}

	@NonNull
	@Override
	public ViewHolder<NavigationElement> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == TYPE_SEPARATOR) {
			return new SeparatorViewHolder(inflate(R.layout.navigation_separator_item, parent));
		}
		if (viewType == TYPE_SUPPORT) {
			return new SupportViewHolder(inflate(R.layout.navigation_support_item, parent), supportUsListener);
		}
		if (viewType == TYPE_BLANK) {
			return new NavigationBlankViewHolder(inflate(R.layout.navigation_blank, parent));
		}
		return new NavigationViewHolder(inflate(R.layout.navigation_item, parent));
	}

	@Override
	protected String getItemId(NavigationElement item) {
		return item.name();
	}

	@Override
	protected void onItemClicked(View view, NavigationElement item, int position) {
		if (item != NavigationElement.SEPARATOR && item != NavigationElement.SUPPORT_US) {
			super.onItemClicked(view, item, position);
		}
	}

	interface SupportUsListener {
		void onSupportUsClicked();
	}
}
