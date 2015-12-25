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

import net.davidtanzer.jobjectformatter.annotations.FormattedFieldType;
import net.davidtanzer.jobjectformatter.annotations.TransitiveInclude;
import net.davidtanzer.jobjectformatter.typeinfo.ClassInfo;
import net.davidtanzer.jobjectformatter.typeinfo.FieldInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfoCache;

import java.util.function.Predicate;

public class ObjectValuesCompiler {
	private final TypeInfoCache typeInfoCache;

	public ObjectValuesCompiler() {
		this(new TypeInfoCache());
	}

	public ObjectValuesCompiler(final TypeInfoCache typeInfoCache) {
		this.typeInfoCache = typeInfoCache;
	}

	public ObjectValuesInfo compileToStringInfo(final TypeInfo typeInfo, final Object object) {
		return compileToStringInfo(typeInfo, object, t -> true);
	}

	private ObjectValuesInfo compileToStringInfo(final TypeInfo typeInfo, final Object object, final Predicate<FormattedFieldType> includeInTransitive) {
		ObjectValuesInfo.Builder builder = new ObjectValuesInfo.Builder();

		for(ClassInfo classInfo : typeInfo.classInfos()) {
			builder.addClassValues(compileClassValues(typeInfo, classInfo, object, includeInTransitive));
		}

		return builder.buildToStringInfo();
	}

	private GroupedValuesInfo compileClassValues(final TypeInfo typeInfo, final ClassInfo classInfo, final Object object, final Predicate<FormattedFieldType> includeInTransitive) {
		GroupedValuesInfo.Builder builder = new GroupedValuesInfo.Builder();
		builder.setClassName(classInfo.getClazz().getSimpleName());

		for(FieldInfo fieldInfo : classInfo.fieldInfos()) {
			formatFieldValueIfNecessary(typeInfo, object, builder, fieldInfo, includeInTransitive);
		}

		return builder.buildByClassValuesInfo();
	}

	private void formatFieldValueIfNecessary(final TypeInfo typeInfo, final Object object, final GroupedValuesInfo.Builder builder, final FieldInfo fieldInfo, final Predicate<FormattedFieldType> includeInTransitive) {
		if(includeInTransitive.test(fieldInfo.getIncludeFieldInTransitive())) {
			Object formattedFieldValue = formatFieldValue(object, fieldInfo);
			typeInfo.getFormattingBehavior().addFieldValueTo(builder, fieldInfo, formattedFieldValue);
		}
	}

	private Object formatFieldValue(final Object object, final FieldInfo fieldInfo) {
		Object fieldValue = fieldInfo.getFieldValue(object);

		Boolean hasFormattedAnnotation = hasFormattedAnnotation(fieldValue, fieldInfo);
		ObjectValuesInfo transitiveValues = this.compileToStringInfo(
				typeInfoCache.typeInfoFor(fieldValue.getClass(), fieldInfo.getTransitiveIncludeOfTarget()), fieldValue, transitive -> transitive.equals(FormattedFieldType.DEFAULT));

		return fieldInfo.getTransitiveIncludeOfTarget().transitiveFieldValue(fieldValue, hasFormattedAnnotation, transitiveValues);
	}

	private Boolean hasFormattedAnnotation(final Object fieldValue, final FieldInfo fieldInfo) {
		return typeInfoCache.behaviorFor(fieldValue.getClass(), (f) -> true, false)
				|| fieldInfo.getTransitiveIncludeOfTarget().equals(TransitiveInclude.ANNOTADED_FIELDS)
				|| fieldInfo.getTransitiveIncludeOfTarget().equals(TransitiveInclude.ALL_FIELDS);
	}
}
