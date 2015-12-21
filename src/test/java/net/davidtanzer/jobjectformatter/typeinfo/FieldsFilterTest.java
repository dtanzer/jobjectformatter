package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.Formatted;
import net.davidtanzer.jobjectformatter.annotations.FormattedField;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.mock;

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
	public void includesNoFields_WhenFormattedAnnotationIsOnClass_AndNoFieldsAreAnnotated() {
		List<FieldInfo> fields = fieldsFilter.getFilteredFields(NoAnnotatedFields.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));

		assertThat(fields.size(), is(0));
	}

	@Test
	public void includesAnnotatedFields_WhenFormattedAnnotationIsOnClass() {
		List<FieldInfo> fields = fieldsFilter.getFilteredFields(AnnotatedFields.class, typeInfoCache);
		assumeThat(fields, is(not(nullValue())));

		assertThat(fields.size(), is(1));
		assertThat(fields.get(0), hasProperty("name", is("foo")));
	}

	private class NoAnnotations {
		private int foo;
		private String bar;
	}

	@Formatted
	private class NoAnnotatedFields {
		private int foo;
		private String bar;
	}

	@Formatted
	private class AnnotatedFields {
		@FormattedField
		private int foo;
		private String bar;
	}
}
