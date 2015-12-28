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

import net.davidtanzer.jobjectformatter.annotations.Formatted;
import net.davidtanzer.jobjectformatter.annotations.FormattedField;
import net.davidtanzer.jobjectformatter.annotations.FormattedFieldType;
import net.davidtanzer.jobjectformatter.annotations.TransitiveInclude;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfoCache;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesCompiler;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ValueInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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
	public void callsStartFormattedStringAtTheBeginningOfProcessing() {
		final AbstractObjectStringFormatter formatterSpy = spy(formatter);

		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainedObject.class);
		final ObjectValuesInfo info = new ObjectValuesCompiler().compileToStringInfo(typeInfo, new ContainedObject());
		formatterSpy.format(info);

		verify(formatterSpy).startFormattedString(any(), any());
	}

	@Test
	public void passesTheOriginalValuesInfoObjectToStartFormattedString() {
		final AbstractObjectStringFormatter formatterSpy = spy(formatter);

		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainedObject.class);
		final ObjectValuesInfo info = new ObjectValuesCompiler().compileToStringInfo(typeInfo, new ContainedObject());
		formatterSpy.format(info);

		verify(formatterSpy).startFormattedString(any(), argThat(is(info)));
	}

	@Test
	public void callsEndFormattedStringAfterProcessingEverythingInObjectValueInfo() {
		final AbstractObjectStringFormatter formatterSpy = spy(formatter);

		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainedObject.class);
		final ObjectValuesInfo info = new ObjectValuesCompiler().compileToStringInfo(typeInfo, new ContainedObject());
		formatterSpy.format(info);

		verify(formatterSpy).endFormattedString(any(), argThat(is(info)));
	}

	@Test
	public void callsStartValueGroupForEveryValueGroupInTheObjectInfoWhenValueGroupingIsEnabled() {
		final AbstractObjectStringFormatter formatterSpy = spy(formatter);

		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainedObject.class);
		final ObjectValuesInfo info = new ObjectValuesCompiler().compileToStringInfo(typeInfo, new ContainedObject());
		formatterSpy.format(info);

		verify(formatterSpy).startValueGroup(any(), argThat(hasProperty("groupName", is("ContainedObject"))));
	}

	@Test
	public void callsEndValueGroupAfterEveryValueGroupInTheObjectWhenValueGroupingIsEnabled() {
		final AbstractObjectStringFormatter formatterSpy = spy(formatter);

		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainedObject.class);
		final ObjectValuesInfo info = new ObjectValuesCompiler().compileToStringInfo(typeInfo, new ContainedObject());
		formatterSpy.format(info);

		verify(formatterSpy).endValueGroup(any());
	}

	@Test
	public void callsAppendSingleValueForEveryValueFromTheObjectValuesInfo() {
		final AbstractObjectStringFormatter formatterSpy = spy(formatter);

		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainedObject.class);
		final ObjectValuesInfo info = new ObjectValuesCompiler().compileToStringInfo(typeInfo, new ContainedObject());
		formatterSpy.format(info);

		verify(formatterSpy).appendSingleValue(any(), argThat(hasProperty("propertyName", is("foo"))));
		verify(formatterSpy).appendSingleValue(any(), argThat(hasProperty("propertyName", is("bar"))));
	}

	@Test
	public void addsToStringValueForSimpleProperty() {
		final TypeInfo typeInfo = new TypeInfoCache().typeInfoFor(ContainingObject.class);
		final ObjectValuesInfo info = new ObjectValuesCompiler().compileToStringInfo(typeInfo, new ContainingObject());

		final String formattedString = formatter.format(info);

		assertThat(formattedString, containsString("foobar=foobar value"));
	}

	@Test
	public void addsFormattedPropertyValueForTransitiveProperty() {
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
		@Formatted(transitive = TransitiveInclude.ANNOTADED_FIELDS)
		private ContainedObject containedObject = new ContainedObject();
	}
}