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
package net.davidtanzer.jobjectformatter;

import net.davidtanzer.jobjectformatter.formatter.ObjectStringFormatter;
import net.davidtanzer.jobjectformatter.formatter.SimpleFormatter;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfoCache;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesCompiler;

/**
 * Generates a formatted string from an object (together with it's metadata).
 *
 * <strong>FormattedStringGenerator</strong> functionality (extracted from the tests by agiledox):
 * <ul>
 *     <li>Retrieves type information from type info cache when generating to string.</li>
 *     <li>Uses info compiler to compile values from object and type info.</li>
 *     <li>Uses to string formatter to format the to string info.</li>
 * </ul>
 */
public class FormattedStringGenerator {
	private final TypeInfoCache typeInfoCache;
	private final ObjectValuesCompiler objectValuesCompiler;
	private final ObjectStringFormatter toStringFormatter;

	/**
	 * Creates a new FormattedStringGenerator that uses a {@link net.davidtanzer.jobjectformatter.formatter.SimpleFormatter} to format the string.
	 *
	 * @see net.davidtanzer.jobjectformatter.formatter.SimpleFormatter
	 */
	public FormattedStringGenerator() {
		this(new SimpleFormatter(), new TypeInfoCache(), new ObjectValuesCompiler());
	}

	/**
	 * Creates a new FormattedStringGenerator with a given {@link net.davidtanzer.jobjectformatter.formatter.ObjectStringFormatter}.
	 *
	 * @param toStringFormatter The formatter to use when creating formatted strings.
	 * @see net.davidtanzer.jobjectformatter.formatter.ObjectStringFormatter
	 */
	public FormattedStringGenerator(final ObjectStringFormatter toStringFormatter) {
		this(toStringFormatter, new TypeInfoCache(), new ObjectValuesCompiler());
	}

	FormattedStringGenerator(final ObjectStringFormatter objectStringFormatter, final TypeInfoCache typeInfoCache, final ObjectValuesCompiler objectValuesCompiler) {
		if(objectStringFormatter == null) {
			throw new IllegalArgumentException("Parameter objectStringFormatter must not be null!");
		}
		assert typeInfoCache != null : "Parameter typeInfoCache must not be null!";
		assert objectValuesCompiler != null : "Parameter objectValuesCompiler must not be null!";

		this.toStringFormatter = objectStringFormatter;
		this.typeInfoCache = typeInfoCache;
		this.objectValuesCompiler = objectValuesCompiler;
	}

	/**
	 * Creates a formatted String representation of an object.
	 * 
	 * @param object The object to format.
	 * @return The formatted String representation.
	 */
	public String format(final Object object) {
		if(object==null) {
			throw new IllegalArgumentException("Parameter object must not be null!");
		}

		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(object.getClass());
		final ObjectValuesInfo objectValuesInfo = objectValuesCompiler.compileToStringInfo(typeInfo, object);

		return toStringFormatter.format(objectValuesInfo);
	}
}
