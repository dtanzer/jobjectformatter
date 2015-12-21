package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.Formatted;
import net.davidtanzer.jobjectformatter.annotations.Transitive;
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
				hasProperty("transitive", is(Transitive.ALLOWED)))));

		assertThat(fieldInfos, hasItem(allOf(
				hasProperty("name", is("bar")),
				hasProperty("transitive", is(Transitive.ALLOWED)))));
	}

	@Test
	public void typeInfosForOtherDependenciesAreNotTransitiveByDefault() {
		final TypeInfo info = typeInfoCache.typeInfoFor(DependentObject.class);

		List<FieldInfo> fieldInfos = info.classInfos().get(0).fieldInfos();
		assertThat(fieldInfos, hasItem(allOf(
				hasProperty("name", is("so")),
				hasProperty("transitive", is(Transitive.DISALLOWED)))));
	}

	@Test
	public void typeInfosFromJavaPackages_CanBeSetToNonTransitive_ByAnnotation() {
		final TypeInfo info = typeInfoCache.typeInfoFor(SimpleObject.class);

		List<FieldInfo> fieldInfos = info.classInfos().get(0).fieldInfos();
		assertThat(fieldInfos, hasItem(allOf(
				hasProperty("name", is("notTransitive")),
				hasProperty("transitive", is(Transitive.DISALLOWED)))));
	}

	@Test
	public void typeInfosFromOtherPackages_CanBeSetToTransitive_ByAnnotation() {
		final TypeInfo info = typeInfoCache.typeInfoFor(DependentObject.class);

		List<FieldInfo> fieldInfos = info.classInfos().get(0).fieldInfos();
		assertThat(fieldInfos, hasItem(allOf(
				hasProperty("name", is("soTransitive")),
				hasProperty("transitive", is(Transitive.ALLOWED)))));
	}

	private class SimpleObject {
		int foo;
		String bar;

		@Formatted(transitive = Transitive.DISALLOWED)
		String notTransitive;
	}

	private class DependentObject {
		private SimpleObject so;
		@Formatted
		private SimpleObject soTransitive;
	}
}
