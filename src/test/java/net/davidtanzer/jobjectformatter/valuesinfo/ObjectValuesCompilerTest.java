package net.davidtanzer.jobjectformatter.valuesinfo;

import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfoCache;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
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

	private class SimpleObject {
		String foo="foo val";
		String bar="bar val";
	}

	private class ExtendedObject extends SimpleObject {
		String eFoo="eFoo val";
		String eBar="eBar val";
	}
}