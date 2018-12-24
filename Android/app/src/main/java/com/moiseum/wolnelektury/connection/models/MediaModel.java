package com.moiseum.wolnelektury.connection.models;

import org.parceler.Parcel;

/**
 * Created by piotrostrowski on 17.11.2017.
 */

@Parcel(Parcel.Serialization.BEAN)
public class MediaModel {

	private String url;
	private String director;
	private String type;
	private String name;
	private String artist;

	public MediaModel() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}
}
