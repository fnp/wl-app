package com.moiseum.wolnelektury.view.book;

/**
 * Created by Piotr Ostrowski on 23.08.2018.
 */
public enum BookType {
	TYPE_DEFAULT {
		@Override
		public boolean shouldShowPremiumLock() {
			return false;
		}
	},
	TYPE_PREMIUM {
		@Override
		public boolean shouldShowPremiumLock() {
			return true;
		}
	};

	public abstract boolean shouldShowPremiumLock();
}
