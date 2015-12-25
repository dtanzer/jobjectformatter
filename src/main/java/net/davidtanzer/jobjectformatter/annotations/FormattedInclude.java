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
