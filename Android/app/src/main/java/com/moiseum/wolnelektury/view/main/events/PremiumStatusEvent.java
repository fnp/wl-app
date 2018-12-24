package com.moiseum.wolnelektury.view.main.events;

/**
 * Created by Piotr Ostrowski on 26.08.2018.
 */
public class PremiumStatusEvent {
	private boolean premium;

	public PremiumStatusEvent(boolean premium) {
		this.premium = premium;
	}

	public boolean isPremium() {
		return premium;
	}
}
