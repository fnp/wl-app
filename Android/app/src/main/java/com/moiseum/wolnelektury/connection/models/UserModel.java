package com.moiseum.wolnelektury.connection.models;

/**
 * Created by Piotr Ostrowski on 21.06.2018.
 */
public class UserModel {
	private String username;
	private boolean premium;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}
}
