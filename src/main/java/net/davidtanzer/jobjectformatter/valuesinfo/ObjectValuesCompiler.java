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
import net.davidtanzer.jobjectformatter.typeinfo.PropertyInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfoCache;

import java.util.function.Predicate;

/**
 *
 * <strong>ObjectValuesCompiler</strong>
 * <ul>
 *     <li>Adds value entry for every class info.</li>
 *     <li>Adds values for all fields to class value info.</li>
 *     <li>Abbreviates transitive object_ when transitivity is not allowed.</li>
 *     <li>Includes transitive object fully_ when transitivity is set to always.</li>
 *     <li>Includes annotated transitive values_ when transitivity is set to annotated.</li>
 *     <li>Includes annotated transitive values_ when transitivity is set to annotated_ by containing class only.</li>
 *     <li>String does not contain any fields_ when class is annotated as formatted annotated_ but has no annotated fields.</li>
 *     <li>String contains all fields_ when class is annotated as formatted all.</li>
 *     <li>String contains annotated fields_ when class is annotated as formatted annotated_ and has annotated fields.</li>
 * </ul>
 */
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
		builder.setType(object.getClass());

		for(ClassInfo classInfo : typeInfo.classInfos()) {
			builder.addClassValues(compileClassValues(typeInfo, classInfo, object, includeInTransitive));
		}

		return builder.buildToStringInfo();
	}

	private GroupedValuesInfo compileClassValues(final TypeInfo typeInfo, final ClassInfo classInfo, final Object object, final Predicate<FormattedFieldType> includeInTransitive) {
		GroupedValuesInfo.Builder builder = new GroupedValuesInfo.Builder();
		builder.setClassName(classInfo.getClazz().getSimpleName());

		for(PropertyInfo propertyInfo : classInfo.fieldInfos()) {
			formatFieldValueIfNecessary(typeInfo, object, builder, propertyInfo, includeInTransitive);
		}

		return builder.buildGroupedValuesInfo();
	}

	private void formatFieldValueIfNecessary(final TypeInfo typeInfo, final Object object, final GroupedValuesInfo.Builder builder, final PropertyInfo propertyInfo, final Predicate<FormattedFieldType> includeInTransitive) {
		if(includeInTransitive.test(propertyInfo.getIncludeFieldInTransitive())) {
			Object formattedFieldValue = formatFieldValue(object, propertyInfo);
			typeInfo.getFormattedInclude().addFieldValueTo(builder, propertyInfo, formattedFieldValue);
		}
	}

	private Object formatFieldValue(final Object object, final PropertyInfo propertyInfo) {
		Object fieldValue = propertyInfo.getPropertyValue(object);
		if(fieldValue == null) {
			return null;
		}

		Boolean hasFormattedAnnotation = hasFormattedAnnotation(fieldValue, propertyInfo);
		ObjectValuesInfo transitiveValues = this.compileToStringInfo(
				typeInfoCache.typeInfoFor(fieldValue.getClass(), propertyInfo.getTransitiveIncludeOfTarget()), fieldValue, transitive -> transitive.equals(FormattedFieldType.DEFAULT));

		return propertyInfo.getTransitiveIncludeOfTarget().transitiveFieldValue(fieldValue, hasFormattedAnnotation, transitiveValues);
	}

	private Boolean hasFormattedAnnotation(final Object fieldValue, final PropertyInfo propertyInfo) {
		return typeInfoCache.configurationFor(fieldValue.getClass(), (f) -> true, false)
				|| propertyInfo.getTransitiveIncludeOfTarget().equals(TransitiveInclude.ANNOTADED_FIELDS)
				|| propertyInfo.getTransitiveIncludeOfTarget().equals(TransitiveInclude.ALL_FIELDS);
	}
}
