package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.FormattedTransitively;

import java.lang.reflect.Field;
import java.util.*;

public class ClassInfo {
	private final Class clazz;
	private final List<FieldInfo> fieldInfos;

	public ClassInfo(final Class<?> type, final TypeInfoCache typeInfoCache, final FieldsFilter fieldsFilter) {
		this.clazz = type;

		List<FieldInfo> fieldInfos = fieldsFilter.getFilteredFields(type, typeInfoCache);
		this.fieldInfos = Collections.unmodifiableList(fieldInfos);
	}

	public Class getClazz() {
		return clazz;
	}

	public List<FieldInfo> fieldInfos() {
		return fieldInfos;
	}

	@Override
	public String toString() {
		return "ClassInfo{" +
				"clazz=" + clazz.getName() +
				", fieldInfos=" + fieldInfos +
				'}';
	}
}
