/*
   Copyright 2015 David Tanzer (business@davidtanzer.net / @dtanzer)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
	public void formattedRepresentationOfAnObjectContainsAllValuesInTheFormatSpecifiedByTheConfiguredFormatter() {
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