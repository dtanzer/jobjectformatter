package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.Formatted;
import net.davidtanzer.jobjectformatter.annotations.FormattedField;
import net.davidtanzer.jobjectformatter.annotations.FormattedType;
import net.davidtanzer.jobjectformatter.annotations.Transitive;
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
		List<FieldInfo> fields = fieldsFilter.getFilteredFields(NoAnnotations.class, typeInfoCache);

		assertThat(fields, is(not(nullValue())));
	}


	@Test
	public void includesAllFieldsIfNoAnnotationsArePresent() {
		List<FieldInfo> fields = fieldsFilter.getFilteredFields(NoAnnotations.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));

		assertThat(fields.size(), is(2));
		assertThat(fields, containsInAnyOrder(
				hasProperty("name", is("foo")), hasProperty("name", is("bar"))
		));
	}

	@Test
	public void includesNoFields_WhenFormattedAnnotatedAnnotationIsOnClass_AndNoFieldsAreAnnotated() {
		List<FieldInfo> fields = fieldsFilter.getFilteredFields(NoAnnotatedFields.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));

		assertThat(fields.size(), is(0));
	}

	@Test
	public void includesAnnotatedFields_WhenFormattedAnnotatedAnnotationIsOnClass() {
		List<FieldInfo> fields = fieldsFilter.getFilteredFields(AnnotatedFields.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));

		assertThat(fields.size(), is(1));
		assertThat(fields.get(0), hasProperty("name", is("foo")));
	}

	@Test
	public void fieldsAnnotatedWithFormatted_HaveTransitiveBehaviorFromAnnotation() {
		List<FieldInfo> fields = fieldsFilter.getFilteredFields(FieldTransitive.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));
		assumeThat(fields.size(), is(1));
		assumeThat(fields.get(0), hasProperty("name", is("noAnnotations")));

		assertThat(fields.get(0), hasProperty("transitive", is(Transitive.ALWAYS)));
	}

	@Test
	public void fieldsNotAnnotated_HaveTransitiveBehaviorDeterminedByTypeInfoCache() {
		when(typeInfoCache.transitiveBehaviorFor(any())).thenReturn(Transitive.ALWAYS);

		List<FieldInfo> fields = fieldsFilter.getFilteredFields(FieldTypeTransitive.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));
		assumeThat(fields.size(), is(1));
		assumeThat(fields.get(0), hasProperty("name", is("typeTransitive")));

		assertThat(fields.get(0), hasProperty("transitive", is(Transitive.ALWAYS)));
	}

	@Test
	public void transitiveAnnotationOnFields_IsMoreImportantThanBehaviorSpecifiedByTypeInfoCache() {
		when(typeInfoCache.transitiveBehaviorFor(any())).thenReturn(Transitive.ALLOWED);

		List<FieldInfo> fields = fieldsFilter.getFilteredFields(FieldTransitive.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));
		assumeThat(fields.size(), is(1));
		assumeThat(fields.get(0), hasProperty("name", is("noAnnotations")));

		assertThat(fields.get(0), hasProperty("transitive", is(Transitive.ALWAYS)));
	}

	private class NoAnnotations {
		private int foo;
		private String bar;
	}

	@Formatted(FormattedType.ANNOTATED)
	private class NoAnnotatedFields {
		private int foo;
		private String bar;
	}

	@Formatted(FormattedType.ANNOTATED)
	private class AnnotatedFields {
		@FormattedField
		private int foo;
		private String bar;
	}

	private class NotTransitive {
		private NoAnnotations noAnnotations;
	}

	private class FieldTransitive {
		@Formatted(transitive = Transitive.ALWAYS)
		private NoAnnotations noAnnotations;
	}

	@Formatted(transitive = Transitive.ALWAYS)
	private class TypeTransitive {
	}

	private class FieldTypeTransitive {
		private TypeTransitive typeTransitive;
	}
}
