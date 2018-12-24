package com.moiseum.wolnelektury.view.main;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.WLApplication;
import com.moiseum.wolnelektury.components.recycler.ViewHolder;
import com.moiseum.wolnelektury.utils.SharedPreferencesUtils;

import butterknife.BindView;

/**
 * @author golonkos
 */

public class NavigationViewHolder extends ViewHolder<NavigationElement> {

	@BindView(R.id.tvNavName)
	TextView tvName;
	@BindView(R.id.ivNavIcon)
	ImageView ivNavIcon;

	NavigationViewHolder(View view) {
		super(view);
	}

	@Override
	public void bind(NavigationElement item, boolean selected) {
		tvName.setText(item.getTitle());
		ivNavIcon.setImageResource(item.getIcon());
		int color = selected ? R.color.white : R.color.turquoise;
		ivNavIcon.setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_IN);
		if (item.getTitle() == R.string.nav_premium) {
			tvName.setTextColor(getContext().getResources().getColor(R.color.orange_light));
			ivNavIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.orange_light), PorterDuff.Mode.SRC_IN);
		}

	}

}
