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

	@Override
	protected void appendSingleValue(final StringBuilder result, final ValueInfo value) {
		result.append("\"").append(value.getPropertyName()).append("\": ");
		if(!unescapedTypes.contains(value.getFieldClass())) {
			result.append("\"");
		}
		result.append(value.getValue());
		if(!unescapedTypes.contains(value.getFieldClass())) {
			result.append("\"");
		}
	}

	@Override
	protected void startFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		result.append("{");
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
