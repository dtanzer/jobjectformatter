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

/**
 * Contains all values of a given object that should be formatted.
 */
public class ObjectValuesInfo {
	private final List<GroupedValuesInfo> valuesByClass;
	private final List<ValueInfo> allValues;
	private final Class type;

	private ObjectValuesInfo(final List<GroupedValuesInfo> valuesByClass, final List<ValueInfo> allValues, final Class type) {
		this.valuesByClass = valuesByClass;
		this.allValues = allValues;
		this.type = type;
	}

	/**
	 * Get all values of the object's properties, grouped by their declaring class.
	 *
	 * @return all values of the object's properties, grouped by their declaring class.
	 */
	public List<GroupedValuesInfo> getValuesByClass() {
		return valuesByClass;
	}

	/**
	 * Get all values of the object's properties.
	 *
	 * @return all values of the object's properties.
	 */
	public List<ValueInfo> getAllValues() {
		return allValues;
	}

	/**
	 * Get the type of the object to format.
	 *
	 * @return the type of the object to format.
	 */
	public Class getType() {
		return type;
	}

	static class Builder {
		private final List<GroupedValuesInfo> valuesByClass = new ArrayList<>();
		private final List<ValueInfo> allValues = new ArrayList<>();
		private Class type;

		public ObjectValuesInfo buildToStringInfo() {
			return new ObjectValuesInfo(
					Collections.unmodifiableList(valuesByClass),
					Collections.unmodifiableList(allValues),
					type);
		}

		Builder addClassValues(final GroupedValuesInfo groupedValuesInfo) {
			valuesByClass.add(groupedValuesInfo);
			allValues.addAll(groupedValuesInfo.getValues());
			return this;
		}

		Builder setType(Class type) {
			this.type = type;
			return this;
		}
	}
}
