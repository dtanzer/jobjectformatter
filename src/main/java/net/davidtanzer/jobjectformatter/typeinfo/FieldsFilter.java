package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.*;

import java.lang.reflect.Field;
import java.util.*;

class FieldsFilter {
	private static final Set<Class<?>> primitiveTypes = new HashSet<Class<?>>() {{
		add(boolean.class);
		add(short.class);
		add(int.class);
		add(long.class);
		add(float.class);
		add(double.class);
	}};

	public List<FieldInfo> getFilteredFields(final Class<?> type, final TypeInfoCache typeInfoCache) {
		final List<FieldInfo> result = new ArrayList<>();

		for(Field field : type.getDeclaredFields()) {
			if(!field.getName().startsWith("this")) {
				field.setAccessible(true);

				final Transitive transitiveBehaviorOfTarget = includeTransitivelyInFormattedText(field, typeInfoCache);
				FormattedFieldType includeField = FormattedFieldType.NEVER;
				FormattedFieldType includeFieldInTransitive = FormattedFieldType.NEVER;
				if(field.isAnnotationPresent(FormattedField.class)) {
					includeField = field.getAnnotation(FormattedField.class).value();
					includeFieldInTransitive = field.getAnnotation(FormattedField.class).transitive();
				}
				result.add(new FieldInfo(field, transitiveBehaviorOfTarget, includeField, includeFieldInTransitive));
			}
		}

		return result;
	}

	private Transitive includeTransitivelyInFormattedText(final Field field, final TypeInfoCache typeInfoCache) {
		Transitive transitive;

		if(field.getType().getName().startsWith("java") || primitiveTypes.contains(field.getType())) {
			transitive = Transitive.ALWAYS;
		} else {
			transitive = typeInfoCache.transitiveBehaviorFor(field.getType());
		}

		if(field.isAnnotationPresent(Formatted.class)) {
			final Formatted[] annotations = field.getAnnotationsByType(Formatted.class);
			assert annotations.length == 1 : "Cannot really be any other getValue, since we have checked that the annotation is there. Or can it?";

			transitive = annotations[0].transitive();
		}
		return transitive;
	}

}
