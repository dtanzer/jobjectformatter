package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.Formatted;
import net.davidtanzer.jobjectformatter.annotations.Transitive;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
	public void usesClassInfoFromCache_IfAvailable() {
		final TypeInfo typeInfo1 = typeInfoCache.typeInfoFor(ExtendedObject.class);
		final TypeInfo typeInfo2 = typeInfoCache.typeInfoFor(ExtendedObject.class);

		assertSame(typeInfo1, typeInfo2);
	}

	@Test
	public void usesFieldsFilter_ToActuallyGetTheRelevantFields() throws NoSuchFieldException {
		final FieldsFilter fieldsFilter = mock(FieldsFilter.class);
		typeInfoCache = new TypeInfoCache(fieldsFilter);

		when(fieldsFilter.getFilteredFields(SimpleObject.class, typeInfoCache)).thenReturn(Arrays.asList(
				new FieldInfo(ObjectWithOtherFields.class.getDeclaredField("field1"), Transitive.ALLOWED),
				new FieldInfo(ObjectWithOtherFields.class.getDeclaredField("field2"), Transitive.ALLOWED)
		));

		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(SimpleObject.class);

		assertThat(typeInfo.classInfos().get(0).fieldInfos(), containsInAnyOrder(
				hasProperty("name", is("field1")),
				hasProperty("name", is("field2"))
		));
	}

	@Test
	public void transitiveBehaviorForUnannotatedClass_IsDisallowed() {
		final Transitive transitive = typeInfoCache.transitiveBehaviorFor(SimpleObject.class);

		assertThat(transitive, is(Transitive.DISALLOWED));
	}

	@Test
	public void transitiveBehaviorForAnnotatedClass_IsDeterminedByAnnotationValue() {
		final Transitive transitive = typeInfoCache.transitiveBehaviorFor(TransitiveAnnotatedClass.class);

		assertThat(transitive, is(Transitive.ALWAYS));
	}

	@Test
	public void transitiveBehaviorForAnnotatedToString_IsDeterminedByAnnotationValue() {
		final Transitive transitive = typeInfoCache.transitiveBehaviorFor(TransitiveAnnotatedToString.class);

		assertThat(transitive, is(Transitive.ALWAYS));
	}

	@Test
	public void transitiveBehaviorForAnnotatedClass_IsMoreImportantThanAnnotationOnClass() {
		final Transitive transitive = typeInfoCache.transitiveBehaviorFor(TransitiveAnnotatedBoth.class);

		assertThat(transitive, is(Transitive.DISALLOWED));
	}

	private class SimpleObject {
		private String foo;
		private String bar;
	}

	private class ExtendedObject extends SimpleObject {
		private String eFoo;
		private String eBar;
	}

	private static class ObjectWithOtherFields {
		private String field1;
		private String field2;
	}

	@Formatted(transitive = Transitive.ALWAYS)
	private class TransitiveAnnotatedClass {
	}

	private class TransitiveAnnotatedToString {
		@Override
		@Formatted(transitive = Transitive.ALWAYS)
		public String toString() {
			return super.toString();
		}
	}

	@Formatted(transitive = Transitive.DISALLOWED)
	private class TransitiveAnnotatedBoth {
		@Override
		@Formatted(transitive = Transitive.ALWAYS)
		public String toString() {
			return super.toString();
		}
	}
}