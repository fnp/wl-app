package com.moiseum.wolnelektury.view.news.single;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moiseum.wolnelektury.R;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Piotr Ostrowski on 06.08.2018.
 */
public class NewsGalleryAdapter extends PagerAdapter {

	private final LayoutInflater inflater;
	private final List<String> galleryUrls;

	private PublishSubject<Integer> pagerOnClickSubject = PublishSubject.create();
	private View.OnClickListener pageClickListener = v -> {
		int position = (int) v.getTag();
		pagerOnClickSubject.onNext(position);
	};

	NewsGalleryAdapter(List<String> galleryUrls, Context context) {
		this.galleryUrls = galleryUrls;
		this.inflater = LayoutInflater.from(context);
	}

	@NonNull
	@SuppressLint("InflateParams")
	@Override
	public Object instantiateItem(@NonNull ViewGroup container, int position) {
		View view = inflater.inflate(R.layout.fragment_single_news_gallery_item, null);
		view.setTag(position);
		view.setOnClickListener(pageClickListener);
		container.addView(view);

		ImageView ivGallery = view.findViewById(R.id.tvGalleryImage);
		Glide.with(view.getContext())
				.load(galleryUrls.get(position))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.dontTransform()
				.into(ivGallery);

		return view;
	}

	@Override
	public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
		View view = (View) object;
		container.removeView(view);
	}

	@Override
	public int getCount() {
		return galleryUrls.size();
	}

	@Override
	public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
		return view == object;
	}

	Observable<Integer> getPageClickObservable() {
		return pagerOnClickSubject.hide();
	}
}
