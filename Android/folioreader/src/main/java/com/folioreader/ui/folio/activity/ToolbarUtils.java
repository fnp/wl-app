package com.folioreader.ui.folio.activity;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.folioreader.Config;
import com.folioreader.R;
import com.folioreader.util.UiUtil;

/**
 * @author golonkos
 */

public final class ToolbarUtils {

	public static void updateToolbarColors(Context context, Toolbar toolbar, Config config, boolean nightMode) {
		if (nightMode) {
			setToolbarColors(context, toolbar, config.getThemeColor(), R.color.black);
		} else {
			setToolbarColors(context, toolbar, config.getIconColor(), config.getThemeColor());
		}
	}

	private static void setToolbarColors(Context context, Toolbar toolbar, @ColorRes int iconColor, @ColorRes int toolbarColor) {
		UiUtil.setColorToImage(context, iconColor, ((ImageView) toolbar.findViewById(R.id.btn_close)).getDrawable());
		UiUtil.setColorToImage(context, iconColor, ((ImageView) toolbar.findViewById(R.id.btn_drawer)).getDrawable());
		UiUtil.setColorToImage(context, iconColor, ((ImageView) toolbar.findViewById(R.id.btn_config)).getDrawable());
		UiUtil.setColorToImage(context, iconColor, ((ImageView) toolbar.findViewById(R.id.btn_speaker)).getDrawable());
		toolbar.setBackgroundColor(ContextCompat.getColor(context, toolbarColor));
		((TextView) toolbar.findViewById(R.id.lbl_center)).setTextColor(ContextCompat.getColor(context, iconColor));
	}
}
