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

public class TypeInfo {
	private final List<ClassInfo> classInfos;
	private final TransitiveInclude transitiveInclude;
	private final FormattedInclude formattingBehavior;

	private TypeInfo(final List<ClassInfo> classInfos, final TransitiveInclude transitiveInclude, final FormattedInclude formattingBehavior) {
		this.classInfos = classInfos;
		this.transitiveInclude = transitiveInclude;
		this.formattingBehavior = formattingBehavior;
	}

	public List<ClassInfo> classInfos() {
		return classInfos;
	}

	public TransitiveInclude getTransitiveInclude() {
		return transitiveInclude;
	}

	public FormattedInclude getFormattingBehavior() {
		return formattingBehavior;
	}

	TypeInfo withTransitiveBehavior(final TransitiveInclude transitiveInclude) {
		return new TypeInfo(this.classInfos, transitiveInclude, this.formattingBehavior);
	}

	static class Builder {
		private final TypeInfoCache typeInfoCache;
		private final FieldsFilter fieldsFilter;
		private FormattedInclude formattingBehavior;
		private TransitiveInclude transitiveInclude;
		private List<ClassInfo> classInfos = new ArrayList<>();

		Builder(final TypeInfoCache typeInfoCache, final FieldsFilter fieldsFilter) {
			this.typeInfoCache = typeInfoCache;
			this.fieldsFilter = fieldsFilter;
		}

		public TypeInfo buildTypeInfo() {
			return new TypeInfo(Collections.unmodifiableList(this.classInfos), this.transitiveInclude, this.formattingBehavior);
		}

		public Builder withFormattingBehavior(final FormattedInclude formattingBehavior, final TransitiveInclude transitiveInclude) {
			this.formattingBehavior = formattingBehavior;
			this.transitiveInclude = transitiveInclude;
			return this;
		}

		public Builder addInfoForClass(final Class<?> currentType) {
			this.classInfos.add(new ClassInfo(currentType, typeInfoCache, fieldsFilter));
			return this;
		}
	}
}
