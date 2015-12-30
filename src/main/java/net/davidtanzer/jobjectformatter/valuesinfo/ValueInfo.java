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

/**
 * Information about a single property value.
 */
public class ValueInfo {
	private final String propertyName;
	private final Object value;
	private final Class<?> propertyType;

	public ValueInfo(final String propertyName, final Object value, final Class<?> propertyType) {
		this.propertyName = propertyName;
		this.value = value;
		this.propertyType = propertyType;
	}

	/**
	 * Get the name of the property.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Get the value of the property.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Get the type of the property.
	 */
	public Class<?> getPropertyType() {
		return propertyType;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ValueInfo valueInfo = (ValueInfo) o;

		if (propertyName != null ? !propertyName.equals(valueInfo.propertyName) : valueInfo.propertyName != null)
			return false;
		return value != null ? value.equals(valueInfo.value) : valueInfo.value == null;

	}

	@Override
	public int hashCode() {
		int result = propertyName != null ? propertyName.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ValueInfo{" +
				"getPropertyName='" + propertyName + '\'' +
				", getValue='" + value + '\'' +
				'}';
	}
}
