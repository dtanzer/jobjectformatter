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

import net.davidtanzer.jobjectformatter.annotations.FormattedInclude;
import net.davidtanzer.jobjectformatter.annotations.TransitiveInclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Type information about a class that can be formatted as string.
 */
public class TypeInfo {
	private final List<ClassInfo> classInfos;
	private final TransitiveInclude transitiveInclude;
	private final FormattedInclude formattedInclude;

	private TypeInfo(final List<ClassInfo> classInfos, final TransitiveInclude transitiveInclude, final FormattedInclude formattedInclude) {
		this.classInfos = classInfos;
		this.transitiveInclude = transitiveInclude;
		this.formattedInclude = formattedInclude;
	}

	/**
	 * Get all {@link net.davidtanzer.jobjectformatter.typeinfo.ClassInfo}s for property information grouped by declaring class.
	 *
	 * @return all {@link net.davidtanzer.jobjectformatter.typeinfo.ClassInfo}s for property information grouped by declaring class.
	 */
	public List<ClassInfo> classInfos() {
		return classInfos;
	}

	/**
	 * Get the transitive include configuration of the type.
	 *
	 * @return the transitive include configuration of the type.
	 */
	public TransitiveInclude getTransitiveInclude() {
		return transitiveInclude;
	}

	/**
	 * Get the formatting configuration of the type.
	 *
	 * @return the formatting configuration of the type.
	 */
	public FormattedInclude getFormattedInclude() {
		return formattedInclude;
	}

	TypeInfo withTransitiveInclude(final TransitiveInclude transitiveInclude) {
		return new TypeInfo(this.classInfos, transitiveInclude, this.formattedInclude);
	}

	static class Builder {
		private final TypeInfoCache typeInfoCache;
		private final FieldsFilter fieldsFilter;
		private FormattedInclude formattedInclude;
		private TransitiveInclude transitiveInclude;
		private List<ClassInfo> classInfos = new ArrayList<>();

		Builder(final TypeInfoCache typeInfoCache, final FieldsFilter fieldsFilter) {
			this.typeInfoCache = typeInfoCache;
			this.fieldsFilter = fieldsFilter;
		}

		public TypeInfo buildTypeInfo() {
			return new TypeInfo(Collections.unmodifiableList(this.classInfos), this.transitiveInclude, this.formattedInclude);
		}

		public Builder withFormattingConfiguration(final FormattedInclude formattingBehavior, final TransitiveInclude transitiveInclude) {
			this.formattedInclude = formattingBehavior;
			this.transitiveInclude = transitiveInclude;
			return this;
		}

		public Builder addInfoForClass(final Class<?> currentType) {
			this.classInfos.add(new ClassInfo(currentType, typeInfoCache, fieldsFilter));
			return this;
		}
	}
}
