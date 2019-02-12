package com.moiseum.wolnelektury.view.book.components;

/**
 * Created by Piotr Ostrowski on 21.11.2017.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.moiseum.wolnelektury.R;

import java.util.Locale;

public class ProgressDownloadButton extends View {

	private static final int MAX_PROGRESS_VALUE = 100;

	public enum ProgressDownloadButtonState {
		STATE_INITIAL {
			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public boolean isDownloaded() {
				return false;
			}

			@Override
			public boolean isDeletable() {
				return false;
			}

			@Override
			public void draw(Canvas canvas, ProgressDownloadButtonAttributes attributes, int width, int height) {
				int posY = getPosY(attributes, height);
				canvas.drawText(attributes.initialText, attributes.paddingStart, posY, attributes.textPaint);
				int top = (height - attributes.iconBitmap.getHeight()) / 2;
				canvas.drawBitmap(attributes.iconBitmap, width - attributes.iconBitmap.getWidth() - attributes.paddingEnd, top, attributes
						.bitmapPaint);
			}
		}, STATE_DOWNLOADING {
			@Override
			public boolean isEnabled() {
				return false;
			}

			@Override
			public boolean isDownloaded() {
				throw new IllegalStateException("This method shall not be called within this state");
			}

			@Override
			public boolean isDeletable() {
				return false;
			}

			@Override
			public void draw(Canvas canvas, ProgressDownloadButtonAttributes attributes, int width, int height) {
				int posY = getPosY(attributes, height);
				String text = String.format(Locale.getDefault(), "%s: %d%%", attributes.loadingText, attributes.currentProgress);

				// Draw text to overlap.
				canvas.drawText(text, attributes.paddingStart, posY, attributes.textPaint);

				// Draw icon to overlap.
				int top = (height - attributes.iconBitmap.getHeight()) / 2;
				canvas.drawBitmap(attributes.iconBitmap, width - attributes.iconBitmap.getWidth() - attributes.paddingEnd, top, attributes
						.bitmapPaint);

				// Draw progress
				int currentProgress = attributes.currentProgress;
				if (currentProgress >= 0 && currentProgress <= MAX_PROGRESS_VALUE) {
					attributes.baseRect.right = attributes.baseRect.width() * currentProgress / MAX_PROGRESS_VALUE;
					canvas.clipRect(attributes.baseRect);
				}

				// Draw current state.
				canvas.drawRoundRect(attributes.outerRectF, attributes.cornerRadius, attributes.cornerRadius, attributes.textPaint);
				canvas.drawText(text, attributes.paddingStart, posY, attributes.invertedPaint);
				canvas.drawBitmap(attributes.iconBitmap, width - attributes.iconBitmap.getWidth() - attributes.paddingEnd, top, attributes
						.bitmapInvertedPaint);
			}
		}, STATE_READING {
			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public boolean isDownloaded() {
				return true;
			}

			@Override
			public boolean isDeletable() {
				return true;
			}

			@Override
			public void draw(Canvas canvas, ProgressDownloadButtonAttributes attributes, int width, int height) {
				int posY = getPosY(attributes, height);
				int top = (height - attributes.iconBitmap.getHeight()) / 2;
				String text = String.format(Locale.getDefault(), "%1$s: %2$d/%3$d", attributes.downloadedText, attributes
						.currentReadPosition, attributes.totalReadCount);

				canvas.drawRoundRect(attributes.outerRectF, attributes.cornerRadius, attributes.cornerRadius, attributes.textPaint);
				canvas.drawText(text, attributes.paddingStart, posY, attributes.invertedPaint);
				canvas.drawBitmap(attributes.iconBitmap, width - attributes.iconBitmap.getWidth() - attributes.paddingEnd, top, attributes
						.bitmapInvertedPaint);
			}
		};

		private static int getPosY(ProgressDownloadButtonAttributes attributes, int height) {
			return (int) ((height / 2) - ((attributes.textPaint.descent() + attributes.textPaint.ascent()) / 2));
		}

		public abstract boolean isEnabled();

		public abstract boolean isDownloaded();

		public abstract boolean isDeletable();

		public abstract void draw(Canvas canvas, ProgressDownloadButtonAttributes attributes, int width, int height);
	}

	private static class ProgressDownloadButtonAttributes {
		private String initialText = "";
		private String downloadedText = "";
		private String loadingText;

		private Rect baseRect = new Rect();
		private RectF outerRectF = new RectF();
		private RectF innerRectF = new RectF();

		private int currentProgress = 0;
		private int currentReadPosition;
		private int totalReadCount;

		private Paint textPaint;
		private Paint invertedPaint;
		private Paint bitmapPaint;
		private Paint bitmapInvertedPaint;

		private Bitmap iconBitmap;
		private int cornerRadius;
		private int innerCornerRadius;
		private int borderSize;
		private int paddingStart;
		private int paddingEnd;
	}


	private ProgressDownloadButtonState currentState = ProgressDownloadButtonState.STATE_INITIAL;
	private ProgressDownloadButtonAttributes attributes;

	public ProgressDownloadButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initComponents(context, attrs, defStyle, 0);
	}

	public ProgressDownloadButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initComponents(context, attrs, 0, 0);
	}

	public ProgressDownloadButton(Context context) {
		super(context);
	}

	//	public InvertedTextProgressbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
	//		super(context, attrs, defStyleAttr, defStyleRes);
	//		initComponents(context, attrs, defStyleAttr, defStyleRes);
	//	}

	/**
	 * Initializes the text paint. This has a fix size.
	 *
	 * @param attrs The XML attributes to use.
	 */
	@SuppressLint("ResourceType")
	private void initComponents(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		attributes = new ProgressDownloadButtonAttributes();

		TypedArray baseAttributes = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.paddingStart, android.R.attr
				.paddingEnd});
		TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ProgressDownloadButton, defStyleAttr, defStyleRes);

		attributes.paddingStart = baseAttributes.getDimensionPixelOffset(0, 0);
		attributes.paddingEnd = baseAttributes.getDimensionPixelOffset(1, 0);

		Paint textPaint = new Paint();
		textPaint.setColor(styledAttributes.getColor(R.styleable.ProgressDownloadButton_text_color, Color.BLACK));
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setTextSize(styledAttributes.getDimensionPixelSize(R.styleable.ProgressDownloadButton_text_size, context.getResources()
				.getDimensionPixelSize(R.dimen.download_button_text_size_default)));
		textPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
		textPaint.setTextAlign(Paint.Align.LEFT); // Text draw is started in the middle
		textPaint.setLinearText(true);
		textPaint.setAntiAlias(true);

		// Define the inverted text paint.
		Paint invertedPaint = new Paint(textPaint);
		invertedPaint.setColor(styledAttributes.getColor(R.styleable.ProgressDownloadButton_text_inverted_color, Color.WHITE));
		attributes.textPaint = textPaint;
		attributes.invertedPaint = invertedPaint;

		// Define paint for drawable
		Paint bitmapPaint = new Paint();
		bitmapPaint.setColorFilter(new PorterDuffColorFilter(styledAttributes.getColor(R.styleable.ProgressDownloadButton_text_color,
				Color.BLACK), PorterDuff.Mode.SRC_ATOP));
		attributes.bitmapPaint = bitmapPaint;

		// Define paint for inverted drawable
		Paint bitmapInvertedPaint = new Paint();
		bitmapInvertedPaint.setColorFilter(new PorterDuffColorFilter(styledAttributes.getColor(R.styleable
				.ProgressDownloadButton_text_inverted_color, Color.WHITE), PorterDuff.Mode.SRC_ATOP));
		attributes.bitmapInvertedPaint = bitmapInvertedPaint;

		// Define the text.
		String initialText = styledAttributes.getString(R.styleable.ProgressDownloadButton_text_initial);
		if (initialText != null) {
			initialText = initialText.toUpperCase();
			attributes.initialText = initialText;
		}
		String downloadedText = styledAttributes.getString(R.styleable.ProgressDownloadButton_text_downloaded);
		if (downloadedText != null) {
			downloadedText = downloadedText.toUpperCase();
			attributes.downloadedText = downloadedText;
		}
		attributes.loadingText = context.getString(R.string.download_ebook_loading);

		// Load drawable
		attributes.iconBitmap = BitmapFactory.decodeResource(getResources(), styledAttributes.getResourceId(R.styleable
				.ProgressDownloadButton_drawable, android.R.drawable.ic_delete));

		attributes.borderSize = styledAttributes.getDimensionPixelSize(R.styleable.ProgressDownloadButton_border_size, context
				.getResources().getDimensionPixelSize(R.dimen.download_button_border_size_default));
		attributes.cornerRadius = styledAttributes.getDimensionPixelSize(R.styleable.ProgressDownloadButton_corner_radius, context
				.getResources().getDimensionPixelSize(R.dimen.download_button_corner_radius_default));
		attributes.innerCornerRadius = attributes.cornerRadius - attributes.borderSize;

		// Recycle the TypedArray.
		baseAttributes.recycle();
		styledAttributes.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.getClipBounds(attributes.baseRect);
		RectF outerRectF = attributes.outerRectF;
		int borderSize = attributes.borderSize;
		outerRectF.set(attributes.baseRect);
		attributes.innerRectF.set(outerRectF.left + borderSize, outerRectF.top + borderSize, outerRectF.right - borderSize, outerRectF
				.bottom - borderSize);

		// Draw outline
		canvas.drawRoundRect(outerRectF, attributes.cornerRadius, attributes.cornerRadius, attributes.textPaint);
		canvas.drawRoundRect(attributes.innerRectF, attributes.innerCornerRadius, attributes.innerCornerRadius, attributes.invertedPaint);

		// Draw current state
		getState().draw(canvas, attributes, getWidth(), getHeight());
	}

	public void setState(ProgressDownloadButtonState state) {
		if (state == ProgressDownloadButtonState.STATE_INITIAL) {
			attributes.currentReadPosition = 0;
			attributes.totalReadCount = 0;
		}
		this.currentState = state;
		this.setEnabled(state.isEnabled());
		invalidate();
	}

	public ProgressDownloadButtonState getState() {
		return currentState;
	}

	/**
	 * Sets the text that will overlay.
	 *
	 * @param text The text to draw.
	 */
	public void setText(String text) {
		attributes.initialText = text;
	}

	/**
	 * Gets the current text to draw.
	 *
	 * @return The current text to draw.
	 */
	public String getText() {
		return attributes.initialText;
	}

	public int getCurrentProgress() {
		return attributes.currentProgress;
	}

	public void setProgress(int progress) {
		attributes.currentProgress = progress;
		invalidate();
	}

	public void setCurrentReadCount(int position, int count) {
		attributes.currentReadPosition = position;
		attributes.totalReadCount = count;
		invalidate();
	}
}