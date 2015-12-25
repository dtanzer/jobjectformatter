package net.davidtanzer.jobjectformatter.valuesinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupedValuesInfo {
	private final String groupName;
	private final List<ValueInfo> values;

	private GroupedValuesInfo(final String groupName, final List<ValueInfo> values) {
		this.groupName = groupName;
		this.values = values;
	}

	public String getGroupName() {
		return groupName;
	}

	public List<ValueInfo> getValues() {
		return values;
	}

	public static class Builder {
		private String groupName;
		private final List<ValueInfo> values = new ArrayList<>();

		public GroupedValuesInfo buildByClassValuesInfo() {
			return new GroupedValuesInfo(groupName, Collections.unmodifiableList(values));
		}

		public Builder setClassName(final String className) {
			this.groupName = className;
			return this;
		}

		public Builder addFieldValue(final String name, final Object formattedFieldValue, final Class<?> fieldClass) {
			values.add(new ValueInfo(name, formattedFieldValue, fieldClass));
			return this;
		}
	}
}
