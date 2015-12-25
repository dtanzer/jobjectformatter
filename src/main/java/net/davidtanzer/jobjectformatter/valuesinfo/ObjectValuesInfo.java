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
package net.davidtanzer.jobjectformatter.valuesinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectValuesInfo {
	private final List<GroupedValuesInfo> valuesByClass;
	private final List<ValueInfo> allValues;

	private ObjectValuesInfo(final List<GroupedValuesInfo> valuesByClass, final List<ValueInfo> allValues) {
		this.valuesByClass = valuesByClass;
		this.allValues = allValues;
	}

	public List<GroupedValuesInfo> getValuesByClass() {
		return valuesByClass;
	}

	public List<ValueInfo> getAllValues() {
		return allValues;
	}

	public static class Builder {
		private final List<GroupedValuesInfo> valuesByClass = new ArrayList<>();
		private final List<ValueInfo> allValues = new ArrayList<>();

		public ObjectValuesInfo buildToStringInfo() {
			return new ObjectValuesInfo(
					Collections.unmodifiableList(valuesByClass),
					Collections.unmodifiableList(allValues));
		}

		public Builder addClassValues(final GroupedValuesInfo groupedValuesInfo) {
			valuesByClass.add(groupedValuesInfo);
			allValues.addAll(groupedValuesInfo.getValues());
			return this;
		}
	}
}
