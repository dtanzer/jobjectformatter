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
package net.davidtanzer.jobjectformatter.formatter;

import net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ValueInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * A formatter that formates objects using a JSON-like syntax.
 *
 * Example output:
 * <pre>
{"Person": {"firstName": "Jane", "lastName": "Doe", "address": "[not null]"}}
 * </pre>
 *
 * <strong>JsonObjectStringFormatter</strong>
 * <ul>
 *     <li>Formats grouped by classes correctly.</li>
 * </ul>
 */
public class JsonObjectStringFormatter extends AbstractObjectStringFormatter {
	private static final Set<Class<?>> unescapedTypes = new HashSet<Class<?>>() {{
		add(Integer.class);
		add(Short.class);
		add(Float.class);
		add(Double.class);
		add(Boolean.class);

		add(int.class);
		add(short.class);
		add(float.class);
		add(double.class);
		add(boolean.class);
	}};
	private final DisplayClassName displayClassName;
	private final FormatGrouped formatGrouped;

	/**
	 * Creates a new JsonObjectStringFormatter with the default configuration.
	 *
	 * The default configuraiton is:
	 * <ul>
	 *     <li>Group objects by their declaring class.</li>
	 *     <li>Only display the class name when not grouping objects by their declaring class.</li>
	 * </ul>
	 *
	 * @see net.davidtanzer.jobjectformatter.formatter.FormatGrouped
	 * @see net.davidtanzer.jobjectformatter.formatter.DisplayClassName
	 */
	public JsonObjectStringFormatter() {
		this(FormatGrouped.BY_CLASS, DisplayClassName.WHEN_NOT_GROUPED_BY_CLASS);
	}

	/**
	 * Creates a new JsonObjectStringFormatter with a given grouping configuration, leaving {@link net.davidtanzer.jobjectformatter.formatter.DisplayClassName}
	 * at the default configuration.
	 *
	 * Refer to the documentation of the no-args constructor for the default configuration.
	 *
	 * @param formatGrouped The configuration for grouping the values.

	 * @see net.davidtanzer.jobjectformatter.formatter.FormatGrouped
	 * @see net.davidtanzer.jobjectformatter.formatter.DisplayClassName
	 * @see JsonObjectStringFormatter#JsonObjectStringFormatter()
	 */
	public JsonObjectStringFormatter(final FormatGrouped formatGrouped) {
		this(formatGrouped, DisplayClassName.WHEN_NOT_GROUPED_BY_CLASS);
	}


	/**
	 * Creates a new JsonObjectStringFormatter with a given grouping configuration and display-class-name configuration.
	 *
	 * @param formatGrouped The configuration for grouping the values.
	 * @param displayClassName The configuration for displaying the class name.
	 *
	 * @see net.davidtanzer.jobjectformatter.formatter.FormatGrouped
	 * @see net.davidtanzer.jobjectformatter.formatter.DisplayClassName
	 */
	public JsonObjectStringFormatter(final FormatGrouped formatGrouped, final DisplayClassName displayClassName) {
		super(formatGrouped);

		if(displayClassName == null) {
			throw new IllegalArgumentException("Parameter displayClassName must not be null.");
		}
		this.formatGrouped = formatGrouped;
		this.displayClassName = displayClassName;
	}

	@Override
	protected void appendSingleValue(final StringBuilder result, final ValueInfo value) {
		result.append("\"").append(value.getPropertyName()).append("\": ");
		if(!unescapedTypes.contains(value.getPropertyType())) {
			result.append("\"");
		}
		result.append(value.getValue());
		if(!unescapedTypes.contains(value.getPropertyType())) {
			result.append("\"");
		}
	}

	@Override
	protected void startFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		result.append("{");
		if(displayClassName == DisplayClassName.ALWAYS ||
				(displayClassName == DisplayClassName.WHEN_NOT_GROUPED_BY_CLASS) && formatGrouped != FormatGrouped.BY_CLASS) {
			result.append("\"class\": \"").append(info.getType().getSimpleName()).append("\"").append(getValueSeparator());
		}
	}

	@Override
	protected void endFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		result.append("}");
	}

	@Override
	protected void startValueGroup(final StringBuilder result, final GroupedValuesInfo groupedValuesInfo) {
		result.append("\"").append(groupedValuesInfo.getGroupName()).append("\": {");
	}

	@Override
	protected void endValueGroup(final StringBuilder result) {
		result.append("}");
	}
}
