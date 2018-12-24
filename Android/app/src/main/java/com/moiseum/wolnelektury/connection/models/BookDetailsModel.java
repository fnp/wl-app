package com.moiseum.wolnelektury.connection.models;

import com.google.gson.annotations.SerializedName;
import com.moiseum.wolnelektury.utils.StringUtils;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrostrowski on 17.11.2017.
 */

@Parcel(Parcel.Serialization.BEAN)
public class BookDetailsModel {

	private static final String HREF_BASE = "http://wolnelektury.pl/api/books/";
	private static final String MEDIA_TYPE_MP3 = "mp3";

	private List<CategoryModel> genres;
	private List<CategoryModel> kinds;
	private BookModel parent;
	private String title;
	private String url;
	private List<MediaModel> media;
	@SerializedName("simple_cover")
	private String cover;
	private List<CategoryModel> epochs;
	private List<CategoryModel> authors;
	private String pdf;
	private String epub;
	@SerializedName("simple_thumb")
	private String coverThumb;
	@SerializedName("fragment_data")
	private FragmentModel fragment;
	@SerializedName("audio_length")
	private String audioLength;
	private ReadingStateModel.ReadingState state;
	private Boolean favouriteState;
	@SerializedName("cover_color")
	private String coverColor;
	private String slug;

	public BookDetailsModel() {
	}

	public List<CategoryModel> getGenres() {
		return genres;
	}

	public void setGenres(List<CategoryModel> genres) {
		this.genres = genres;
	}

	public List<CategoryModel> getKinds() {
		return kinds;
	}

	public void setKinds(List<CategoryModel> kinds) {
		this.kinds = kinds;
	}

	public BookModel getParent() {
		return parent;
	}

	public void setParent(BookModel parent) {
		this.parent = parent;
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

	public List<MediaModel> getMedia() {
		return media;
	}

	public void setMedia(List<MediaModel> media) {
		this.media = media;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public List<CategoryModel> getEpochs() {
		return epochs;
	}

	public void setEpochs(List<CategoryModel> epochs) {
		this.epochs = epochs;
	}

	public List<CategoryModel> getAuthors() {
		return authors;
	}

	public void setAuthors(List<CategoryModel> authors) {
		this.authors = authors;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

	public String getEpub() {
		return epub;
	}

	public void setEpub(String epub) {
		this.epub = epub;
	}

	public String getCoverThumb() {
		return coverThumb;
	}

	public void setCoverThumb(String coverThumb) {
		this.coverThumb = coverThumb;
	}

	public String getCoverColor() {
		return coverColor;
	}

	public void setCoverColor(String coverColor) {
		this.coverColor = coverColor;
	}

	public FragmentModel getFragment() {
		return fragment;
	}

	public void setFragment(FragmentModel fragment) {
		this.fragment = fragment;
	}

	public String getAudioLength() {
		return audioLength;
	}

	public void setAudioLength(String audioLength) {
		this.audioLength = audioLength;
	}

	public boolean hasAudio() {
		return media != null && media.size() > 0;
	}

	public ReadingStateModel.ReadingState getState() {
		return state;
	}

	public void setState(ReadingStateModel.ReadingState state) {
		this.state = state;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public BookModel getStorageModel(String slug) {
		BookModel model = new BookModel();
		model.setAuthor(StringUtils.joinCategory(getAuthors(), ", "));
		model.setCover(getCover());
		model.setCoverColor(getCoverColor());
		model.setCoverThumb(getCoverThumb());
		model.setEpoch(StringUtils.joinCategory(getEpochs(), ", "));
		model.setGenre(StringUtils.joinCategory(getGenres(), ", "));
		model.setKind(StringUtils.joinCategory(getKinds(), ", "));
		model.setHref(HREF_BASE + slug + "/");
		model.setTitle(getTitle());
		model.setSlug(slug);
		model.setUrl(getUrl());
		model.setHasAudio(hasAudio());
		return model;
	}

	public ArrayList<MediaModel> getAudiobookMediaModels() {
		ArrayList<MediaModel> mediaModels = new ArrayList<>();
		for (MediaModel mediaFile : getMedia()) {
			if (MEDIA_TYPE_MP3.equals(mediaFile.getType())) {
				mediaModels.add(mediaFile);
			}
		}
		return mediaModels;
	}

	public ArrayList<String> getAudiobookFilesUrls() {
		ArrayList<String> urls = new ArrayList<>();
		for (MediaModel mediaFile : getMedia()) {
			if (MEDIA_TYPE_MP3.equals(mediaFile.getType())) {
				urls.add(mediaFile.getUrl());
			}
		}
		return urls;
	}

	public boolean getFavouriteState() {
		return favouriteState;
	}

	public void setFavouriteState(boolean favouriteState) {
		this.favouriteState = favouriteState;
	}

	public String getFavouriteString(boolean favouriteState) {
		return favouriteState ? "like" : "unlike";
	}
}

