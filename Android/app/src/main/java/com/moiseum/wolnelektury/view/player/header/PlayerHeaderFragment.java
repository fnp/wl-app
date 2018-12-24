package com.moiseum.wolnelektury.view.player.header;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.connection.RestClient;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.http.HEAD;

/**
 * Created by Piotr Ostrowski on 22.05.2018.
 */
public class PlayerHeaderFragment extends PresenterFragment<PlayerHeaderPresenter> implements PlayerHeaderView {

	private static final String AUTHOR_KEY = "AuthorKey";
	private static final String TITLE_KEY = "TitleKey";
	private static final String COVER_KEY = "CoverKey";

	public static PlayerHeaderFragment newInstance(String author, String title, String coverUrl) {
		PlayerHeaderFragment fragment = new PlayerHeaderFragment();
		Bundle args = new Bundle();
		args.putString(AUTHOR_KEY, author);
		args.putString(TITLE_KEY, title);
		args.putString(COVER_KEY, coverUrl);
		fragment.setArguments(args);
		return fragment;
	}


	@BindView(R.id.vCoverOverlay)
	View vCoverOverlay;
	@BindView(R.id.ivCoverBackground)
	ImageView ivCoverBackground;
	@BindView(R.id.ivCover)
	ImageView ivCover;
	@BindView(R.id.tvAuthor)
	TextView tvAuthor;
	@BindView(R.id.tvBookTitle)
	TextView tvBookTitle;

	@Override
	protected PlayerHeaderPresenter createPresenter() {
		if (getArguments() == null) {
			throw new IllegalStateException("Missing fragment arguments.");
		}
		String author = getArguments().getString(AUTHOR_KEY);
		String title = getArguments().getString(TITLE_KEY);
		String coverUrl = getArguments().getString(COVER_KEY);
		return new PlayerHeaderPresenter(author, title, coverUrl, this);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_player_header;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		initView();
	}

	public void initView() {
		if (getArguments() != null) {
			vCoverOverlay.setAlpha(0.7f);
			vCoverOverlay.setBackgroundColor(Color.parseColor("#db4b16"));
			tvAuthor.setText(getArguments().getString(AUTHOR_KEY));
			tvBookTitle.setText(getArguments().getString(TITLE_KEY));
			if (getArguments().getString(COVER_KEY) != null) {
				String coverUrl = getArguments().getString(COVER_KEY);
				if (coverUrl != null && !coverUrl.contains(RestClient.MEDIA_URL) && !coverUrl.contains(RestClient.MEDIA_URL_HTTPS)) {
					coverUrl = RestClient.MEDIA_URL_HTTPS + coverUrl;
				}
				Glide.with(getContext()).load(coverUrl).placeholder(R.drawable.list_nocover).diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().into(ivCover);
			}
			if (getArguments().getString(COVER_KEY) != null) {
				String coverBackgroundUrl = getArguments().getString(COVER_KEY);
				if (coverBackgroundUrl != null && !coverBackgroundUrl.contains(RestClient.MEDIA_URL) && !coverBackgroundUrl.contains(RestClient.MEDIA_URL_HTTPS)) {
					coverBackgroundUrl = RestClient.MEDIA_URL_HTTPS + coverBackgroundUrl;
				}
				Glide.with(getContext()).load(coverBackgroundUrl).diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().into(ivCoverBackground);
			}
		}
	}

	@OnClick(R.id.ibBack)
	public void onBackButtonClicked() {
		if (getActivity() != null) {
			getActivity().finish();
		}
	}
}

