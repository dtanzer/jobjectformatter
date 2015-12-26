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
 * Annotation that indicates how a class or a transitive object should be formatted.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
public @interface Formatted {
	/**
	 * Determines how objects of this class or transitive object should be formatted when passed directly to "format".
	 *
	 * @return The {@link net.davidtanzer.jobjectformatter.annotations.FormattedInclude} value for the class or transitive object.
	 */
	FormattedInclude value() default FormattedInclude.ALL_FIELDS;

	/**
	 * Determines how objects of this class or transitive object should be formatted when the object is formatted transitively from another object.
	 *
	 * @return The @see "TransitiveInclude" value for the class or transitive object.
	 */
	TransitiveInclude transitive() default TransitiveInclude.ANNOTADED_FIELDS;
}
