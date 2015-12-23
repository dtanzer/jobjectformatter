package net.davidtanzer.jobjectformatter.formatter;

import net.davidtanzer.jobjectformatter.annotations.Formatted;
import net.davidtanzer.jobjectformatter.annotations.FormattedField;
import net.davidtanzer.jobjectformatter.annotations.FormattedFieldType;
import net.davidtanzer.jobjectformatter.annotations.Transitive;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfoCache;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesCompiler;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ValueInfo;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

public class AbstractObjectStringFormatterTest {
	private AbstractObjectStringFormatter formatter;

	@Before
	public void setup() throws Exception {
		formatter = new AbstractObjectStringFormatter() {
			@Override
			protected void appendSingleValue(final StringBuilder result, final ValueInfo value) {
				result.append(value.getPropertyName()).append("=").append(value.getValue());
			}

			@Override
			protected void startFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
				result.append("{ ");
			}

			@Override
			protected void endFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
				result.append(" }");
			}
		};
	}

	@Test
	public void addsPropertyValueForSimpleProperty() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainingObject.class);
		final ObjectValuesInfo info = new ObjectValuesCompiler().compileToStringInfo(typeInfo, new ContainingObject());

		final String formattedString = formatter.format(info);

		assertThat(formattedString, containsString("foobar=foobar value"));
	}

	@Test
	public void addsPropertyValueForTransitiveProperty() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainingObject.class);
		final ObjectValuesInfo info = new ObjectValuesCompiler().compileToStringInfo(typeInfo, new ContainingObject());

		final String formattedString = formatter.format(info);

		assertThat(formattedString, containsString("containedObject={ foo=foo }"));
	}

	public class ContainedObject {
		@FormattedField(transitive = FormattedFieldType.DEFAULT)
		private String foo = "foo";
		private String bar = "bar";
	}

	public class ContainingObject {
		private String foobar = "foobar value";
		@Formatted(transitive = Transitive.ALLOWED)
		private ContainedObject containedObject = new ContainedObject();
	}
}