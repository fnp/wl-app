package com.moiseum.wolnelektury.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractFragment;

import butterknife.BindView;

/**
 * @author golonkos.
 */

public class WebViewFragment extends AbstractFragment {

	private static final String PARAM_URL = "PARAM_URL";

	@BindView(R.id.wvAbout)
	WebView wvPage;
	@BindView(R.id.btnBack)
	Button btnBack;
	@BindView(R.id.btnRefresh)
	Button btnRefresh;
	@BindView(R.id.btnNext)
	Button btnNext;
	@BindView(R.id.tvPageError)
	TextView tvPageError;

	private boolean loadFailed;

	public static Fragment newInstance(String url) {
		WebViewFragment fragment = new WebViewFragment();
		Bundle args = new Bundle(1);
		args.putString(PARAM_URL, url);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_web_view;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		initWebView();
	}

	private void initWebView() {
		wvPage.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		wvPage.getSettings().setJavaScriptEnabled(true);
		wvPage.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		wvPage.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				wvPage.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				if (tvPageError == null) {
					return;
				}
				loadFailed = true;
				tvPageError.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (tvPageError == null) {
					return;
				}
				if (!loadFailed) {
					tvPageError.setVisibility(View.GONE);
				}
				btnBack.setEnabled(wvPage.canGoBack());
				btnNext.setEnabled(wvPage.canGoForward());
			}
		});
		String url = getArguments().getString(PARAM_URL);
		wvPage.loadUrl(url);

		btnRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadFailed = false;
				wvPage.reload();
			}
		});

		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				wvPage.goBack();
			}
		});

		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				wvPage.goForward();
			}
		});
	}
}
