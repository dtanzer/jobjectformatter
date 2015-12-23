package net.davidtanzer.jobjectformatter.valuesinfo;

import net.davidtanzer.jobjectformatter.annotations.FormattedFieldType;
import net.davidtanzer.jobjectformatter.annotations.TransitiveInclude;
import net.davidtanzer.jobjectformatter.typeinfo.ClassInfo;
import net.davidtanzer.jobjectformatter.typeinfo.FieldInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfoCache;

import java.util.function.Predicate;

public class ObjectValuesCompiler {
	private final TypeInfoCache typeInfoCache;

	public ObjectValuesCompiler() {
		this(new TypeInfoCache());
	}

	public ObjectValuesCompiler(final TypeInfoCache typeInfoCache) {
		this.typeInfoCache = typeInfoCache;
	}

	public ObjectValuesInfo compileToStringInfo(final TypeInfo typeInfo, final Object object) {
		return compileToStringInfo(typeInfo, object, t -> true);
	}

	private ObjectValuesInfo compileToStringInfo(final TypeInfo typeInfo, final Object object, final Predicate<FormattedFieldType> includeInTransitive) {
		ObjectValuesInfo.Builder builder = new ObjectValuesInfo.Builder();

		for(ClassInfo classInfo : typeInfo.classInfos()) {
			builder.addClassValues(compileClassValues(typeInfo, classInfo, object, includeInTransitive));
		}

		return builder.buildToStringInfo();
	}

	private GroupedValuesInfo compileClassValues(final TypeInfo typeInfo, final ClassInfo classInfo, final Object object, final Predicate<FormattedFieldType> includeInTransitive) {
		GroupedValuesInfo.Builder builder = new GroupedValuesInfo.Builder();
		builder.setClassName(classInfo.getClazz().getSimpleName());

		for(FieldInfo fieldInfo : classInfo.fieldInfos()) {
			formatFieldValueIfNecessary(typeInfo, object, builder, fieldInfo, includeInTransitive);
		}

		return builder.buildByClassValuesInfo();
	}

	private void formatFieldValueIfNecessary(final TypeInfo typeInfo, final Object object, final GroupedValuesInfo.Builder builder, final FieldInfo fieldInfo, final Predicate<FormattedFieldType> includeInTransitive) {
		if(includeInTransitive.test(fieldInfo.getIncludeFieldInTransitive())) {
			Object formattedFieldValue = formatFieldValue(object, fieldInfo);
			switch (typeInfo.getFormattingBehavior()) {
				case ALL_FIELDS:
					builder.addFieldValue(fieldInfo.getName(), formattedFieldValue, fieldInfo.getType());
					break;
				case ANNOTATED_FIELDS:
					if (fieldInfo.getIncludeField().equals(FormattedFieldType.DEFAULT)) {
						builder.addFieldValue(fieldInfo.getName(), formattedFieldValue, fieldInfo.getType());
					}
					break;
				case NO_FIELDS:
					break;
				default:
					throw new IllegalStateException("Unexpected formatting behavior type: " + typeInfo.getFormattingBehavior());
			}
		}
	}

	private Object formatFieldValue(final Object object, final FieldInfo fieldInfo) {
		Object fieldValue = fieldInfo.getFieldValue(object);
		Object formattedFieldValue;
		switch (fieldInfo.getTransitiveIncludeOfTarget()) {
			case ALL_FIELDS:
				formattedFieldValue = fieldValue;
				break;
			case ANNOTADED_FIELDS:
				if(hasFormattedAnnotation(fieldValue, fieldInfo)) {
					TypeInfo transitiveTypeInfo = typeInfoCache.typeInfoFor(fieldValue.getClass(), fieldInfo.getTransitiveIncludeOfTarget());
					ObjectValuesInfo objectValuesInfo = this.compileToStringInfo(
							transitiveTypeInfo, fieldValue, transitive -> transitive.equals(FormattedFieldType.DEFAULT));
					if(objectValuesInfo.getAllValues().isEmpty()) {
						formattedFieldValue = "[not null]";
					} else {
						formattedFieldValue = objectValuesInfo;
					}
				} else {
					formattedFieldValue = fieldValue;
				}
				break;
			case NO_FIELDS:
				if(fieldValue == null) {
					formattedFieldValue = null;
				} else {
					formattedFieldValue = "[not null]";
				}
				break;
			default:
				throw new IllegalStateException("Illegal getValue for transitive behavior: "+fieldInfo.getTransitiveIncludeOfTarget());
		}
		return formattedFieldValue;
	}

	private Boolean hasFormattedAnnotation(final Object fieldValue, final FieldInfo fieldInfo) {
		return typeInfoCache.behaviorFor(fieldValue.getClass(), (f) -> true, false)
				|| fieldInfo.getTransitiveIncludeOfTarget().equals(TransitiveInclude.ANNOTADED_FIELDS)
				|| fieldInfo.getTransitiveIncludeOfTarget().equals(TransitiveInclude.ALL_FIELDS);
	}
}
