package com.moiseum.wolnelektury.view.news.zoom;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.moiseum.wolnelektury.R;

import java.lang.ref.WeakReference;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by Piotr Ostrowski on 07.08.2018.
 */
public class ZoomPhotosAdapter extends PagerAdapter {

	private List<String> photoUrls;
	private LayoutInflater inflater;
	private WeakReference<Context> mContext;

	ZoomPhotosAdapter(List<String> photoUrl, Context context) {
		inflater = LayoutInflater.from(context);
		mContext = new WeakReference<>(context);
		this.photoUrls = photoUrl;
		for (String photo : photoUrl) {
			Glide.with(context).load(photo).dontTransform().preload();
		}
	}

	@NonNull
	@Override
	public Object instantiateItem(@NonNull ViewGroup collection, int position) {
		final String photoUrl = photoUrls.get(position);
		View view = inflater.inflate(R.layout.zoom_item, collection, false);

		final ProgressBar pbLoading = view.findViewById(R.id.pbLoading);
		final TextView tvLoading = view.findViewById(R.id.tvLoading);
		final Button btnRetry = view.findViewById(R.id.btnRetry);
		final ImageViewTouch ivPhoto = view.findViewById(R.id.ivPointPhoto);

		btnRetry.setOnClickListener(v -> {
			pbLoading.setVisibility(View.VISIBLE);
			tvLoading.setVisibility(View.VISIBLE);
			btnRetry.setVisibility(View.GONE);
			fetchImageWithGlide(photoUrl, pbLoading, tvLoading, btnRetry, ivPhoto);
		});

		ivPhoto.setDisplayType(ImageViewTouchBase.DisplayType.FIT_HEIGHT);
		fetchImageWithGlide(photoUrl, pbLoading, tvLoading, btnRetry, ivPhoto);

		collection.addView(view);
		return view;
	}

	@Override
	public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
		collection.removeView((View) view);
	}

	@Override
	public int getCount() {
		return photoUrls.size();
	}

	@Override
	public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
		return view == object;
	}

	private void fetchImageWithGlide(String photoUrl, final ProgressBar pbLoading, final TextView tvLoading, final Button btnRetry, ImageViewTouch ivPhoto) {
		if (mContext.get() != null) {
			Glide.with(mContext.get()).load(photoUrl).dontTransform().listener(new RequestListener<String, GlideDrawable>() {
				@Override
				public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
					pbLoading.setVisibility(View.GONE);
					tvLoading.setVisibility(View.GONE);
					btnRetry.setVisibility(View.VISIBLE);
					return true;
				}

				@Override
				public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
					pbLoading.setVisibility(View.GONE);
					tvLoading.setVisibility(View.GONE);
					return false;
				}
			}).into(ivPhoto);
		}
	}
}
