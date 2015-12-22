package net.davidtanzer.jobjectformatter.valuesinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectValuesInfo {
	private List<GroupedValuesInfo> valuesByClass = new ArrayList<>();
	private List<ValueInfo> allValues = new ArrayList<>();

	private ObjectValuesInfo() {
	}

	public List<GroupedValuesInfo> getValuesByClass() {
		return valuesByClass;
	}

	public List<ValueInfo> getAllValues() {
		return allValues;
	}

	public static class Builder {
		private final ObjectValuesInfo objectValuesInfo;

		public Builder() {
			objectValuesInfo = new ObjectValuesInfo();
		}

		public ObjectValuesInfo buildToStringInfo() {
			objectValuesInfo.valuesByClass = Collections.unmodifiableList(objectValuesInfo.valuesByClass);
			objectValuesInfo.allValues = Collections.unmodifiableList(objectValuesInfo.allValues);
			return objectValuesInfo;
		}

		public Builder addClassValues(final GroupedValuesInfo groupedValuesInfo) {
			objectValuesInfo.valuesByClass.add(groupedValuesInfo);
			objectValuesInfo.allValues.addAll(groupedValuesInfo.getValues());
			return this;
		}
	}
}
