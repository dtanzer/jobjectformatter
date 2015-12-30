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

import java.util.List;

/**
 * Abstract base class for implementing your own {@link net.davidtanzer.jobjectformatter.formatter.ObjectStringFormatter}s.
 *
 * <strong>AbstractObjectStringFormatter</strong> functionality (extracted from the tests by agiledox):
 * <ul>
 *     <li>Calls <code>startFormattedString</code> at the beginning of processing.</li>
 *     <li>Passes the original values info object to <code>startFormattedString</code>.</li>
 *     <li>Calls <code>endFormattedString</code> after processing everything in <code>objectValuesInfo</code>.</li>
 *     <li>Calls <code>startValueGroup</code> for every value group in the object info when value grouping is enabled.</li>
 *     <li>Calls <code>endValueGroup</code> after every value group in the object when value grouping is enabled.</li>
 *     <li>Calls <code>appendSingleValue</code> for every value from the object values info.</li>
 *     <li>Adds to string value for simple property.</li>
 *     <li>Adds formatted property value for transitive property.</li>
 * </ul>
 *
 * @see net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo
 * @see net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo
 * @see net.davidtanzer.jobjectformatter.valuesinfo.ValueInfo
 */
public abstract class AbstractObjectStringFormatter implements ObjectStringFormatter {
	/**
	 * Processes all the information in the {@link net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo}
	 * and calls protected methods to do the actual formatting.
	 *
	 * This method does the heavy lifting of processing the ObjectValuesInfo in the correct order. You normally won't have
	 * to override this method, as it calls some protected methods in the correct order. You only have to override those
	 * protected methods.
	 *
	 * @param info The {@link net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo} that contains all relevant information about the object to format.
	 * @return The formatted representation of the object, as String.
	 */
	@Override
	public String format(final ObjectValuesInfo info) {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		startFormattedString(result, info);
		for(GroupedValuesInfo groupedValuesInfo : info.getValuesByClass()) {
			if(!first) {
				result.append(getGroupsSeparator());
			}

			startValueGroup(result, groupedValuesInfo);
			appendAllValues(result, groupedValuesInfo.getValues());
			endValueGroup(result);

			first = false;
		}
		endFormattedString(result, info);
		return result.toString();
	}

	private void appendAllValues(final StringBuilder result, final List<ValueInfo> values) {
		boolean first = true;
		for(ValueInfo value : values) {
			if(!first) {
				result.append(getValueSeparator());
			}
			if(value.getValue() instanceof ObjectValuesInfo) {
				appendSingleValue(result, new ValueInfo(value.getPropertyName(), format((ObjectValuesInfo) value.getValue()), value.getPropertyType()));
			} else {
				appendSingleValue(result, value);
			}
			first = false;
		}
	}

	/**
	 * The abstract formatter calls this method before it does any processing (Override to define the start of the formatted string).
	 *
	 * @param result The string builder where the formatter collects the formatted string.
	 * @param info All compiled information about the object to be formatted.
	 * @see net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo
	 */
	protected void startFormattedString(StringBuilder result, final ObjectValuesInfo info) {}

	/**
	 * The abstract formatter calls this method after it is finished with processing (Override to define the end of the formatted string).
	 *
	 * @param result The string builder where the formatter collects the formatted string.
	 * @param info All compiled information about the object to be formatted.
	 * @see net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo
	 */
	protected void endFormattedString(StringBuilder result, final ObjectValuesInfo info) {}

	/**
	 * The abstract formatter calls this method before processing any value group (When value grouping is enabled; Override to define the start of a value group in the formatted string).
	 *
	 * @param result The string builder where the formatter collects the formatted string.
	 * @param groupedValuesInfo Information about the value group the formatter is starting.
	 * @see net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo
	 */
	protected void startValueGroup(StringBuilder result, GroupedValuesInfo groupedValuesInfo) {}

	/**
	 * The abstract formatter calls this method after processing any value group (When value grouping is enabled; Override to define the start of a value group in the formatted string).
	 *
	 * @param result The string builder where the formatter collects the formatted string.
	 */
	protected void endValueGroup(StringBuilder result) {}

	/**
	 * The abstract formatter calls this method for every value that should be added to the result.
	 *
	 * In this method, you should add the value to the result (with the correct formatting). An implementation could be
	 * as simple as this:
	 *
	 * <pre>
{@literal @}Override
protected void appendSingleValue(final StringBuilder result, final ValueInfo value) {
    result.append(value.getPropertyName()).append("=").append(value.getValue());
}
	 * </pre>
	 * @param result The string builder where the formatter collects the formatted string.
	 * @param value Information about the value that should be added to the result.
	 */
	protected abstract void appendSingleValue(StringBuilder result, ValueInfo value);

	/**
	 * Get the separator that the formatter will print between two values.
	 *
	 * @return The string to separate two values
	 */
	private String getValueSeparator() {
		return ", ";
	}

	/**
	 * Get the separator that the formatter will print between two value groups.
	 *
	 * @return The string to separate two value groups
	 */
	protected String getGroupsSeparator() {
		return getValueSeparator();
	}
}
