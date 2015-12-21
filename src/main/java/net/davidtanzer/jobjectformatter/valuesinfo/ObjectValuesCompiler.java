package net.davidtanzer.jobjectformatter.valuesinfo;

import net.davidtanzer.jobjectformatter.typeinfo.ClassInfo;
import net.davidtanzer.jobjectformatter.typeinfo.FieldInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;

public class ObjectValuesCompiler {
	public ObjectValuesInfo compileToStringInfo(final TypeInfo typeInfo, final Object object) {
		ObjectValuesInfo.Builder builder = new ObjectValuesInfo.Builder();

		for(ClassInfo classInfo : typeInfo.classInfos()) {
			builder.addClassValues(compileClassValues(classInfo, object));
		}

		return builder.buildToStringInfo();
	}

	private GroupedValuesInfo compileClassValues(final ClassInfo classInfo, final Object object) {
		GroupedValuesInfo.Builder builder = new GroupedValuesInfo.Builder();
		builder.setClassName(classInfo.getClazz().getSimpleName());

		for(FieldInfo fieldInfo : classInfo.fieldInfos()) {
			Object fieldValue = fieldInfo.getFieldValue(object);
			final String formattedFieldValue = String.valueOf(fieldValue);
			builder.addFieldValue(fieldInfo.getName(), formattedFieldValue, fieldInfo.getType());
		}

		return builder.buildByClassValuesInfo();
	}
}
