package net.davidtanzer.jobjectformatter.valuesinfo;

import net.davidtanzer.jobjectformatter.annotations.*;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfoCache;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

public class ObjectValuesCompilerTest {
	private ObjectValuesCompiler objectValuesCompiler;

	@Before
	public void setup() throws Exception {
		objectValuesCompiler = new ObjectValuesCompiler();
	}

	@Test
	public void addsValueEntryForEveryClassInfo() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ExtendedObject.class);
		final ExtendedObject object = new ExtendedObject();

		final ObjectValuesInfo info = objectValuesCompiler.compileToStringInfo(typeInfo, object);

		assertThat(info.getValuesByClass(), contains(
				hasProperty("groupName", is("ExtendedObject")),
				hasProperty("groupName", is("SimpleObject"))
		));
	}

	@Test
	public void addsValuesForAllFieldsToClassValueInfo() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ExtendedObject.class);
		final ExtendedObject object = new ExtendedObject();

		final ObjectValuesInfo info = objectValuesCompiler.compileToStringInfo(typeInfo, object);
		assumeThat(info.getValuesByClass().get(0).getGroupName(), is("ExtendedObject"));

		final GroupedValuesInfo classValuesInfo = info.getValuesByClass().get(0);
		assertThat(classValuesInfo.getValues(), containsInAnyOrder(
				is(new ValueInfo("eFoo", "eFoo val", String.class)),
				is(new ValueInfo("eBar", "eBar val", String.class))
		));
	}

	@Test
	public void abbreviatesTransitiveObject_WhenTransitivityIsNotAllowed() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainingObject.class);
		final ObjectValuesInfo info = objectValuesCompiler.compileToStringInfo(typeInfo, new ContainingObject());

		assumeThat(info.getValuesByClass().get(0).getGroupName(), is("ContainingObject"));

		final GroupedValuesInfo classValuesInfo = info.getValuesByClass().get(0);
		assertThat(classValuesInfo.getValues(), contains(
				is(new ValueInfo("containedObject", "[not null]", SimpleContainedObject.class))
		));
	}

	@Test
	public void includesTransitiveObjectFully_WhenTransitivityIsSetToAlways() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainingObjectAlwaysTransitive.class);
		final ObjectValuesInfo info = objectValuesCompiler.compileToStringInfo(typeInfo, new ContainingObjectAlwaysTransitive());

		assumeThat(info.getValuesByClass().get(0).getGroupName(), is("ContainingObjectAlwaysTransitive"));

		final GroupedValuesInfo classValuesInfo = info.getValuesByClass().get(0);
		assertThat(classValuesInfo.getValues(), contains(
				is(new ValueInfo("containedObject", "result of toString", SimpleContainedObject.class))
		));
	}

	@Test
	public void includesAnnotatedTransitiveValues_WhenTransitivityIsSetToAnnotated() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainingObjectAnnotatedTransitive.class);
		final ObjectValuesInfo info = objectValuesCompiler.compileToStringInfo(typeInfo, new ContainingObjectAnnotatedTransitive());

		assumeThat(info.getValuesByClass().get(0).getGroupName(), is("ContainingObjectAnnotatedTransitive"));

		final GroupedValuesInfo classValuesInfo = info.getValuesByClass().get(0);
		assertThat(classValuesInfo.getValues().get(0).getValue(), instanceOf(ObjectValuesInfo.class));
		final ObjectValuesInfo innerValuesInfo = (ObjectValuesInfo) classValuesInfo.getValues().get(0).getValue();
		assertThat(innerValuesInfo.getAllValues(), contains(hasProperty("propertyName", is("prop1"))));
	}

	@Test
	public void stringDoesNotContainAnyFields_WhenClassIsAnnotatedAsFormattedAnnotated_ButHasNoAnnotatedFields() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(FormattedObjectWithoutFields.class);
		final ObjectValuesInfo info = objectValuesCompiler.compileToStringInfo(typeInfo, new FormattedObjectWithoutFields());

		assumeThat(info.getValuesByClass().get(0).getGroupName(), is("FormattedObjectWithoutFields"));

		final GroupedValuesInfo classValuesInfo = info.getValuesByClass().get(0);
		assertThat(classValuesInfo.getValues().size(), is(0));
	}

	@Test
	public void stringContainsAllFields_WhenClassIsAnnotatedAsFormattedAll() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(FormattedAllObjectWithFields.class);
		final ObjectValuesInfo info = objectValuesCompiler.compileToStringInfo(typeInfo, new FormattedAllObjectWithFields());

		assumeThat(info.getValuesByClass().get(0).getGroupName(), is("FormattedAllObjectWithFields"));

		final GroupedValuesInfo classValuesInfo = info.getValuesByClass().get(0);
		assertThat(classValuesInfo.getValues().size(), is(2));
	}

	@Test
	public void stringContainsAnnotatedFields_WhenClassIsAnnotatedAsFormattedAnnotated_AndHasAnnotatedFields() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(FormattedAnnotatedObjectWithFields.class);
		final ObjectValuesInfo info = objectValuesCompiler.compileToStringInfo(typeInfo, new FormattedAnnotatedObjectWithFields());

		assumeThat(info.getValuesByClass().get(0).getGroupName(), is("FormattedAnnotatedObjectWithFields"));

		final GroupedValuesInfo classValuesInfo = info.getValuesByClass().get(0);
		assertThat(classValuesInfo.getValues().size(), is(1));
		assertThat(classValuesInfo.getValues(), contains(hasProperty("propertyName", is("bar"))));
	}

	private class SimpleObject {
		String foo="foo val";
		String bar="bar val";
	}

	private class ExtendedObject extends SimpleObject {
		String eFoo="eFoo val";
		String eBar="eBar val";
	}

	private class SimpleContainedObject {
		@FormattedField(transitive = FormattedFieldType.DEFAULT)
		String prop1 = "contained property 1";
		String prop2 = "contained property 2";

		@Override
		@Formatted(transitive = Transitive.DISALLOWED)
		public String toString() {
			return "result of toString";
		}
	}

	@Formatted(FormattedType.ALL)
	private class ContainingObject {
		SimpleContainedObject containedObject = new SimpleContainedObject();
	}

	private class ContainingObjectAlwaysTransitive {
		@Formatted(transitive = Transitive.ALWAYS)
		SimpleContainedObject containedObject = new SimpleContainedObject();
	}

	private class ContainingObjectAnnotatedTransitive {
		@Formatted(transitive = Transitive.ALLOWED)
		SimpleContainedObject containedObject = new SimpleContainedObject();
	}

	@Formatted
	private class FormattedObjectWithoutFields {
		private String foo = "foo";
	}

	@Formatted(FormattedType.ALL)
	private class FormattedAllObjectWithFields {
		private String foo = "foo";
		@FormattedField
		private String bar = "bar";
	}

	@Formatted
	private class FormattedAnnotatedObjectWithFields {
		private String foo = "foo";
		@FormattedField
		private String bar = "bar";
	}
}