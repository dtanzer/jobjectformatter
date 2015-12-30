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
import net.davidtanzer.jobjectformatter.annotations.TransitiveInclude;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TypeInfoTransitivityTest {
	private TypeInfoCache typeInfoCache;

	@Before
	public void setup() {
		typeInfoCache = new TypeInfoCache();
	}

	@Test
	public void allTypeInfosFromJavaPackagesAreTransitive() {
		final TypeInfo info = typeInfoCache.typeInfoFor(SimpleObject.class);

		List<PropertyInfo> propertyInfos = info.classInfos().get(0).fieldInfos();
		assertThat(propertyInfos, hasItem(allOf(
				hasProperty("name", is("foo")),
				hasProperty("transitiveIncludeOfTarget", is(TransitiveInclude.ALL_FIELDS)))));

		assertThat(propertyInfos, hasItem(allOf(
				hasProperty("name", is("bar")),
				hasProperty("transitiveIncludeOfTarget", is(TransitiveInclude.ALL_FIELDS)))));
	}

	@Test
	public void typeInfosForOtherDependenciesAreNotTransitiveByDefault() {
		final TypeInfo info = typeInfoCache.typeInfoFor(DependentObject.class);

		List<PropertyInfo> propertyInfos = info.classInfos().get(0).fieldInfos();
		assertThat(propertyInfos, hasItem(allOf(
				hasProperty("name", is("so")),
				hasProperty("transitiveIncludeOfTarget", is(TransitiveInclude.NO_FIELDS)))));
	}

	@Test
	public void typeInfosFromJavaPackagesCanBeSetToNonTransitiveByAnnotation() {
		final TypeInfo info = typeInfoCache.typeInfoFor(SimpleObject.class);

		List<PropertyInfo> propertyInfos = info.classInfos().get(0).fieldInfos();
		assertThat(propertyInfos, hasItem(allOf(
				hasProperty("name", is("notTransitive")),
				hasProperty("transitiveIncludeOfTarget", is(TransitiveInclude.NO_FIELDS)))));
	}

	@Test
	public void typeInfosFromOtherPackagesCanBeSetToTransitiveByAnnotation() {
		final TypeInfo info = typeInfoCache.typeInfoFor(DependentObject.class);

		List<PropertyInfo> propertyInfos = info.classInfos().get(0).fieldInfos();
		assertThat(propertyInfos, hasItem(allOf(
				hasProperty("name", is("soTransitive")),
				hasProperty("transitiveIncludeOfTarget", is(TransitiveInclude.ANNOTADED_FIELDS)))));
	}

	private class SimpleObject {
		int foo;
		String bar;

		@Formatted(transitive = TransitiveInclude.NO_FIELDS)
		String notTransitive;
	}

	private class DependentObject {
		private SimpleObject so;
		@Formatted
		private SimpleObject soTransitive;
	}
}
