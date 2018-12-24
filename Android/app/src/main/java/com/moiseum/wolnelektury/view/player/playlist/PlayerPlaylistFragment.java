package com.moiseum.wolnelektury.view.player.playlist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.base.mvp.PresenterFragment;
import com.moiseum.wolnelektury.components.ProgressRecyclerView;
import com.moiseum.wolnelektury.components.recycler.RecyclerAdapter;
import com.moiseum.wolnelektury.connection.models.MediaModel;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Piotr Ostrowski on 28.05.2018.
 */
public class PlayerPlaylistFragment extends PresenterFragment<PlayerPlaylistPresenter> implements PlayerPlaylistView {

	private static final String MEDIA_FILES_KEY = "MediaFilesKey";

	@BindView(R.id.rvPlayerPlaylist)
	ProgressRecyclerView<MediaModel> rvPlayerPlaylist;

	private PlayerPlaylistAdapter adapter;

	public static PlayerPlaylistFragment newInstance(List<MediaModel> mediaFiles) {
		PlayerPlaylistFragment fragment = new PlayerPlaylistFragment();
		Bundle args = new Bundle();
		args.putParcelable(MEDIA_FILES_KEY, Parcels.wrap(mediaFiles));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected PlayerPlaylistPresenter createPresenter() {
		if (getArguments() == null || getArguments().getParcelable(MEDIA_FILES_KEY) == null) {
			throw new IllegalStateException("Media files object is required at this point.");
		}
		return new PlayerPlaylistPresenter(Parcels.unwrap(getArguments().getParcelable(MEDIA_FILES_KEY)), this, getContext());
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_player_playlist;
	}

	@Override
	public void prepareView(View view, Bundle savedInstanceState) {
		initList(rvPlayerPlaylist);
	}

	@Override
	public void setCurrentPlaylistItem(int position) {
		adapter.selectItem(adapter.getItem(position));
	}

	public void initList(ProgressRecyclerView<MediaModel> rvList) {
		adapter = new PlayerPlaylistAdapter(getContext());
		adapter.setOnItemClickListener((item, view, position) -> getPresenter().onPlaylistItemClick(position));
		rvList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		rvList.setAdapter(adapter);
	}

	public void setPlaylist(List<MediaModel> item) {
		rvPlayerPlaylist.setItems(item);
	}
}
