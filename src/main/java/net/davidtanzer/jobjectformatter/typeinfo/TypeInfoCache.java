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

import net.davidtanzer.jobjectformatter.annotations.Formatted;
import net.davidtanzer.jobjectformatter.annotations.FormattedInclude;
import net.davidtanzer.jobjectformatter.annotations.TransitiveInclude;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Creates and caches information about types (classes) so that the later stages of processing can use this information.
 *
 * The TypeInfoCache compiles and caches information about classes that will be needed by the
 * {@link net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesCompiler}. It creates a
 * {@link net.davidtanzer.jobjectformatter.typeinfo.TypeInfo} object for every class it processes.
 *
 * <strong>TypeInfoCache</strong> functionality (extracted from the tests by agiledox):
 * <ul>
 *     <li>Creates type info object for class.</li>
 *     <li>Uses type info from cache if available.</li>
 *     <li>Type info has information from the formatted annotation on class.</li>
 *     <li>Type info contains a class info for every class in the hierarchy.</li>
 *     <li>Class infos of type info are ordered from type itself to classes up the call hierarchy.</li>
 *     <li>Every class info contains fields from the class.</li>
 *     <li>Class info does not contain fields from other classes.</li>
 *     <li>Uses fields filter to actually get the relevant fields for a class info.</li>
 *     <li>Transitive include for unannotated class is disallowed.</li>
 *     <li>Transitive include for annotated class is determined by annotation value.</li>
 *     <li>Transitive include for annotated to string is determined by annotation value.</li>
 *     <li>Transitive include for annotated class is more important than annotation on class.</li>
 * </ul>
 *
 * <strong>TypeInfoTransitivity</strong> functionality (extracted from the tests by agiledox):
 * <ul>
 *     <li>All type infos from java packages are transitive.</li>
 *     <li>Type infos for other dependencies are not transitive by default.</li>
 *     <li>Type infos from java packages can be set to non transitive by annotation.</li>
 *     <li>Type infos from other packages can be set to transitive by annotation.</li>
 * </ul>
 *
 * @see net.davidtanzer.jobjectformatter.typeinfo.TypeInfo
 * @see net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesCompiler
 */
public class TypeInfoCache {
	private final FieldsFilter fieldsFilter;
	private Map<Class<?>, TypeInfo> cachedTypeInfos = new ConcurrentHashMap<>();

	/**
	 * Create a new TypeInfoCache with a default FieldsFilter.
	 *
	 * @see net.davidtanzer.jobjectformatter.typeinfo.FieldsFilter
	 */
	public TypeInfoCache() {
		this(new FieldsFilter());
	}

	TypeInfoCache(final FieldsFilter fieldsFilter) {
		this.fieldsFilter = fieldsFilter;
	}

	/**
	 * Get the type info for a certain type (from the cache if available).
	 *
	 * @param type The java class for which you need a type info.
	 * @return The type info (newly computed or from the cache).
	 */
	public TypeInfo typeInfoFor(final Class<?> type) {
		return cachedTypeInfos.computeIfAbsent(type, this::createTypeInfoFrom);
	}

	/**
	 * Get the type info for a certain type with a different include configuration(from the cache if available).
	 *
	 * @param type The java class for which you need a type info.
	 * @param transitiveInclude The transitive include configuration for the type info.
	 * @return The type info (newly computed or from the cache).
	 */
	public TypeInfo typeInfoFor(final Class<?> type, final TransitiveInclude transitiveInclude) {
		return typeInfoFor(type).withTransitiveInclude(transitiveInclude);
	}

	private TypeInfo createTypeInfoFrom(final Class<?> type) {
		TypeInfo.Builder builder = new TypeInfo.Builder(this, fieldsFilter);

		Class<?> currentType = type;
		while(currentType != null && currentType != Object.class) {
			builder.addInfoForClass(currentType);
			currentType = currentType.getSuperclass();
		}

		builder.withFormattingConfiguration(formattedIncludeFor(type), transitiveIncludeFor(type));
		return builder.buildTypeInfo();
	}

	private FormattedInclude formattedIncludeFor(final Class<?> type) {
		return configurationFor(type, f -> f.value(), FormattedInclude.ALL_FIELDS);
	}

	TransitiveInclude transitiveIncludeFor(final Class<?> type) {
		return configurationFor(type, f -> f.transitive(), TransitiveInclude.NO_FIELDS);
	}

	/**
	 * Get a certain configuration for a type, based on it's {@link net.davidtanzer.jobjectformatter.annotations.Formatted} annotation value.
	 *
	 * @param type The type for which you need the configuration.
	 * @param mappingFunction A function that extracts the configuration from a Formatted annotation.
	 * @param defaultValue The default value you'll use when no annotation is present.
	 * @param <T> The configuration type.
	 * @return The extracted configuration (either computed by calling the provided mapping function, or the defaultValue).
	 *
	 * @see net.davidtanzer.jobjectformatter.annotations.Formatted
	 */
	public <T> T configurationFor(final Class<?> type, final Function<Formatted, T> mappingFunction, final T defaultValue) {
		assert type != null : "Parameter \"type\" must not be null.";
		assert mappingFunction != null : "Parameter \"mappingFunction\" must not be null.";
		assert defaultValue != null : "Parameter \"defaultValue\" must not be null.";

		try {
			if(type.isAnnotationPresent(Formatted.class)) {
				return mappingFunction.apply(type.getAnnotation(Formatted.class));
			}

			Method toStringMethod = type.getMethod("toString");
			assert toStringMethod != null : "Cannot be null, since we would get a NoSuchMethodException when the method does not exist.";

			if(toStringMethod.isAnnotationPresent(Formatted.class)) {
				return mappingFunction.apply(toStringMethod.getAnnotation(Formatted.class));
			}
		} catch (NoSuchMethodException e) {
			ignoreException_BecauseNotAllTypesHaveToString_AndWeDontCare(e);
		}
		return defaultValue;
	}

	private void ignoreException_BecauseNotAllTypesHaveToString_AndWeDontCare(final NoSuchMethodException e) {
	}
}
