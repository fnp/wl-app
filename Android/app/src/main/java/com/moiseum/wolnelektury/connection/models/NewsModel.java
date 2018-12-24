package com.moiseum.wolnelektury.connection.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel(Parcel.Serialization.BEAN)
public class NewsModel {

	private String body;
	private String lead;
	private String title;
	private String url;
	@SerializedName("image_url")
	private String imageUrl;
	private String key;
	private String time;
	private String place;
	@SerializedName("image_thumb")
	private String thumbUrl;
	@SerializedName("gallery_urls")
	private List<String> galleryUrl;
	private String type;

	public NewsModel() {

	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getLead() {
		return lead;
	}

	public void setLead(String lead) {
		this.lead = lead;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public List<String> getGalleryUrl() {
		return galleryUrl;
	}

	public void setGalleryUrl(List<String> galleryUrl) {
		this.galleryUrl = galleryUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
