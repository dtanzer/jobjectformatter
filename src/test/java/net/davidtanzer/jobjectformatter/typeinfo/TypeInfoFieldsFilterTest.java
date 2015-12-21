package net.davidtanzer.jobjectformatter.typeinfo;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

public class TypeInfoFieldsFilterTest {
	private FieldsFilter fieldsFilter;

	@Before
	public void setup() {
		fieldsFilter = new FieldsFilter();
	}

	@Test
	public void alwaysReturnsANonNullCollection() {
		List<Field> fields = fieldsFilter.getFilteredFields(NoAnnotations.class);

		assertThat(fields, is(not(nullValue())));
	}


	@Test
	public void includesAllFieldsIfNoAnnotationsArePresent() {
		List<Field> fields = fieldsFilter.getFilteredFields(NoAnnotations.class);
		assumeThat(fields, is(not(nullValue())));

		assertThat(fields.size(), is(2));
	}

	private class NoAnnotations {
		private int foo;
		private String bar;
	}
}
