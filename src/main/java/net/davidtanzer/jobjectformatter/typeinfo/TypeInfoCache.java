package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.Formatted;
import net.davidtanzer.jobjectformatter.annotations.FormattedInclude;
import net.davidtanzer.jobjectformatter.annotations.TransitiveInclude;

import java.lang.reflect.Method;
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

	public TypeInfo typeInfoFor(final Class<?> type, final TransitiveInclude transitiveIncludeBehavior) {
		return typeInfoFor(type).withTransitiveBehavior(transitiveIncludeBehavior);
	}

	private TypeInfo createTypeInfoFrom(final Class<?> type) {
		TypeInfo.Builder builder = new TypeInfo.Builder(this, fieldsFilter);

		Class<?> currentType = type;
		while(currentType != null && currentType != Object.class) {
			builder.addInfoForClass(currentType);
			currentType = currentType.getSuperclass();
		}

		builder.withFormattingBehavior(formattingBehaviorFor(type), transitiveIncludeFor(type));
		return builder.buildTypeInfo();
	}

	private FormattedInclude formattingBehaviorFor(final Class<?> type) {
		return behaviorFor(type, f -> f.value(), FormattedInclude.ALL_FIELDS);
	}

	TransitiveInclude transitiveIncludeFor(final Class<?> type) {
		return behaviorFor(type, f -> f.transitive(), TransitiveInclude.NO_FIELDS);
	}

	public <T> T behaviorFor(final Class<?> type, final Function<Formatted, T> mappingFunction, final T defaultValue) {
		assert type != null : "Parameter \"type\" must not be null.";
		assert mappingFunction != null : "Parameter \"mappingFunction\" must not be null.";
		assert defaultValue != null : "Parameter \"defaultValue\" must not be null.";

		try {
			if(type.isAnnotationPresent(Formatted.class)) {
				return mappingFunction.apply(type.getAnnotation(Formatted.class));
			}

			Method toStringMethod = type.getMethod("toString");
			assert toStringMethod != null : "Cannot be null, since we would get a NoSuchMethodException when the method does not exist.";

			if(toStringMethod.isAnnotationPresent(Formatted.class)) {
				return mappingFunction.apply(toStringMethod.getAnnotation(Formatted.class));
			}
		} catch (NoSuchMethodException e) {
			ignoreException_BecauseNotAllTypesHaveToString_AndWeDontCare(e);
		}
		return defaultValue;
	}

	private void ignoreException_BecauseNotAllTypesHaveToString_AndWeDontCare(final NoSuchMethodException e) {
	}
}
