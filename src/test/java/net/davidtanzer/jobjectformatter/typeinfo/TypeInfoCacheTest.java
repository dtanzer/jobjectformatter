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
import net.davidtanzer.jobjectformatter.annotations.FormattedFieldType;
import net.davidtanzer.jobjectformatter.annotations.FormattedInclude;
import net.davidtanzer.jobjectformatter.annotations.TransitiveInclude;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TypeInfoCacheTest {
	private TypeInfoCache typeInfoCache;

	@Before
	public void setup() throws Exception {
		typeInfoCache = new TypeInfoCache();
	}

	@Test
	public void addsClassInfo_ForEveryClassInTheHierarchy() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(ExtendedObject.class);

		assertThat(typeInfo.classInfos(), contains(
				hasProperty("clazz", is((Object) ExtendedObject.class)),
				hasProperty("clazz", is((Object) SimpleObject.class))));
	}

	@Test
	public void classInfosOfTypeInfo_AreInTheCorrectOrder() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(ExtendedObject.class);

		final ClassInfo extendedObjectInfo = typeInfo.classInfos().get(0);
		assertThat(extendedObjectInfo.getClazz(), is((Object) ExtendedObject.class));
	}

	@Test
	public void classInfo_ContainsAllFieldsFromTheClass() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(ExtendedObject.class);

		final ClassInfo extendedObjectInfo = typeInfo.classInfos().get(0);
		assumeThat(extendedObjectInfo.getClazz(), is((Object) ExtendedObject.class));
		assertThat(extendedObjectInfo.fieldInfos(), containsInAnyOrder(
				hasProperty("name", is("eFoo")),
				hasProperty("name", is("eBar"))
		));
	}

	@Test
	public void classInfo_DoesNotContainFieldsFromOtherClasses() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(ExtendedObject.class);

		final ClassInfo extendedObjectInfo = typeInfo.classInfos().get(0);
		assumeThat(extendedObjectInfo.getClazz(), is((Object) ExtendedObject.class));
		assertThat(extendedObjectInfo.fieldInfos(), not(hasItem(
				hasProperty("name", is("foo"))
		)));
	}

	@Test
	public void usesClassInfoFromCache_IfAvailable() {
		final TypeInfo typeInfo1 = typeInfoCache.typeInfoFor(ExtendedObject.class);
		final TypeInfo typeInfo2 = typeInfoCache.typeInfoFor(ExtendedObject.class);

		assertSame(typeInfo1, typeInfo2);
	}

	@Test
	public void usesFieldsFilter_ToActuallyGetTheRelevantFields() throws NoSuchFieldException {
		final FieldsFilter fieldsFilter = mock(FieldsFilter.class);
		typeInfoCache = new TypeInfoCache(fieldsFilter);

		final FormattedFieldType includeField = FormattedFieldType.DEFAULT;
		final FormattedFieldType includeFieldInTransitive = FormattedFieldType.DEFAULT;
		when(fieldsFilter.getFilteredFields(SimpleObject.class, typeInfoCache)).thenReturn(Arrays.asList(
				new PropertyInfo(ObjectWithOtherFields.class.getDeclaredField("field1"), TransitiveInclude.ANNOTADED_FIELDS, includeField, includeFieldInTransitive),
				new PropertyInfo(ObjectWithOtherFields.class.getDeclaredField("field2"), TransitiveInclude.ANNOTADED_FIELDS, includeField, includeFieldInTransitive)
		));

		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(SimpleObject.class);

		assertThat(typeInfo.classInfos().get(0).fieldInfos(), containsInAnyOrder(
				hasProperty("name", is("field1")),
				hasProperty("name", is("field2"))
		));
	}

	@Test
	public void transitiveBehaviorForUnannotatedClass_IsDisallowed() {
		final TransitiveInclude transitiveInclude = typeInfoCache.transitiveIncludeFor(SimpleObject.class);

		assertThat(transitiveInclude, is(TransitiveInclude.NO_FIELDS));
	}

	@Test
	public void transitiveBehaviorForAnnotatedClass_IsDeterminedByAnnotationValue() {
		final TransitiveInclude transitiveInclude = typeInfoCache.transitiveIncludeFor(TransitiveAnnotatedClass.class);

		assertThat(transitiveInclude, is(TransitiveInclude.ALL_FIELDS));
	}

	@Test
	public void transitiveBehaviorForAnnotatedToString_IsDeterminedByAnnotationValue() {
		final TransitiveInclude transitiveInclude = typeInfoCache.transitiveIncludeFor(TransitiveAnnotatedToString.class);

		assertThat(transitiveInclude, is(TransitiveInclude.ALL_FIELDS));
	}

	@Test
	public void transitiveBehaviorForAnnotatedClass_IsMoreImportantThanAnnotationOnClass() {
		final TransitiveInclude transitiveInclude = typeInfoCache.transitiveIncludeFor(TransitiveAnnotatedBoth.class);

		assertThat(transitiveInclude, is(TransitiveInclude.NO_FIELDS));
	}

	@Test
	public void typeInfoHasInformationFromTheFormattedAnnotationOnClass() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(TransitiveAnnotatedBoth.class);

		assertThat(typeInfo.getTransitiveInclude(), is(TransitiveInclude.NO_FIELDS));
		assertThat(typeInfo.getFormattedInclude(), is(FormattedInclude.NO_FIELDS));
	}

	private class SimpleObject {
		private String foo;
		private String bar;
	}

	private class ExtendedObject extends SimpleObject {
		private String eFoo;
		private String eBar;
	}

	private static class ObjectWithOtherFields {
		private String field1;
		private String field2;
	}

	@Formatted(transitive = TransitiveInclude.ALL_FIELDS)
	private class TransitiveAnnotatedClass {
	}

	private class TransitiveAnnotatedToString {
		@Override
		@Formatted(transitive = TransitiveInclude.ALL_FIELDS)
		public String toString() {
			return super.toString();
		}
	}

	@Formatted(transitive = TransitiveInclude.NO_FIELDS, value = FormattedInclude.NO_FIELDS)
	private class TransitiveAnnotatedBoth {
		@Override
		@Formatted(transitive = TransitiveInclude.ALL_FIELDS)
		public String toString() {
			return super.toString();
		}
	}
}