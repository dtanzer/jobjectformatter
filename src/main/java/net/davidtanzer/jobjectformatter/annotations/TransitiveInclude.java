/*
   Copyright 2015 David Tanzer (business@davidtanzer.net / @dtanzer)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package net.davidtanzer.jobjectformatter.annotations;

import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;

/**
 * With TransitiveInclude, you can configure which types of fields (all, annotated or none) to include in the formatted output
 * using the {@link net.davidtanzer.jobjectformatter.annotations.Formatted} annotation.
 *
 * @see net.davidtanzer.jobjectformatter.annotations.Formatted
 */
public enum TransitiveInclude {
	/**
	 * Include all fields in the formatted output.
	 */
	ALL_FIELDS {
		@Override
		public Object transitiveFieldValue(final Object fieldValue, final boolean hasFormattedAnnotation, final ObjectValuesInfo transitiveValues) {
			return fieldValue;
		}
	},

	/**
	 * Include only fields in the formatted output where the field has the {@link net.davidtanzer.jobjectformatter.annotations.FormattedField}
	 * annotation and is configured to be included.
	 *
	 * @see net.davidtanzer.jobjectformatter.annotations.FormattedField
	 * @see net.davidtanzer.jobjectformatter.annotations.FormattedFieldType
	 */
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

	/**
	 * Do not include any fields in the formatted output.
	 */
	NO_FIELDS {
		@Override
		public Object transitiveFieldValue(final Object fieldValue, final boolean hasFormattedAnnotation, final ObjectValuesInfo transitiveValues) {
			if(fieldValue == null) {
				return null;
			} else {
				return "[not null]";
			}
		}
	};

	/**
	 * Get the transitive field value (already formatted) of the field based on the value of this enum.
	 *
	 * @param fieldValue The value of the current field.
	 * @param hasFormattedAnnotation true when the field or the referenced class has the formatted annotation.
	 * @param transitiveValues An {@link net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo} that contains all relevant values from the transitive object.
	 * @return The formatted field value that will be passed to the {@link net.davidtanzer.jobjectformatter.formatter.ObjectStringFormatter}
	 */
	public abstract Object transitiveFieldValue(final Object fieldValue, final boolean hasFormattedAnnotation, final ObjectValuesInfo transitiveValues);
}
