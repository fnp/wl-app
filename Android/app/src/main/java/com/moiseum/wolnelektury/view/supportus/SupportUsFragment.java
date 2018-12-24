package com.moiseum.wolnelektury.view.supportus;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractFragment;

import butterknife.BindView;

/**
 * @author golonkos
 */

public class SupportUsFragment extends AbstractFragment {

	public static SupportUsFragment newInstance() {
		return new SupportUsFragment();
	}

	@BindView(R.id.tvSupportUsText)
	TextView tvSupportUsText;

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_support_us;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		tvSupportUsText.setText(Html.fromHtml(getString(R.string.support_us_text)));
		tvSupportUsText.setLinksClickable(true);
		tvSupportUsText.setMovementMethod(LinkMovementMethod.getInstance());
	}
}
