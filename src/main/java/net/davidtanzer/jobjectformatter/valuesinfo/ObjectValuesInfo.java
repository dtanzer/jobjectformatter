package net.davidtanzer.jobjectformatter.valuesinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectValuesInfo {
	private final List<GroupedValuesInfo> valuesByClass;
	private final List<ValueInfo> allValues;

	private ObjectValuesInfo(final List<GroupedValuesInfo> valuesByClass, final List<ValueInfo> allValues) {
		this.valuesByClass = valuesByClass;
		this.allValues = allValues;
	}

	public List<GroupedValuesInfo> getValuesByClass() {
		return valuesByClass;
	}

	public List<ValueInfo> getAllValues() {
		return allValues;
	}

	public static class Builder {
		private final List<GroupedValuesInfo> valuesByClass = new ArrayList<>();
		private final List<ValueInfo> allValues = new ArrayList<>();

		public ObjectValuesInfo buildToStringInfo() {
			return new ObjectValuesInfo(
					Collections.unmodifiableList(valuesByClass),
					Collections.unmodifiableList(allValues));
		}

		public Builder addClassValues(final GroupedValuesInfo groupedValuesInfo) {
			valuesByClass.add(groupedValuesInfo);
			allValues.addAll(groupedValuesInfo.getValues());
			return this;
		}
	}
}
