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

import net.davidtanzer.jobjectformatter.typeinfo.PropertyInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo;

/**
 * With FormattedInclude, you can configure which types of fields (all, annotated or none) to include in the formatted output
 * using the {@link net.davidtanzer.jobjectformatter.annotations.Formatted} annotation.
 *
 * @see net.davidtanzer.jobjectformatter.annotations.Formatted
 */
public enum FormattedInclude {
	/**
	 * Include all fields in the formatted output.
	 */
	ALL_FIELDS {
		@Override
		public void addFieldValueTo(final GroupedValuesInfo.Builder builder, final PropertyInfo propertyInfo, final Object formattedFieldValue) {
			builder.addFieldValue(propertyInfo.getName(), formattedFieldValue, propertyInfo.getType());
		}
	},
	/**
	 * Include only fields in the formatted output where the field has the {@link net.davidtanzer.jobjectformatter.annotations.FormattedField}
	 * annotation and is configured to be included.
	 *
	 * @see net.davidtanzer.jobjectformatter.annotations.FormattedField
	 * @see net.davidtanzer.jobjectformatter.annotations.FormattedFieldType
	 */
	ANNOTATED_FIELDS {
		@Override
		public void addFieldValueTo(final GroupedValuesInfo.Builder builder, final PropertyInfo propertyInfo, final Object formattedFieldValue) {
			if (propertyInfo.getIncludeField().equals(FormattedFieldType.DEFAULT)) {
				builder.addFieldValue(propertyInfo.getName(), formattedFieldValue, propertyInfo.getType());
			}
		}
	},
	/**
	 * Do not include any fields in the formatted output.
	 */
	NO_FIELDS {
		@Override
		public void addFieldValueTo(final GroupedValuesInfo.Builder builder, final PropertyInfo propertyInfo, final Object formattedFieldValue) {
		}
	};

	/**
	 * Implements the behavior how the field value should be added based on the value of this enum.
	 *
	 * @param builder The {@link net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo.Builder} where the caller collects the values.
	 * @param propertyInfo The {@link PropertyInfo} of the current filed.
	 * @param formattedFieldValue The value of the field in the current object (already formatted).
	 */
	public abstract void addFieldValueTo(final GroupedValuesInfo.Builder builder, final PropertyInfo propertyInfo, final Object formattedFieldValue);
}
