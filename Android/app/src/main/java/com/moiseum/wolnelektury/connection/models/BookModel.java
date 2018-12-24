package com.moiseum.wolnelektury.connection.models;

import com.google.gson.annotations.SerializedName;
import com.moiseum.wolnelektury.storage.StringListConverter;

import org.parceler.Parcel;

import java.util.List;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by piotrostrowski on 16.11.2017.
 */

@Parcel(Parcel.Serialization.BEAN)
@Entity
public class BookModel {

	@Id(assignable = true)
	private long localId;

	// API provided fields
	private String kind;
	private String author;
	private String url;
	@SerializedName("has_audio")
	private boolean hasAudio;
	private String title;
	private String cover;
	private String epoch;
	private String href;
	private String genre;
	private String slug;
	@SerializedName("cover_color")
	private String coverColor;
	private String key;
	@SerializedName("full_sort_key")
	private String sortedKey;
	@SerializedName("simple_thumb")
	private String coverThumb;
	private boolean liked;

	// Locally stored fields
	private String ebookName;
	private int currentChapter;
	private int totalChapters;
	private String ebookFileUrl;
	private int currentAudioChapter;
	private int totalAudioChapters;
	@Convert(converter = StringListConverter.class, dbType = String.class)
	private List<String> audioFileUrls;

	public BookModel() {
	}

	public long getLocalId() {
		return localId;
	}

	public void setLocalId(long localId) {
		this.localId = localId;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isHasAudio() {
		return hasAudio;
	}

	public void setHasAudio(boolean hasAudio) {
		this.hasAudio = hasAudio;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getEpoch() {
		return epoch;
	}

	public void setEpoch(String epoch) {
		this.epoch = epoch;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getSlug() {
		return slug;
	}

	public String getCoverColor(){return coverColor;}

	public void setCoverColor(String coverColor){this.coverColor=coverColor;}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSortedKey() {
		return sortedKey;
	}

	public void setSortedKey(String sortedKey) {
		this.sortedKey = sortedKey;
	}

	public String getCoverThumb() {
		return coverThumb;
	}

	public void setCoverThumb(String coverThumb) {
		this.coverThumb = coverThumb;
	}

	public String getEbookName() {
		return ebookName;
	}

	public void setEbookName(String ebookName) {
		this.ebookName = ebookName;
	}

	public int getCurrentChapter() {
		return currentChapter;
	}

	public void setCurrentChapter(int currentChapter) {
		this.currentChapter = currentChapter;
	}

	public int getTotalChapters() {
		return totalChapters;
	}

	public void setTotalChapters(int totalChapters) {
		this.totalChapters = totalChapters;
	}

	public String getEbookFileUrl() {
		return ebookFileUrl;
	}

	public void setEbookFileUrl(String ebookFileUrl) {
		this.ebookFileUrl = ebookFileUrl;
	}

	public int getCurrentAudioChapter() {
		return currentAudioChapter;
	}

	public void setCurrentAudioChapter(int currentAudioChapter) {
		this.currentAudioChapter = currentAudioChapter;
	}

	public int getTotalAudioChapters() {
		return totalAudioChapters;
	}

	public void setTotalAudioChapters(int totalAudioChapters) {
		this.totalAudioChapters = totalAudioChapters;
	}

	public List<String> getAudioFileUrls() {
		return audioFileUrls;
	}

	public void setAudioFileUrls(List<String> audioFileUrls) {
		this.audioFileUrls = audioFileUrls;
	}

	public boolean isEbookDownloaded() {
		return ebookFileUrl != null;
	}

	public boolean isAudioDownloaded() {
		return audioFileUrls != null && audioFileUrls.size() > 0;
	}

	public boolean isDeletable() {
		return ebookFileUrl != null || (audioFileUrls != null && audioFileUrls.size() > 0);
	}

	public boolean isLiked() { return liked; }

	public void setLiked(boolean liked) { this.liked = liked; }
}
