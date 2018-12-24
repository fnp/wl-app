package com.moiseum.wolnelektury.view.news.zoom;

import java.util.List; /**
 * Created by Piotr Ostrowski on 07.08.2018.
 */
public interface ZoomView {
	void initializeZoomView(List<String> photoUrls, int initialPosition);
}
