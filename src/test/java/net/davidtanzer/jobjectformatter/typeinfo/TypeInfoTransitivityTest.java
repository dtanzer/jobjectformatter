package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.FormattedTransitively;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TypeInfoTransitivityTest {
	private TypeInfoCache typeInfoCache;

	@Before
	public void setup() {
		typeInfoCache = new TypeInfoCache();
	}

	@Test
	public void allTypeInfosFromJavaPackagesAreTransitive() {
		final TypeInfo info = typeInfoCache.typeInfoFor(SimpleObject.class);

		List<FieldInfo> fieldInfos = info.classInfos().get(0).fieldInfos();
		assertThat(fieldInfos, hasItem(allOf(
				hasProperty("name", is("foo")),
				hasProperty("transitive", is(FormattedTransitively.TransitiveInclude.ALLOWED)))));

		assertThat(fieldInfos, hasItem(allOf(
				hasProperty("name", is("bar")),
				hasProperty("transitive", is(FormattedTransitively.TransitiveInclude.ALLOWED)))));
	}

	@Test
	public void typeInfosForOtherDependenciesAreNotTransitiveByDefault() {
		final TypeInfo info = typeInfoCache.typeInfoFor(DependentObject.class);

		List<FieldInfo> fieldInfos = info.classInfos().get(0).fieldInfos();
		assertThat(fieldInfos, hasItem(allOf(
				hasProperty("name", is("so")),
				hasProperty("transitive", is(FormattedTransitively.TransitiveInclude.DISALLOWED)))));
	}

	@Test
	public void typeInfosFromJavaPackages_CanBeSetToNonTransitive_ByAnnotation() {
		final TypeInfo info = typeInfoCache.typeInfoFor(SimpleObject.class);

		List<FieldInfo> fieldInfos = info.classInfos().get(0).fieldInfos();
		assertThat(fieldInfos, hasItem(allOf(
				hasProperty("name", is("notTransitive")),
				hasProperty("transitive", is(FormattedTransitively.TransitiveInclude.DISALLOWED)))));
	}

	@Test
	public void typeInfosFromOtherPackages_CanBeSetToTransitive_ByAnnotation() {
		final TypeInfo info = typeInfoCache.typeInfoFor(DependentObject.class);

		List<FieldInfo> fieldInfos = info.classInfos().get(0).fieldInfos();
		assertThat(fieldInfos, hasItem(allOf(
				hasProperty("name", is("soTransitive")),
				hasProperty("transitive", is(FormattedTransitively.TransitiveInclude.ALLOWED)))));
	}

	private class SimpleObject {
		int foo;
		String bar;

		@FormattedTransitively(FormattedTransitively.TransitiveInclude.DISALLOWED)
		String notTransitive;
	}

	private class DependentObject {
		private SimpleObject so;
		@FormattedTransitively
		private SimpleObject soTransitive;
	}
}
