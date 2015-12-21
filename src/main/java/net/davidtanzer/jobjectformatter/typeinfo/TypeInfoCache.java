package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.AutomaticallyFormatted;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class TypeInfoCache {
	private final FieldsFilter fieldsFilter;
	private Map<Class<?>, TypeInfo> cachedTypeInfos = new ConcurrentHashMap<>();

	public TypeInfoCache() {
		this(new FieldsFilter());
	}

	public TypeInfoCache(final FieldsFilter fieldsFilter) {
		this.fieldsFilter = fieldsFilter;
	}

	public TypeInfo typeInfoFor(final Class<?> type) {
		return cachedTypeInfos.computeIfAbsent(type, this::createTypeInfoFrom);
	}

	private TypeInfo createTypeInfoFrom(final Class<?> type) {
		TypeInfo.Builder builder = new TypeInfo.Builder(this, fieldsFilter);

		Class<?> currentType = type;
		while(currentType != null && currentType != Object.class) {
			builder.addInfoForClass(currentType);
			currentType = currentType.getSuperclass();
		}

		if(hasAnnotatedToString(type)) {
			builder.typeHasAutomaticallyFormattedToString();
		}
		return builder.buildTypeInfo();
	}

	boolean hasAnnotatedToString(final Class<?> type) {
		try {
			Method toStringMethod = type.getMethod("toString");
			assert toStringMethod != null : "Cannot be null, since we would get a NoSuchMethodException when the method does not exist.";

			if(toStringMethod.isAnnotationPresent(AutomaticallyFormatted.class)) {
				return true;
			}
		} catch (NoSuchMethodException e) {
			ignoreException_BecauseNotAllTypesHaveToString_AndWeDontCare(e);
		}
		return false;
	}

	private void ignoreException_BecauseNotAllTypesHaveToString_AndWeDontCare(final NoSuchMethodException e) {
	}
}
