package com.moiseum.wolnelektury.connection.models;

import com.google.gson.annotations.SerializedName;

public class FavouriteStateModel {

	@SerializedName("likes")
	private boolean state;

	public FavouriteStateModel() {
		this.state = false;
	}

	public boolean getState() {
		return state;
	}
}
