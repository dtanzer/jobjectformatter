package net.davidtanzer.jobjectformatter.valuesinfo;

import net.davidtanzer.jobjectformatter.annotations.Transitive;
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
			String formattedFieldValue = "";
			switch (fieldInfo.getTransitive()) {
				case ALWAYS:
					formattedFieldValue = String.valueOf(fieldValue);
					break;
				case ALLOWED:
					formattedFieldValue = String.valueOf(fieldValue);
					break;
				case DISALLOWED:
					if(fieldValue == null) {
						formattedFieldValue = null;
					} else {
						formattedFieldValue = "[not null]";
					}
					break;
				default:
					throw new IllegalStateException("Illegal value for transitive behavior: "+fieldInfo.getTransitive());
			}

			builder.addFieldValue(fieldInfo.getName(), formattedFieldValue, fieldInfo.getType());
		}

		return builder.buildByClassValuesInfo();
	}
}
