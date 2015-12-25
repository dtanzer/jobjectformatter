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
package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.FormattedFieldType;
import net.davidtanzer.jobjectformatter.annotations.TransitiveInclude;

import java.lang.reflect.Field;

public class FieldInfo {
	private final Field field;
	private final TransitiveInclude transitiveIncludeOfTarget;
	private final FormattedFieldType includeFieldInTransitive;
	private final FormattedFieldType includeField;

	FieldInfo(final Field field, final TransitiveInclude transitiveIncludeOfTarget, final FormattedFieldType includeField, final FormattedFieldType includeFieldInTransitive) {
		this.field = field;
		this.transitiveIncludeOfTarget = transitiveIncludeOfTarget;
		this.includeFieldInTransitive = includeFieldInTransitive;
		this.includeField = includeField;
	}

	public String getName() {
		return field.getName();
	}

	public Class<?> getType() {
		return field.getType();
	}

	public TransitiveInclude getTransitiveIncludeOfTarget() {
		return transitiveIncludeOfTarget;
	}

	public FormattedFieldType getIncludeField() {
		return includeField;
	}

	public FormattedFieldType getIncludeFieldInTransitive() {
		return includeFieldInTransitive;
	}

	@Override
	public String toString() {
		return "FieldInfo{" +
				"field=" + field.getName() +
				", transitiveIncludeOfTarget=" + transitiveIncludeOfTarget +
				", includeFieldInTransitive=" + includeFieldInTransitive +
				", includeField=" + includeField +
				'}';
	}

	public Object getFieldValue(final Object object) {
		try {
			return field.get(object);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Could not determine field getValue", e);
		}
	}
}
