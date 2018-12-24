package com.moiseum.wolnelektury.view.splash;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.AbstractActivity;
import com.moiseum.wolnelektury.view.main.MainActivity;

import butterknife.BindView;

/**
 * Created by piotrostrowski on 09.12.2017.
 */

public class SplashActivity extends AbstractActivity {

	@BindView(R.id.rlMainView)
	View rlMainView;

	private Handler launchHandler;
	private Runnable launchRunnable = new Runnable() {
		@Override
		public void run() {
			launchDashboard();
		}
	};

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_splash;
	}

	@Override
	public void prepareView(Bundle savedInstanceState) {
		rlMainView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchDashboard();
			}
		});
		launchHandler = new Handler();
		launchHandler.postDelayed(launchRunnable, 1500);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		launchHandler.removeCallbacks(launchRunnable);
	}

	private void launchDashboard() {
		MainActivity.MainIntent intent = new MainActivity.MainIntent(this);
		startActivity(intent);
		finish();
	}
}
