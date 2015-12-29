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
package net.davidtanzer.jobjectformatter.typeinfo;

import java.util.*;

/**
 * Type information about one of the super classes of an object.
 */
public class ClassInfo {
	private final Class clazz;
	private final List<PropertyInfo> propertyInfos;

	ClassInfo(final Class<?> type, final TypeInfoCache typeInfoCache, final FieldsFilter fieldsFilter) {
		this.clazz = type;

		List<PropertyInfo> propertyInfos = fieldsFilter.getFilteredFields(type, typeInfoCache);
		this.propertyInfos = Collections.unmodifiableList(propertyInfos);
	}

	/**
	 * Get the class about which this objects stores type information.
	 */
	public Class getClazz() {
		return clazz;
	}

	/**
	 * Get information about all the relevant fields of the class returned from {@link net.davidtanzer.jobjectformatter.typeinfo.ClassInfo#getClazz}.
	 * @see PropertyInfo
	 */
	public List<PropertyInfo> fieldInfos() {
		return propertyInfos;
	}

	@Override
	public String toString() {
		return "ClassInfo{" +
				"clazz=" + clazz.getName() +
				", fieldInfos=" + propertyInfos +
				'}';
	}
}
