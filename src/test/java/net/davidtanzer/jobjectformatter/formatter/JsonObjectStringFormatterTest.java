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
package net.davidtanzer.jobjectformatter.formatter;

import net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
				.buildGroupedValuesInfo();
		final ObjectValuesInfo info = mock(ObjectValuesInfo.class);
		when(info.getValuesByClass()).thenReturn(Arrays.asList(classInfo));

		String formattedString = formatter.format(info);

		assertThat(formattedString, is("{\"Foo\": {\"foo\": \"foobar\", \"bar\": 123}}"));
	}
}