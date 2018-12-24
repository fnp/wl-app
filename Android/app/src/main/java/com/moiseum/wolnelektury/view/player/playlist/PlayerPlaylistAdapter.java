package com.moiseum.wolnelektury.view.player.playlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moiseum.wolnelektury.R;
import com.moiseum.wolnelektury.components.recycler.RecyclerAdapter;
import com.moiseum.wolnelektury.components.recycler.ViewHolder;
import com.moiseum.wolnelektury.connection.models.MediaModel;

import butterknife.BindView;

public class PlayerPlaylistAdapter extends RecyclerAdapter<MediaModel, PlayerPlaylistAdapter.PlayerViewHolder> {

	PlayerPlaylistAdapter(Context context) {
		super(context, Selection.SINGLE);
	}

	@Override
	protected String getItemId(MediaModel item) {
		return item.getUrl();
	}

	@NonNull
	@Override
	public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new PlayerViewHolder(inflate(R.layout.playlist_item, parent));
	}

	static class PlayerViewHolder extends ViewHolder<MediaModel> {

		@BindView(R.id.tvMediaName)
		TextView tvMediaName;
		@BindView(R.id.ibPlay)
		ImageButton ibPlay;

		PlayerViewHolder(View view) {
			super(view);
		}

		@Override
		public void bind(MediaModel item, boolean selected) {
			tvMediaName.setText(item.getName());
			ibPlay.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
		}
	}
}
