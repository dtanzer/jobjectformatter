package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.FormattedTransitively;

import java.lang.reflect.Field;

public class FieldInfo {
	private final Field field;
	private final FormattedTransitively.TransitiveInclude transitive;

	public FieldInfo(final Field field, final FormattedTransitively.TransitiveInclude transitive) {
		this.field = field;
		this.transitive = transitive;
	}

	public String getName() {
		return field.getName();
	}

	public Class<?> getType() {
		return field.getType();
	}

	public FormattedTransitively.TransitiveInclude getTransitive() {
		return transitive;
	}

	@Override
	public String toString() {
		return "FieldInfo{" +
				"field=" + field.getName() +
				'}';
	}

	public Object getFieldValue(final Object object) {
		try {
			return field.get(object);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Could not determine field value", e);
		}
	}
}
