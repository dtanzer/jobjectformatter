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

import net.davidtanzer.jobjectformatter.annotations.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FieldsFilterTest {
	private FieldsFilter fieldsFilter;
	private TypeInfoCache typeInfoCache;

	@Before
	public void setup() {
		fieldsFilter = new FieldsFilter();
		typeInfoCache = mock(TypeInfoCache.class);
	}

	@Test
	public void alwaysReturnsANonNullCollection() {
		List<PropertyInfo> fields = fieldsFilter.getFilteredFields(NoAnnotations.class, typeInfoCache);

		assertThat(fields, is(not(nullValue())));
	}


	@Test
	public void includesAllFieldsIfNoAnnotationsArePresent() {
		List<PropertyInfo> fields = fieldsFilter.getFilteredFields(NoAnnotations.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));

		assertThat(fields.size(), is(2));
		assertThat(fields, containsInAnyOrder(
				hasProperty("name", is("foo")), hasProperty("name", is("bar"))
		));
	}

	@Test
	public void includesNoFields_WhenFormattedAnnotatedAnnotationIsOnClass_AndNoFieldsAreAnnotated() {
		List<PropertyInfo> fields = fieldsFilter.getFilteredFields(NoAnnotatedFields.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));

		assertThat(fields.size(), is(2));
		assertThat(fields, everyItem(hasProperty("includeField", is(FormattedFieldType.NEVER))));
	}

	@Test
	public void includesAnnotatedFields_WhenFormattedAnnotatedAnnotationIsOnClass() {
		List<PropertyInfo> fields = fieldsFilter.getFilteredFields(AnnotatedFields.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));

		assertThat(fields.size(), is(2));
		assertThat(fields.get(0), allOf(
				hasProperty("name", is("foo")), hasProperty("includeField", is(FormattedFieldType.DEFAULT))));
		assertThat(fields.get(1), allOf(
				hasProperty("name", is("bar")), hasProperty("includeField", is(FormattedFieldType.NEVER))));
	}

	@Test
	public void fieldsAnnotatedWithFormatted_HaveTransitiveBehaviorFromAnnotation() {
		List<PropertyInfo> fields = fieldsFilter.getFilteredFields(FieldTransitive.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));
		assumeThat(fields.size(), is(1));
		assumeThat(fields.get(0), hasProperty("name", is("noAnnotations")));

		assertThat(fields.get(0), hasProperty("transitiveIncludeOfTarget", is(TransitiveInclude.ALL_FIELDS)));
	}

	@Test
	public void fieldsNotAnnotated_HaveTransitiveBehaviorDeterminedByTypeInfoCache() {
		when(typeInfoCache.transitiveIncludeFor(any())).thenReturn(TransitiveInclude.ALL_FIELDS);

		List<PropertyInfo> fields = fieldsFilter.getFilteredFields(FieldTypeTransitive.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));
		assumeThat(fields.size(), is(1));
		assumeThat(fields.get(0), hasProperty("name", is("typeTransitive")));

		assertThat(fields.get(0), hasProperty("transitiveIncludeOfTarget", is(TransitiveInclude.ALL_FIELDS)));
	}

	@Test
	public void transitiveAnnotationOnFields_IsMoreImportantThanBehaviorSpecifiedByTypeInfoCache() {
		when(typeInfoCache.transitiveIncludeFor(any())).thenReturn(TransitiveInclude.ANNOTADED_FIELDS);

		List<PropertyInfo> fields = fieldsFilter.getFilteredFields(FieldTransitive.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));
		assumeThat(fields.size(), is(1));
		assumeThat(fields.get(0), hasProperty("name", is("noAnnotations")));

		assertThat(fields.get(0), hasProperty("transitiveIncludeOfTarget", is(TransitiveInclude.ALL_FIELDS)));
	}

	private class NoAnnotations {
		private int foo;
		private String bar;
	}

	@Formatted(FormattedInclude.ANNOTATED_FIELDS)
	private class NoAnnotatedFields {
		private int foo;
		private String bar;
	}

	@Formatted(FormattedInclude.ANNOTATED_FIELDS)
	private class AnnotatedFields {
		@FormattedField
		private int foo;
		private String bar;
	}

	private class NotTransitive {
		private NoAnnotations noAnnotations;
	}

	private class FieldTransitive {
		@Formatted(transitive = TransitiveInclude.ALL_FIELDS)
		private NoAnnotations noAnnotations;
	}

	@Formatted(transitive = TransitiveInclude.ALL_FIELDS)
	private class TypeTransitive {
	}

	private class FieldTypeTransitive {
		private TypeTransitive typeTransitive;
	}
}
