package com.moiseum.wolnelektury.connection.models;

import org.parceler.Parcel;

/**
 * Created by piotrostrowski on 17.11.2017.
 */

@Parcel(Parcel.Serialization.BEAN)
public class CategoryModel {

	private String url;
	private String href;
	private String name;
	private String slug;
	private boolean checked;

	public CategoryModel() {

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
