package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.AutomaticallyFormattedToString;
import net.davidtanzer.jobjectformatter.annotations.FormattedTransitively;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class TypeInfoAnnotationsTest {
	private TypeInfoCache typeInfoCache;

	@Before
	public void setup() {
		typeInfoCache = new TypeInfoCache();
	}

	@Test
	public void recognizesAFTSAnnotationOnToString() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(ContainingObject.class);

		assertTrue(typeInfo.hasAutomaticallyFormattedToString());
	}

	@Test
	public void objectWithAFTSAnnotationIsAutomaticallyTransitive() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(ContainingObject.class);

		assumeTrue(typeInfo.hasAutomaticallyFormattedToString());
		List<FieldInfo> fieldInfos = typeInfo.classInfos().get(0).fieldInfos();
		assertThat(fieldInfos, hasItem(allOf(
				hasProperty("name", is("eo")),
				hasProperty("transitive", is(FormattedTransitively.TransitiveInclude.ALLOWED)))));
	}

	private class SimpleObject {
		private String foo;
		private String bar;
	}

	private class ExtendedObject extends SimpleObject {
		private String eFoo;
		private String eBar;

		@Override
		@AutomaticallyFormattedToString
		public String toString() {
			return super.toString();
		}
	}

	private class ContainingObject {
		private String cFoo;
		private ExtendedObject eo = new ExtendedObject();

		@Override
		@AutomaticallyFormattedToString
		public String toString() {
			return super.toString();
		}
	}
}
