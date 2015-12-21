package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.AutomaticallyFormattedToString;

import java.lang.reflect.Method;
import java.util.HashMap;

public class TypeInfoCache {
	private HashMap<Class<?>, TypeInfo> cachedTypeInfos = new HashMap<>();

	public synchronized TypeInfo typeInfoFor(final Class<?> type) {
		final TypeInfo cachedTypeInfo = cachedTypeInfos.get(type);
		if(cachedTypeInfo != null) {
			return cachedTypeInfo;
		}

		TypeInfo typeInfo = createTypeInfoFrom(type);
		cachedTypeInfos.put(type, typeInfo);

		return typeInfo;
	}

	private TypeInfo createTypeInfoFrom(final Class<?> type) {
		TypeInfo.Builder builder = new TypeInfo.Builder(this);

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

			if(toStringMethod.isAnnotationPresent(AutomaticallyFormattedToString.class)) {
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
