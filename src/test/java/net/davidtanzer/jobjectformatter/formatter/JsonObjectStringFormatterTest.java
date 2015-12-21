package net.davidtanzer.jobjectformatter.formatter;

import net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class JsonObjectStringFormatterTest {
	private JsonObjectStringFormatter formatter;

	@Before
	public void setup() throws Exception {
		formatter = new JsonObjectStringFormatter();
	}

	@Test
	public void formatsGroupedByClassesCorrectly() {
		final GroupedValuesInfo classInfo = new GroupedValuesInfo.Builder()
				.setClassName("Foo")
				.addFieldValue("foo", "foobar", String.class)
				.addFieldValue("bar", "123", Integer.class)
				.buildByClassValuesInfo();
		final ObjectValuesInfo info = new ObjectValuesInfo.Builder().addClassValues(classInfo).buildToStringInfo();

		String formattedString = formatter.format(info);

		assertThat(formattedString, is("{\"Foo\": {\"foo\": \"foobar\", \"bar\": 123}}"));
	}
}