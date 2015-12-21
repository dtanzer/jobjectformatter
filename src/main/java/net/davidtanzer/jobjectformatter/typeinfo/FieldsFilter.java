package net.davidtanzer.jobjectformatter.typeinfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FieldsFilter {
	public List<Field> getFilteredFields(final Class<?> type) {
		final List<Field> result = new ArrayList<>();

		for(Field f : type.getDeclaredFields()) {
			if(!f.getName().startsWith("this")) {
				result.add(f);
			}
		}

		return result;
	}
}
