package com.moiseum.wolnelektury.view.player;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.utils.StringUtils;
import com.moiseum.wolnelektury.view.player.header.PlayerHeaderFragment;
import com.moiseum.wolnelektury.view.player.playlist.PlayerPlaylistFragment;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.OnClick;

import static com.moiseum.wolnelektury.view.player.PlayerActivity.PlayerIntent.BOOK_KEY;
import static com.moiseum.wolnelektury.view.player.PlayerActivity.PlayerIntent.BOOK_SLUG_KEY;


/**
 * Created by Piotr Ostrowski on 22.05.2018.
 */
public class PlayerFragment extends PresenterFragment<PlayerPresenter> implements PlayerView {

	private static final String TAG = PlayerFragment.class.getSimpleName();
	private AlertDialog errorDialog;

	public static PlayerFragment newInstance(BookDetailsModel book, String slug) {
		PlayerFragment playerFragment = new PlayerFragment();
		Bundle args = new Bundle();
		args.putParcelable(BOOK_KEY, Parcels.wrap(book));
		args.putString(BOOK_SLUG_KEY, slug);
		playerFragment.setArguments(args);
		return playerFragment;
	}

	private static final String HEADER_FRAGMENT_TAG = "HeaderFragmentTag";
	private static final String LIST_FRAGMENT_TAG = "ListFragmentTag";

	int userSelectedPosition = 0;
	private boolean mUserIsSeeking = false;

	private PlayerHeaderFragment headerFragment;
	private PlayerPlaylistFragment playlistFragment;

	private SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			tvCurrentProgress.setText(getPresenter().getCurrentTimerText(progress));
			if (fromUser) {
				userSelectedPosition = progress;
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mUserIsSeeking = true;
			getPresenter().playOrPause(true);
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mUserIsSeeking = false;
			getPresenter().seekTo(userSelectedPosition);
			getPresenter().playOrPause(false);
		}
	};

	@BindView(R.id.sbPlayerProgress)
	SeekBar sbPlayerProgress;
	@BindView(R.id.tvCurrentProgress)
	TextView tvCurrentProgress;
	@BindView(R.id.tvTotalProgress)
	TextView tvTotalProgress;
	@BindView(R.id.tvChapterTitle)
	TextView tvChapterTitle;
	@BindView(R.id.tvArtist)
	TextView tvArtist;
	@BindView(R.id.ibToggleList)
	ImageButton ibToggleList;
	@BindView(R.id.ibPlayPause)
	ImageButton ibPlayPause;
	@BindView(R.id.ibPrevious)
	ImageButton ibPrevious;
	@BindView(R.id.ibNext)
	ImageButton ibNext;

	@Override
	protected PlayerPresenter createPresenter() {
		if (getArguments() == null || getArguments().getParcelable(BOOK_KEY) == null) {
			throw new IllegalStateException("Book object is required at this point.");
		}
		return new PlayerPresenter(
				Parcels.unwrap(getArguments().getParcelable(BOOK_KEY)),
				getArguments().getString(BOOK_SLUG_KEY),
				this,
				getContext()
		);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_player;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		if (getArguments() == null || getArguments().getParcelable(BOOK_KEY) == null) {
			throw new IllegalStateException("Book object is required at this point.");
		}
		BookDetailsModel book = Parcels.unwrap(getArguments().getParcelable(BOOK_KEY));
		initializeHeaderAndPlaylistFragments(book);
		sbPlayerProgress.setOnSeekBarChangeListener(listener);

		int visibility = book.getAudiobookMediaModels().size() > 1 ? View.VISIBLE : View.GONE;
		tvArtist.setVisibility(visibility);
		ibToggleList.setVisibility(visibility);
		ibPrevious.setVisibility(visibility);
		ibNext.setVisibility(visibility);
	}

	@OnClick(R.id.ibToggleList)
	public void onToggleListClicked() {
		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager == null) {
			return;
		}

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if (playlistFragment.isHidden()) {
			fragmentTransaction.show(playlistFragment);
			fragmentTransaction.hide(headerFragment);
		} else {
			fragmentTransaction.hide(playlistFragment);
			fragmentTransaction.show(headerFragment);
		}
		fragmentTransaction.commit();
	}

	@OnClick(R.id.ibPrevious)
	public void onPreviousClicked() {
		getPresenter().changeChapter(false);
	}

	@OnClick(R.id.ibNext)
	public void onNextClicked() {
		getPresenter().changeChapter(true);
	}

	@OnClick(R.id.ibRewind)
	public void onRewindClicked() {
		getPresenter().seekToButton(false);
	}

	@OnClick(R.id.ibPlayPause)
	public void onPauseClicked() {
		getPresenter().playOrPause(false);
	}

	@OnClick(R.id.ibFastForward)
	public void onFastForwardClicked() {
		getPresenter().seekToButton(true);
	}

	@Override
	public void setTrackDuration(int trackDuration, String totalProgress) {
		sbPlayerProgress.setMax(trackDuration);
		tvTotalProgress.setText(totalProgress);
	}

	@Override
	public void setTrackPosition(int position, String currentProgress) {
		if (!mUserIsSeeking) {
			sbPlayerProgress.setProgress(position);
			tvCurrentProgress.setText(currentProgress);
		}
	}

	@Override
	public void setTrackTexts(String title, int chapter) {
		tvArtist.setText(getString(R.string.player_chapter_number, (chapter + 1)));
		tvChapterTitle.setText(title);
	}

	@Override
	public void setPlayButtonState(boolean playing) {
		if (getContext() != null) {
			Drawable drawable = ContextCompat.getDrawable(getContext(), playing ? R.drawable.pause_selector : R.drawable.play_selector);
			ibPlayPause.setImageDrawable(drawable);
		}
	}

	@Override
	public void onPlayerError() {
		if (getActivity() != null && errorDialog == null) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
			dialogBuilder.setMessage(getString(R.string.load_player_failed));
			dialogBuilder.setCancelable(false);
			dialogBuilder.setPositiveButton(getString(R.string.close), (dialog, id) -> {
				getActivity().finish();
				dialog.dismiss();
			});
			dialogBuilder.setOnDismissListener(dialog -> errorDialog = null);
			errorDialog = dialogBuilder.create();
			errorDialog.show();
		}
	}

	private void initializeHeaderAndPlaylistFragments(BookDetailsModel book) {
		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager == null) {
			return;
		}

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		headerFragment = (PlayerHeaderFragment) fragmentManager.findFragmentByTag(HEADER_FRAGMENT_TAG);
		if (headerFragment == null) {
			headerFragment = PlayerHeaderFragment.newInstance(StringUtils.joinCategory(book.getAuthors(), ", "), book.getTitle(), book.getCoverThumb());
			fragmentTransaction.add(R.id.flPlayerFragmentContainer, headerFragment, HEADER_FRAGMENT_TAG);
		}
		playlistFragment = (PlayerPlaylistFragment) fragmentManager.findFragmentByTag(LIST_FRAGMENT_TAG);
		if (playlistFragment == null) {
			playlistFragment = PlayerPlaylistFragment.newInstance(book.getAudiobookMediaModels());
			fragmentTransaction.add(R.id.flPlayerFragmentContainer, playlistFragment, LIST_FRAGMENT_TAG);
		}
		fragmentTransaction.hide(playlistFragment);
		fragmentTransaction.commit();
	}
}



