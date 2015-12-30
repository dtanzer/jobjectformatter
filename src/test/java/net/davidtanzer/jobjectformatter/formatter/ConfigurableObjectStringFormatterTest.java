package net.davidtanzer.jobjectformatter.formatter;

import net.davidtanzer.jobjectformatter.FormattedStringGenerator;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurableObjectStringFormatterTest {
	@Test
	public void ungroupedCurlyBracedOutputWithClassNameTest() {
		final ConfigurableObjectStringFormatter formatter = ConfigurableObjectStringFormatter.UNGROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME;
		FormattedStringGenerator generator = new FormattedStringGenerator(formatter);

		final String formattedString = generator.format(new DerivedClass());

		assertThat(formattedString, is("DerivedClass{baz=\"1\", foobar=\"foobar\", foo=\"foo\", bar=\"bar\"}"));
	}

	@Test
	public void groupedCurlyBracedOutputWithClassNameTest() {
		final ConfigurableObjectStringFormatter formatter = ConfigurableObjectStringFormatter.GROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME;
		FormattedStringGenerator generator = new FormattedStringGenerator(formatter);

		final String formattedString = generator.format(new DerivedClass());

		assertThat(formattedString, is("DerivedClass{baz=\"1\", foobar=\"foobar\"}, BaseClass{foo=\"foo\", bar=\"bar\"}}"));
	}

	public class BaseClass {
		public String foo="foo";
		public String bar="bar";
	}

	public class DerivedClass extends BaseClass {
		public int baz=1;
		public String foobar = "foobar";
	}
}