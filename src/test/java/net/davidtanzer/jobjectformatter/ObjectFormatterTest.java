package net.davidtanzer.jobjectformatter;

import net.davidtanzer.jobjectformatter.formatter.JsonObjectStringFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ObjectFormatterTest {
	@Before
	public void setup() {
		ObjectFormatter.configureGenerator(new FormattedStringGenerator(new JsonObjectStringFormatter()));
	}

	@Test
	public void toStringOfClassProducesNiceOutput() {
		final String formatted = new ExtendedObject().toString();

		assertThat(formatted, is("{\"ExtendedObject\": {\"eFoo\": \"eFoo val\", \"eBar\": false}, \"SimpleObject\": {\"foo\": \"foo val\", \"bar\": 1}}"));
	}

	private class SimpleObject {
		String foo="foo val";
		int bar=1;
	}

	private class ExtendedObject extends SimpleObject {
		String eFoo="eFoo val";
		Boolean eBar=Boolean.FALSE;

		@Override
		public String toString() {
			return ObjectFormatter.format(this);
		}
	}

}