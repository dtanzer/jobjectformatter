package net.davidtanzer.jobjectformatter.annotations;

import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;

public enum TransitiveInclude {
	ANNOTADED_FIELDS {
		@Override
		public Object transitiveFieldValue(final Object fieldValue, final boolean hasFormattedAnnotation, final ObjectValuesInfo transitiveValues) {
			if(hasFormattedAnnotation) {
				if(transitiveValues.getAllValues().isEmpty()) {
					return "[not null]";
				} else {
					return transitiveValues;
				}
			} else {
				return fieldValue;
			}
		}
	},

	NO_FIELDS {
		@Override
		public Object transitiveFieldValue(final Object fieldValue, final boolean hasFormattedAnnotation, final ObjectValuesInfo transitiveValues) {
			if(fieldValue == null) {
				return null;
			} else {
				return "[not null]";
			}
		}
	},

	ALL_FIELDS {
		@Override
		public Object transitiveFieldValue(final Object fieldValue, final boolean hasFormattedAnnotation, final ObjectValuesInfo transitiveValues) {
			return fieldValue;
		}
	};

	public abstract Object transitiveFieldValue(final Object fieldValue, final boolean hasFormattedAnnotation, final ObjectValuesInfo transitiveValues);
}
