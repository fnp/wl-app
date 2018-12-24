package com.moiseum.wolnelektury.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.objectbox.converter.PropertyConverter;

/**
 * Created by Piotr Ostrowski on 01.07.2018.
 */
public class StringListConverter implements PropertyConverter<List<String>, String> {

	@Override
	public List<String> convertToEntityProperty(String databaseValue) {
		if (databaseValue == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(databaseValue.split(","));
	}

	@Override
	public String convertToDatabaseValue(List<String> entityProperty) {
		if (entityProperty == null) {
			return "";
		}
		if (entityProperty.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (String property : entityProperty) {
			builder.append(property).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
}