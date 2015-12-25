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

import net.davidtanzer.jobjectformatter.formatter.ObjectStringFormatter;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfoCache;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesCompiler;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FormattedStringGeneratorTest {
	private FormattedStringGenerator formattedStringGenerator;
	private TypeInfoCache typeInfoCache;
	private ObjectValuesCompiler objectValuesCompiler;
	private ObjectStringFormatter toStringFormatter;

	@Before
	public void setup() {
		typeInfoCache = mock(TypeInfoCache.class);
		objectValuesCompiler = mock(ObjectValuesCompiler.class);
		toStringFormatter = mock(ObjectStringFormatter.class);
		formattedStringGenerator = new FormattedStringGenerator(toStringFormatter, typeInfoCache, objectValuesCompiler);
	}

	@Test
	public void retrievesTypeInformationFromTypeInfoCacheWhenGeneratingToString() {
		formattedStringGenerator.format(new SimpleObject());

		verify(typeInfoCache).typeInfoFor(SimpleObject.class);
	}

	@Test
	public void usesInfoCompilerToCompileValuesFromObjectAndTypeInfo() {
		final TypeInfo typeInfo = mock(TypeInfo.class);
		when(typeInfoCache.typeInfoFor(any())).thenReturn(typeInfo);

		SimpleObject object = new SimpleObject();
		formattedStringGenerator.format(object);

		verify(objectValuesCompiler).compileToStringInfo(typeInfo, object);
	}

	@Test
	public void usesToStringFormatterToFormatTheToStringInfo() {
		when(toStringFormatter.format(any())).thenReturn("Formatted String");

		final String stringValue = formattedStringGenerator.format(new SimpleObject());

		assertThat(stringValue, is("Formatted String"));
	}

	private class SimpleObject {
	}
}