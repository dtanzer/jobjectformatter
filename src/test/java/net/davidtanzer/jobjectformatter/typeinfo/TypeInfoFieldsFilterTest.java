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
import static org.mockito.Mockito.mock;

public class TypeInfoFieldsFilterTest {
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
	}

	private class NoAnnotations {
		private int foo;
		private String bar;
	}
}
