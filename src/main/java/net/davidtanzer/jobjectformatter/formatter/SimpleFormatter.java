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

import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ValueInfo;

/**
 * A formatter that is not configurable and has a very simple output - Used as default formatter when no other formatter is configured.
 *
 * Example output:
 * <pre>
{ firstName=Jane, lastName=Doe, address=[not null] }
 * </pre>
 */
public class SimpleFormatter extends AbstractObjectStringFormatter {
	@Override
	protected void startFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		result.append("{ ");
	}

	@Override
	protected void endFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		result.append(" }");
	}

	@Override
	protected void appendSingleValue(final StringBuilder result, final ValueInfo value) {
		result.append(value.getPropertyName()).append("=").append(value.getValue());
	}
}
