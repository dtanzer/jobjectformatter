package net.davidtanzer.jobjectformatter.typeinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeInfo {
	private List<ClassInfo> classInfos = new ArrayList<>();
	private boolean hasAutomaticallyFormattedToString = false;

	public List<ClassInfo> classInfos() {
		return classInfos;
	}

	public boolean hasAutomaticallyFormattedToString() {
		return hasAutomaticallyFormattedToString;
	}

	static class Builder {
		private final TypeInfoCache typeInfoCache;
		private final FieldsFilter fieldsFilter;
		private TypeInfo typeInfo;

		Builder(final TypeInfoCache typeInfoCache, final FieldsFilter fieldsFilter) {
			this.typeInfoCache = typeInfoCache;
			this.fieldsFilter = fieldsFilter;
			typeInfo = new TypeInfo();
		}

		public TypeInfo buildTypeInfo() {
			typeInfo.classInfos = Collections.unmodifiableList(typeInfo.classInfos);
			return typeInfo;
		}

		public Builder addInfoForClass(final Class<?> currentType) {
			typeInfo.classInfos.add(new ClassInfo(currentType, typeInfoCache, fieldsFilter));
			return this;
		}

		public Builder typeHasAutomaticallyFormattedToString() {
			typeInfo.hasAutomaticallyFormattedToString = true;
			return this;
		}
	}
}
