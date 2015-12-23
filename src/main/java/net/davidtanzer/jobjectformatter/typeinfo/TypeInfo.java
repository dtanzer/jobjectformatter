package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.FormattedType;
import net.davidtanzer.jobjectformatter.annotations.Transitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeInfo {
	private final List<ClassInfo> classInfos;
	private final Transitive transitiveBehavior;
	private final FormattedType formattingBehavior;

	private TypeInfo(final List<ClassInfo> classInfos, final Transitive transitiveBehavior, final FormattedType formattingBehavior) {
		this.classInfos = classInfos;
		this.transitiveBehavior = transitiveBehavior;
		this.formattingBehavior = formattingBehavior;
	}

	public List<ClassInfo> classInfos() {
		return classInfos;
	}

	public Transitive getTransitiveBehavior() {
		return transitiveBehavior;
	}

	public FormattedType getFormattingBehavior() {
		return formattingBehavior;
	}

	TypeInfo withTransitiveBehavior(final Transitive transitiveBehavior) {
		return new TypeInfo(this.classInfos, transitiveBehavior, this.formattingBehavior);
	}

	static class Builder {
		private final TypeInfoCache typeInfoCache;
		private final FieldsFilter fieldsFilter;
		private FormattedType formattingBehavior;
		private Transitive transitiveBehavior;
		private List<ClassInfo> classInfos = new ArrayList<>();

		Builder(final TypeInfoCache typeInfoCache, final FieldsFilter fieldsFilter) {
			this.typeInfoCache = typeInfoCache;
			this.fieldsFilter = fieldsFilter;
		}

		public TypeInfo buildTypeInfo() {
			return new TypeInfo(Collections.unmodifiableList(this.classInfos), this.transitiveBehavior, this.formattingBehavior);
		}

		public Builder withFormattingBehavior(final FormattedType formattingBehavior, final Transitive transitiveBehavior) {
			this.formattingBehavior = formattingBehavior;
			this.transitiveBehavior = transitiveBehavior;
			return this;
		}

		public Builder addInfoForClass(final Class<?> currentType) {
			this.classInfos.add(new ClassInfo(currentType, typeInfoCache, fieldsFilter));
			return this;
		}
	}
}
