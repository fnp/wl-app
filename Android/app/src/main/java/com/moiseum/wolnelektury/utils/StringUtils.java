package com.moiseum.wolnelektury.utils;

import com.moiseum.wolnelektury.connection.models.CategoryModel;

import java.util.List;

/**
 * Created by piotrostrowski on 19.11.2017.
 */

public class StringUtils {

	public static String joinCategory(List<CategoryModel> list, String delim) {
		StringBuilder sb = new StringBuilder();
		String loopDelimiter = "";

		for (CategoryModel s : list) {
			sb.append(loopDelimiter);
			sb.append(s.getName());
			loopDelimiter = delim;
		}

		return sb.toString();
	}

	public static String joinSlugs(List<CategoryModel> list, String delim) {
		if (list.size() == 0) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		String loopDelimiter = "";

		for (CategoryModel s : list) {
			sb.append(loopDelimiter);
			sb.append(s.getSlug());
			loopDelimiter = delim;
		}

		return sb.toString();
	}
}
