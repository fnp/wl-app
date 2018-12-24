package com.moiseum.wolnelektury.view.book;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.folioreader.Config;
import com.folioreader.util.FolioReader;
import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.connection.models.BookDetailsModel;
import com.moiseum.wolnelektury.utils.StringUtils;
import com.moiseum.wolnelektury.view.book.components.ProgressDownloadButton;
import com.moiseum.wolnelektury.view.player.PlayerActivity;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.folioreader.ui.folio.activity.FolioActivity.PARAM_CHAPTERS_COUNT;
import static com.folioreader.ui.folio.activity.FolioActivity.PARAM_CURRENT_CHAPTER;
import static com.folioreader.ui.folio.activity.FolioActivity.PARAM_FILE_NAME;
import static com.moiseum.wolnelektury.view.book.BookActivity.BOOK_SLUG_KEY;
import static com.moiseum.wolnelektury.view.book.BookActivity.BOOK_TYPE_KEY;

/**
 * Created by Piotr Ostrowski on 17.11.2017.
 */

public class BookFragment extends PresenterFragment<BookPresenter> implements BookView {

	private static final int BOOK_READER_CODE = 32434;
	private static final String DEFAULT_OVERLAY_COLOR = "#80db4b16";

	public static BookFragment newInstance(String slug, BookType type) {
		BookFragment bookFragment = new BookFragment();
		Bundle args = new Bundle();
		args.putString(BOOK_SLUG_KEY, slug);
		args.putString(BOOK_TYPE_KEY, type.name());
		bookFragment.setArguments(args);
		return bookFragment;
	}

	@BindView(R.id.clMainView)
	View clMainView;
	@BindView(R.id.ctlCollapse)
	CollapsingToolbarLayout ctlCollapse;
	@BindView(R.id.ivCoverBackground)
	ImageView ivCoverBackground;
	@BindView(R.id.ivCover)
	ImageView ivCover;
	@BindView(R.id.vCoverOverlay)
	View vCoverOverlay;
	@BindView(R.id.tvBookAuthor)
	TextView tvBookAuthor;
	@BindView(R.id.tvBookTitle)
	TextView tvBookTitle;
	@BindView(R.id.btnEbook)
	ProgressDownloadButton btnEbook;
	@BindView(R.id.btnAudiobook)
	ProgressDownloadButton btnAudiobook;
	@BindView(R.id.tvBookKind)
	TextView tvBookKind;
	@BindView(R.id.tvBookGenre)
	TextView tvBookGenre;
	@BindView(R.id.tvBookEpoch)
	TextView tvBookEpoch;
	@BindView(R.id.tvQuotationText)
	HtmlTextView tvQuotationText;
	@BindView(R.id.tvQuotationAuthor)
	TextView tvQuotationAuthor;
	@BindView(R.id.ibDeleteEbook)
	ImageButton ibDeleteEbook;
	@BindView(R.id.ibDeleteAudiobook)
	ImageButton ibDeleteAudiobook;
	@BindView(R.id.pbHeaderLoading)
	ProgressBar pbHeaderLoading;
	@BindView(R.id.rlHeaderLoadingContainer)
	RelativeLayout rlHeaderLoadingContainer;
	@BindView(R.id.fabShare)
	FloatingActionButton fabShare;
	@BindView(R.id.fabFavourite)
	FloatingActionButton fabFavourite;
	@BindView(R.id.shimmerContentContainer)
	ShimmerFrameLayout shimmerContentContainer;
	@BindView(R.id.rlEbookButtonsContainer)
	RelativeLayout rlEbookButtonsContainer;
	@BindView(R.id.rlAudioButtonsContainer)
	RelativeLayout rlAudioButtonsContainer;
	@BindView(R.id.vSecondDivider)
	View vSecondDivider;
	@BindView(R.id.ibRetry)
	ImageButton ibRetry;
	@BindView(R.id.clPremium)
	View clPremium;

