package net.davidtanzer.jobjectformatter.typeinfo;

import net.davidtanzer.jobjectformatter.annotations.FormattedInclude;
import net.davidtanzer.jobjectformatter.annotations.TransitiveInclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeInfo {
	private final List<ClassInfo> classInfos;
	private final TransitiveInclude transitiveInclude;
	private final FormattedInclude formattingBehavior;

	private TypeInfo(final List<ClassInfo> classInfos, final TransitiveInclude transitiveInclude, final FormattedInclude formattingBehavior) {
		this.classInfos = classInfos;
		this.transitiveInclude = transitiveInclude;
		this.formattingBehavior = formattingBehavior;
	}

	public List<ClassInfo> classInfos() {
		return classInfos;
	}

	public TransitiveInclude getTransitiveInclude() {
		return transitiveInclude;
	}

	public FormattedInclude getFormattingBehavior() {
		return formattingBehavior;
	}

	TypeInfo withTransitiveBehavior(final TransitiveInclude transitiveInclude) {
		return new TypeInfo(this.classInfos, transitiveInclude, this.formattingBehavior);
	}

	static class Builder {
		private final TypeInfoCache typeInfoCache;
		private final FieldsFilter fieldsFilter;
		private FormattedInclude formattingBehavior;
		private TransitiveInclude transitiveInclude;
		private List<ClassInfo> classInfos = new ArrayList<>();

		Builder(final TypeInfoCache typeInfoCache, final FieldsFilter fieldsFilter) {
			this.typeInfoCache = typeInfoCache;
			this.fieldsFilter = fieldsFilter;
		}

		public TypeInfo buildTypeInfo() {
			return new TypeInfo(Collections.unmodifiableList(this.classInfos), this.transitiveInclude, this.formattingBehavior);
		}

		public Builder withFormattingBehavior(final FormattedInclude formattingBehavior, final TransitiveInclude transitiveInclude) {
			this.formattingBehavior = formattingBehavior;
			this.transitiveInclude = transitiveInclude;
			return this;
		}

		public Builder addInfoForClass(final Class<?> currentType) {
			this.classInfos.add(new ClassInfo(currentType, typeInfoCache, fieldsFilter));
			return this;
		}
	}
}
