package net.davidtanzer.jobjectformatter.valuesinfo;

import net.davidtanzer.jobjectformatter.annotations.FormattedFieldType;
import net.davidtanzer.jobjectformatter.annotations.Transitive;
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
				case ALL:
					builder.addFieldValue(fieldInfo.getName(), formattedFieldValue, fieldInfo.getType());
					break;
				case ANNOTATED:
					if (fieldInfo.getIncludeField().equals(FormattedFieldType.DEFAULT)) {
						builder.addFieldValue(fieldInfo.getName(), formattedFieldValue, fieldInfo.getType());
					}
					break;
				case NONE:
					break;
				default:
					throw new IllegalStateException("Unexpected formatting behavior type: " + typeInfo.getFormattingBehavior());
			}
		}
	}

	private Object formatFieldValue(final Object object, final FieldInfo fieldInfo) {
		Object fieldValue = fieldInfo.getFieldValue(object);
		Object formattedFieldValue;
		switch (fieldInfo.getTransitiveBehaviorOfTarget()) {
			case ALWAYS:
				formattedFieldValue = String.valueOf(fieldValue);
				break;
			case ALLOWED:
				if(hasFormattedAnnotation(fieldValue)) {
					ObjectValuesInfo objectValuesInfo = this.compileToStringInfo(typeInfoCache.typeInfoFor(fieldValue.getClass()), fieldValue);
					if(objectValuesInfo.getAllValues().isEmpty()) {
						formattedFieldValue = "[not null]";
					} else {
						formattedFieldValue = objectValuesInfo;
					}
				} else {
					formattedFieldValue = String.valueOf(fieldValue);
				}
				break;
			case DISALLOWED:
				if(fieldValue == null) {
					formattedFieldValue = null;
				} else {
					formattedFieldValue = "[not null]";
				}
				break;
			default:
				throw new IllegalStateException("Illegal getValue for transitive behavior: "+fieldInfo.getTransitiveBehaviorOfTarget());
		}
		return formattedFieldValue;
	}

	private Boolean hasFormattedAnnotation(final Object fieldValue) {
		return typeInfoCache.behaviorFor(fieldValue.getClass(), (f) -> true, false);
	}
}
