package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.FormattedTransitively;

import java.lang.reflect.Field;
import java.util.*;

public class ClassInfo {
	private static final Set<Class<?>> primitiveTypes = new HashSet<Class<?>>() {{
		add(boolean.class);
		add(short.class);
		add(int.class);
		add(long.class);
		add(float.class);
		add(double.class);
	}};
	private final Class clazz;
	private final List<FieldInfo> fieldInfos;

	public ClassInfo(final Class<?> type, final TypeInfoCache typeInfoCache, final FieldsFilter fieldsFilter) {
		this.clazz = type;

		List<FieldInfo> fieldInfos = new ArrayList<>();
		for(Field field : fieldsFilter.getFilteredFields(type)) {
			field.setAccessible(true);

			final FormattedTransitively.TransitiveInclude transitive = includeTransitivelyInFormattedText(field, typeInfoCache);
			fieldInfos.add(new FieldInfo(field, transitive));
		}
		this.fieldInfos = Collections.unmodifiableList(fieldInfos);
	}

	protected FormattedTransitively.TransitiveInclude includeTransitivelyInFormattedText(final Field field, final TypeInfoCache typeInfoCache) {
		FormattedTransitively.TransitiveInclude transitive = FormattedTransitively.TransitiveInclude.DISALLOWED;
		if(field.getType().getName().startsWith("java") || primitiveTypes.contains(field.getType())) {
			transitive = FormattedTransitively.TransitiveInclude.ALLOWED;
		} else {
			if (typeInfoCache.hasAnnotatedToString(field.getType())) {
				transitive = FormattedTransitively.TransitiveInclude.ALLOWED;
			}
		}

		if(field.isAnnotationPresent(FormattedTransitively.class)) {
			final FormattedTransitively[] annotations = field.getAnnotationsByType(FormattedTransitively.class);
			assert annotations.length == 1 : "Cannot really be any other value, since we have checked that the annotation is there. Or can it?";

			transitive = annotations[0].value();
		}
		return transitive;
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
