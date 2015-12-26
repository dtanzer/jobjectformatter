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

import net.davidtanzer.jobjectformatter.annotations.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 *
 * <strong>FieldsFilter</strong>
 * <ul>
 *     <li>Always returns a non null collection.</li>
 *     <li>Includes all fields if no annotations are present.</li>
 *     <li>Includes no fields_ when formatted annotated annotation is on class_ and no fields are annotated.</li>
 *     <li>Includes annotated fields_ when formatted annotated annotation is on class.</li>
 *     <li>Fields annotated with formatted_ have transitive behavior from annotation.</li>
 *     <li>Fields not annotated_ have transitive behavior determined by type info cache.</li>
 *     <li>Transitive annotation on fields_ is more important than behavior specified by type info cache.</li>
 * </ul>
 */
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

				final TransitiveInclude transitiveIncludeOfTarget = includeTransitivelyInFormattedText(field, typeInfoCache);
				FormattedFieldType includeField = FormattedFieldType.NEVER;
				FormattedFieldType includeFieldInTransitive = FormattedFieldType.NEVER;
				if(field.isAnnotationPresent(FormattedField.class)) {
					includeField = field.getAnnotation(FormattedField.class).value();
					includeFieldInTransitive = field.getAnnotation(FormattedField.class).transitive();
				}
				result.add(new FieldInfo(field, transitiveIncludeOfTarget, includeField, includeFieldInTransitive));
			}
		}

		return result;
	}

	private TransitiveInclude includeTransitivelyInFormattedText(final Field field, final TypeInfoCache typeInfoCache) {
		TransitiveInclude transitiveInclude;

		if(field.getType().getName().startsWith("java") || primitiveTypes.contains(field.getType())) {
			transitiveInclude = TransitiveInclude.ALL_FIELDS;
		} else {
			transitiveInclude = typeInfoCache.transitiveIncludeFor(field.getType());
		}

		if(field.isAnnotationPresent(Formatted.class)) {
			final Formatted[] annotations = field.getAnnotationsByType(Formatted.class);
			assert annotations.length == 1 : "Cannot really be any other getValue, since we have checked that the annotation is there. Or can it?";

			transitiveInclude = annotations[0].transitive();
		}
		return transitiveInclude;
	}

}
