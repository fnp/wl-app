package com.moiseum.wolnelektury.connection.models;

import org.parceler.Parcel;

/**
 * Created by piotrostrowski on 30.11.2017.
 */
@Parcel(Parcel.Serialization.BEAN)
public class FragmentModel {

	private String html;
	private String title;

	public FragmentModel() {
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