	@Override
	protected BookPresenter createPresenter() {
		if (getArguments() == null || getArguments().getString(BOOK_SLUG_KEY) == null || getArguments().getString(BOOK_TYPE_KEY) == null) {
			throw new IllegalStateException("Missing BookDetails data!");
		}
		BookType type = BookType.valueOf(getArguments().getString(BOOK_TYPE_KEY));
		return new BookPresenter(getArguments().getString(BOOK_SLUG_KEY), type, this);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_book;
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		shimmerContentContainer.startShimmerAnimation();
		Toolbar toolbar = view.findViewById(R.id.bookToolbar);
		setupToolbar(toolbar);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BOOK_READER_CODE && resultCode == RESULT_OK) {
			String bookName = data.getStringExtra(PARAM_FILE_NAME);
			int currentChapter = data.getIntExtra(PARAM_CURRENT_CHAPTER, 0);
			int count = data.getIntExtra(PARAM_CHAPTERS_COUNT, 0);
			getPresenter().onBackFromReader(bookName, currentChapter, count);
		}
	}

	@OnClick(R.id.btnEbook)
	public void onEbookClick() {
		getPresenter().launchEbookForState(btnEbook.getState());
	}

	@OnClick(R.id.btnAudiobook)
	public void onAudiobookClick() {
		getPresenter().launchAudiobookForState(btnAudiobook.getState());
	}

	@OnClick(R.id.ibDeleteEbook)
	public void onDeleteEbookClick() {
		getPresenter().deleteEbook();
		Toast.makeText(getContext() ,getString(R.string.book_deleted_message), Toast.LENGTH_SHORT).show();
	}

	@OnClick(R.id.ibDeleteAudiobook)
	public void onDeleteAudiobookClick() {
		getPresenter().deleteAudiobook();
		Toast.makeText(getContext() ,getString(R.string.book_deleted_message), Toast.LENGTH_SHORT).show();
	}

	@OnClick(R.id.ibRetry)
	public void onRetryClick() {
		ibRetry.setVisibility(View.GONE);
		pbHeaderLoading.setVisibility(View.VISIBLE);
		getPresenter().reloadBookDetails();
	}

	@OnClick(R.id.fabShare)
	public void onShareClick() {
		getPresenter().onShareEbookClicked();
	}

	@OnClick(R.id.fabFavourite)
	public void onFavouriteClick() {
		getPresenter().onFavouriteEbookClicked();
	}

	@OnClick(R.id.bSupportUs)
	public void onSupportUsClick() {
		showPayPalForm();
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------
	// BookView
	// ------------------------------------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeBookView(BookDetailsModel book) {
		shimmerContentContainer.stopShimmerAnimation();
		fabShare.setVisibility(View.VISIBLE);
		getPresenter().showFavouriteButton(book);
		rlHeaderLoadingContainer.setVisibility(View.GONE);
		rlEbookButtonsContainer.setVisibility(View.VISIBLE);

		if (book.hasAudio()) {
			rlAudioButtonsContainer.setVisibility(View.VISIBLE);
		}
		ctlCollapse.setTitle(book.getTitle());
		ctlCollapse.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

		Glide.with(getContext()).load(book.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().into(ivCoverBackground);
		Glide.with(getContext()).load(book.getCover()).placeholder(R.drawable.list_nocover).diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().into(ivCover);

		vCoverOverlay.setAlpha(0.7f);
		String colorHash = book.getCoverColor() != null ? book.getCoverColor() : DEFAULT_OVERLAY_COLOR;
		vCoverOverlay.setBackgroundColor(Color.parseColor(colorHash));

		if (book.getAuthors() != null && book.getAuthors().size() > 0) {
			tvBookAuthor.setText(StringUtils.joinCategory(book.getAuthors(), ", "));
		}
		tvBookTitle.setText(book.getTitle());

		if (book.getKinds() != null && book.getKinds().size() > 0) {
			tvBookKind.setText(StringUtils.joinCategory(book.getKinds(), ", "));
		}
		tvBookKind.setBackgroundColor(Color.TRANSPARENT);
		if (book.getGenres() != null && book.getGenres().size() > 0) {
			tvBookGenre.setText(StringUtils.joinCategory(book.getGenres(), ", "));
		}
		tvBookGenre.setBackgroundColor(Color.TRANSPARENT);
		if (book.getEpochs() != null && book.getEpochs().size() > 0) {
			tvBookEpoch.setText(StringUtils.joinCategory(book.getEpochs(), ", "));
		}
		tvBookEpoch.setBackgroundColor(Color.TRANSPARENT);

		if (book.getFragment() != null) {
			tvQuotationText.setMinLines(0);
			tvQuotationText.setHtml(book.getFragment().getHtml());
			tvQuotationText.setBackgroundColor(Color.TRANSPARENT);
			tvQuotationAuthor.setText(book.getFragment().getTitle());
			tvQuotationAuthor.setBackgroundColor(Color.TRANSPARENT);
		} else {
			tvQuotationText.setVisibility(View.GONE);
			tvQuotationAuthor.setVisibility(View.GONE);
			vSecondDivider.setVisibility(View.GONE);
		}
		enableToolbarCollapse();
	}

	private void enableToolbarCollapse() {
		ViewTreeObserver viewTreeObserver = shimmerContentContainer.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					shimmerContentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					if (ctlCollapse.getHeight() + shimmerContentContainer.getHeight() > clMainView.getHeight()) {
						AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) ctlCollapse.getLayoutParams();
						params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED); // list other flags here by |
						ctlCollapse.setLayoutParams(params);
					}
				}
			});
		}
	}

	@Override
	public void changeDownloadButtonState(ProgressDownloadButton.ProgressDownloadButtonState state, boolean forAudiobook) {
		if (forAudiobook) {
			ibDeleteAudiobook.setVisibility(state.isDeletable() ? View.VISIBLE : View.INVISIBLE);
			btnAudiobook.setState(state);
		} else {
			ibDeleteEbook.setVisibility(state.isDeletable() ? View.VISIBLE : View.INVISIBLE);
			btnEbook.setState(state);
		}
	}

	@Override
	public void showCurrentStateProgress(int percentage, boolean forAudiobook) {
		if (forAudiobook) {
			btnAudiobook.setProgress(percentage);
		} else {
			btnEbook.setProgress(percentage);
		}
	}

	@Override
	public void showInitializationError() {
		Toast.makeText(getContext(), R.string.book_loading_error, Toast.LENGTH_LONG).show();
		pbHeaderLoading.setVisibility(View.GONE);
		ibRetry.setVisibility(View.VISIBLE);
	}

	@Override
	public void showDownloadFileError() {
		Toast.makeText(getContext(), R.string.book_download_error, Toast.LENGTH_LONG).show();
	}

	@Override
	public void startShareActivity(String shareUrl) {
		showShareActivity(shareUrl);
	}

	@Override
	public void openBook(String downloadedBookUrl) {
		FolioReader reader = new FolioReader(getContext());
		Config config = new Config.ConfigBuilder().build();
		Intent bookIntent = reader.createBookIntent(downloadedBookUrl, config);
		startActivityForResult(bookIntent, BOOK_READER_CODE);
	}

	@Override
	public void launchPlayer(BookDetailsModel book) {
		if (getArguments() != null) {
			PlayerActivity.PlayerIntent intent = new PlayerActivity.PlayerIntent(book, getArguments().getString(BOOK_SLUG_KEY), getContext());
			startActivity(intent);
		}
	}

	@Override
	public void updateReadingProgress(int currentChapter, int count, boolean forAudiobook) {
		if (forAudiobook) {
			btnAudiobook.setCurrentReadCount(currentChapter + 1, count);
		} else {
			btnEbook.setCurrentReadCount(currentChapter, count);
		}
	}

	@Override
	public void startLikeClicked() {
		fabFavourite.setImageResource(R.drawable.ic_fav_active);
	}

	@Override
	public void stopLikeClicked() {
		fabFavourite.setImageResource(R.drawable.ic_fav);
	}

	public void showFavouriteButton(BookDetailsModel book) {
		fabFavourite.setVisibility(View.VISIBLE);
		if(book.getFavouriteState()) {
			fabFavourite.setImageResource(R.drawable.ic_fav_active);
		}
	}

	@Override
	public void showPremiumLock(boolean lock) {
		if (!lock) {
			clPremium.setVisibility(View.GONE);
		} else {
			clPremium.setVisibility(View.VISIBLE);
			btnEbook.setClickable(false);
			btnAudiobook.setClickable(false);
			fabFavourite.setClickable(false);
			fabShare.setClickable(false);
		}
	}
}
