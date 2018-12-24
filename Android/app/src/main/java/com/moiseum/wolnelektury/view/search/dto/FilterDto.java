package com.moiseum.wolnelektury.view.search.dto;

import com.moiseum.wolnelektury.connection.models.CategoryModel;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrostrowski on 26.11.2017.
 */

@Parcel(Parcel.Serialization.BEAN)
public class FilterDto {

	private boolean isLecture;
	private boolean audiobook;
	private List<CategoryModel> filteredEpochs;
	private List<CategoryModel> filteredGenres;
	private List<CategoryModel> filteredKinds;

	public FilterDto() {
		this.filteredEpochs = new ArrayList<>();
		this.filteredGenres = new ArrayList<>();
		this.filteredKinds = new ArrayList<>();
	}

	public boolean isLecture() {
		return isLecture;
	}

	public void setLecture(boolean lecture) {
		isLecture = lecture;
	}

	public boolean isAudiobook() {
		return audiobook;
	}

	public void setAudiobook(boolean audiobook) {
		this.audiobook = audiobook;
	}

	public List<CategoryModel> getFilteredEpochs() {
		return filteredEpochs;
	}

	public void setFilteredEpochs(List<CategoryModel> filteredEpochs) {
		this.filteredEpochs = filteredEpochs;
	}

	public List<CategoryModel> getFilteredGenres() {
		return filteredGenres;
	}

	public void setFilteredGenres(List<CategoryModel> filteredGenres) {
		this.filteredGenres = filteredGenres;
	}

	public List<CategoryModel> getFilteredKinds() {
		return filteredKinds;
	}

	public void setFilteredKinds(List<CategoryModel> filteredKinds) {
		this.filteredKinds = filteredKinds;
	}
}
