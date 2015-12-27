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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that indicates if a field should be included in the formatted string (when formatting the object directly or transitively via another object).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormattedField {
	/**
	 * Determines whether this field should be included in the formatted string when formatting the object directly.
	 *
	 * @return The {@link net.davidtanzer.jobjectformatter.annotations.FormattedFieldType} for formatting this field directly.
	 * @see net.davidtanzer.jobjectformatter.annotations.FormattedFieldType
	 */
	FormattedFieldType value() default FormattedFieldType.DEFAULT;

	/**
	 * Determines whether this field should be included in the formatted string when formatting the object transitively via another object.
	 *
	 * @return The {@link net.davidtanzer.jobjectformatter.annotations.FormattedFieldType} for formatting this field transitively.
	 * @see net.davidtanzer.jobjectformatter.annotations.FormattedFieldType
	 */
	FormattedFieldType transitive() default FormattedFieldType.NEVER;
}
