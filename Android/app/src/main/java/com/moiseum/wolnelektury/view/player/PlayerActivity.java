package com.moiseum.wolnelektury.view.player;

import android.content.Context;
import android.os.Bundle;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractActivity;
import com.moiseum.wolnelektury.base.AbstractIntent;
import com.moiseum.wolnelektury.connection.models.BookDetailsModel;

import org.parceler.Parcels;

import static com.moiseum.wolnelektury.view.player.PlayerActivity.PlayerIntent.BOOK_KEY;
import static com.moiseum.wolnelektury.view.player.PlayerActivity.PlayerIntent.BOOK_SLUG_KEY;

/**
 * Created by Piotr Ostrowski on 22.05.2018.
 */
public class PlayerActivity extends AbstractActivity {

	private static final String PLAYER_FRAGMENT_TAG = "PlayerFragmentTag";

	public static class PlayerIntent extends AbstractIntent {

		static final String BOOK_KEY = "BookKey";
		static final String BOOK_SLUG_KEY = "BookSlugKey";

		public PlayerIntent(BookDetailsModel book, String slug, Context context) {
			super(context, PlayerActivity.class);
			putExtra(BOOK_KEY, Parcels.wrap(book));
			putExtra(BOOK_SLUG_KEY, slug);
		}
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_blank;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {
		PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag(PLAYER_FRAGMENT_TAG);
		if (playerFragment == null) {
			playerFragment = PlayerFragment.newInstance(Parcels.unwrap(getIntent().getParcelableExtra(BOOK_KEY)), getIntent().getStringExtra(BOOK_SLUG_KEY));
			getSupportFragmentManager().beginTransaction().add(R.id.flContainer, playerFragment, PLAYER_FRAGMENT_TAG).commit();
		}
	}
}
