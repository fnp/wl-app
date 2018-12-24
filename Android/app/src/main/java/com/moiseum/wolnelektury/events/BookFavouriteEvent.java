package com.moiseum.wolnelektury.events;

public class BookFavouriteEvent {
	private boolean state;

	public BookFavouriteEvent(boolean state) {
		this.state = state;
	}

	public boolean getState() {
		return state;
	}
}
