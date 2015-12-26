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
 *
 * <strong>AbstractObjectStringFormatter</strong>
 * <ul>
 *     <li>Adds property value for simple property.</li>
 *     <li>Adds property value for transitive property.</li>
 * </ul>
 */
public abstract class AbstractObjectStringFormatter implements ObjectStringFormatter {
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
				appendSingleValue(result, new ValueInfo(value.getPropertyName(), format((ObjectValuesInfo) value.getValue()), value.getFieldClass()));
			} else {
				appendSingleValue(result, value);
			}
			first = false;
		}
	}

	protected void startFormattedString(StringBuilder result, final ObjectValuesInfo info) {}

	protected void endFormattedString(StringBuilder result, final ObjectValuesInfo info) {}

	protected void startValueGroup(StringBuilder result, GroupedValuesInfo groupedValuesInfo) {}

	protected void endValueGroup(StringBuilder result) {}

	private String getValueSeparator() {
		return ", ";
	}

	protected String getGroupsSeparator() {
		return getValueSeparator();
	}

	protected abstract void appendSingleValue(StringBuilder result, ValueInfo value);
}
