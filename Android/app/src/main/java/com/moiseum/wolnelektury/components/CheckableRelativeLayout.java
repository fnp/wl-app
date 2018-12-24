package com.moiseum.wolnelektury.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * Relative layout for handling checking/selecting action.
 */
public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

	public CheckableRelativeLayout(Context context) {
		super(context);
	}

	public CheckableRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void setChecked(boolean checked) {
		super.setSelected(checked);
	}

	@Override
	public boolean isChecked() {
		return super.isSelected();
	}

	@Override
	public void toggle() {
		super.setSelected(!isSelected());
	}
}

