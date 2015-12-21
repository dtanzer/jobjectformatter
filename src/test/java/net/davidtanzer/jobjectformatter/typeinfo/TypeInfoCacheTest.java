package net.davidtanzer.jobjectformatter.typeinfo;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;

public class TypeInfoCacheTest {
	private TypeInfoCache typeInfoCache;

	@Before
	public void setup() throws Exception {
		typeInfoCache = new TypeInfoCache();
	}

	@Test
	public void addsClassInfo_ForEveryClassInTheHierarchy() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(ExtendedObject.class);

		assertThat(typeInfo.classInfos(), contains(
				hasProperty("clazz", is((Object) ExtendedObject.class)),
				hasProperty("clazz", is((Object) SimpleObject.class))));
	}

	@Test
	public void classInfosOfTypeInfo_AreInTheCorrectOrder() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(ExtendedObject.class);

		final ClassInfo extendedObjectInfo = typeInfo.classInfos().get(0);
		assertThat(extendedObjectInfo.getClazz(), is((Object) ExtendedObject.class));
	}

	@Test
	public void classInfo_ContainsAllFieldsFromTheClass() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(ExtendedObject.class);

		final ClassInfo extendedObjectInfo = typeInfo.classInfos().get(0);
		assumeThat(extendedObjectInfo.getClazz(), is((Object) ExtendedObject.class));
		assertThat(extendedObjectInfo.fieldInfos(), containsInAnyOrder(
				hasProperty("name", is("eFoo")),
				hasProperty("name", is("eBar"))
		));
	}

	@Test
	public void classInfo_DoesNotContainFieldsFromOtherClasses() {
		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(ExtendedObject.class);

		final ClassInfo extendedObjectInfo = typeInfo.classInfos().get(0);
		assumeThat(extendedObjectInfo.getClazz(), is((Object) ExtendedObject.class));
		assertThat(extendedObjectInfo.fieldInfos(), not(hasItem(
				hasProperty("name", is("foo"))
		)));
	}

	@Test
	public void usesClassInfoFromCach_IfAvailable() {
		final TypeInfo typeInfo1 = typeInfoCache.typeInfoFor(ExtendedObject.class);
		final TypeInfo typeInfo2 = typeInfoCache.typeInfoFor(ExtendedObject.class);

		assertSame(typeInfo1, typeInfo2);
	}

	private class SimpleObject {
		private String foo;
		private String bar;
	}

	private class ExtendedObject extends SimpleObject {
		private String eFoo;
		private String eBar;
	}
}