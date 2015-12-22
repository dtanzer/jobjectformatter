package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.Formatted;
import net.davidtanzer.jobjectformatter.annotations.FormattedField;
import net.davidtanzer.jobjectformatter.annotations.FormattedType;
import net.davidtanzer.jobjectformatter.annotations.Transitive;

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

		FormattedType formatted = FormattedType.ALL;
		if(type.isAnnotationPresent(Formatted.class)) {
			formatted = type.getAnnotation(Formatted.class).value();
		}

		for(Field field : type.getDeclaredFields()) {
			if(!field.getName().startsWith("this")) {
				field.setAccessible(true);

				final Transitive transitive = includeTransitivelyInFormattedText(field, typeInfoCache);
				if(formatted.equals(FormattedType.ALL)) {
					result.add(new FieldInfo(field, transitive));
				} else if(formatted.equals(FormattedType.ANNOTATED)) {
					if(field.isAnnotationPresent(FormattedField.class)) {
						result.add(new FieldInfo(field, transitive));
					}
				}
			}
		}

		return result;
	}

	private Transitive includeTransitivelyInFormattedText(final Field field, final TypeInfoCache typeInfoCache) {
		Transitive transitive;

		if(field.getType().getName().startsWith("java") || primitiveTypes.contains(field.getType())) {
			transitive = Transitive.ALLOWED;
		} else {
			transitive = typeInfoCache.transitiveBehaviorFor(field.getType());
		}

		if(field.isAnnotationPresent(Formatted.class)) {
			final Formatted[] annotations = field.getAnnotationsByType(Formatted.class);
			assert annotations.length == 1 : "Cannot really be any other value, since we have checked that the annotation is there. Or can it?";

			transitive = annotations[0].transitive();
		}
		return transitive;
	}

}
