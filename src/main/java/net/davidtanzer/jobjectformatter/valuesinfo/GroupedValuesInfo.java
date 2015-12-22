package net.davidtanzer.jobjectformatter.valuesinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupedValuesInfo {
	private String groupName;
	private List<ValueInfo> values = new ArrayList<>();

	private GroupedValuesInfo() {
	}

	public String getGroupName() {
		return groupName;
	}

	public List<ValueInfo> getValues() {
		return values;
	}

	public static class Builder {
		private final GroupedValuesInfo byClassValuesInfo;

		public Builder() {
			byClassValuesInfo = new GroupedValuesInfo();
		}

		public GroupedValuesInfo buildByClassValuesInfo() {
			byClassValuesInfo.values = Collections.unmodifiableList(byClassValuesInfo.values);
			return byClassValuesInfo;
		}

		public Builder setClassName(final String className) {
			byClassValuesInfo.groupName = className;
			return this;
		}

		public Builder addFieldValue(final String name, final Object formattedFieldValue, final Class<?> fieldClass) {
			byClassValuesInfo.values.add(new ValueInfo(name, formattedFieldValue, fieldClass));
			return this;
		}
	}
}
