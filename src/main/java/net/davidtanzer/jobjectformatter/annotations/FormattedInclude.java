package net.davidtanzer.jobjectformatter.annotations;

import net.davidtanzer.jobjectformatter.typeinfo.FieldInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo;

public enum FormattedInclude {
	ALL_FIELDS {
		@Override
		public void addFieldValueTo(final GroupedValuesInfo.Builder builder, final FieldInfo fieldInfo, final Object formattedFieldValue) {
			builder.addFieldValue(fieldInfo.getName(), formattedFieldValue, fieldInfo.getType());
		}
	},
	ANNOTATED_FIELDS {
		@Override
		public void addFieldValueTo(final GroupedValuesInfo.Builder builder, final FieldInfo fieldInfo, final Object formattedFieldValue) {
			if (fieldInfo.getIncludeField().equals(FormattedFieldType.DEFAULT)) {
				builder.addFieldValue(fieldInfo.getName(), formattedFieldValue, fieldInfo.getType());
			}
		}
	},
	NO_FIELDS {
		@Override
		public void addFieldValueTo(final GroupedValuesInfo.Builder builder, final FieldInfo fieldInfo, final Object formattedFieldValue) {
		}
	};

	public abstract void addFieldValueTo(final GroupedValuesInfo.Builder builder, final FieldInfo fieldInfo, final Object formattedFieldValue);
}
