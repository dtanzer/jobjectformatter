package net.davidtanzer.jobjectformatter.formatter;

import net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ValueInfo;

import java.util.List;

public abstract class AbstractObjectStringFormatter implements ObjectStringFormatter {
	@Override
	public String format(final ObjectValuesInfo info) {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		startFormattedString(result);
		for(GroupedValuesInfo groupedValuesInfo : info.getValuesByClass()) {
			if(!first) {
				result.append(getGroupsSeparator());
			}

			startValueGroup(result, groupedValuesInfo);
			appendAllValues(result, groupedValuesInfo.getValues());
			endValueGroup(result);

			first = false;
		}
		endFormattedString(result);
		return result.toString();
	}

	private void appendAllValues(final StringBuilder result, final List<ValueInfo> values) {
		boolean first = true;
		for(ValueInfo value : values) {
			if(!first) {
				result.append(getValueSeparator());
			}
			appendSingleValue(result, value);
			first = false;
		}
	}

	protected abstract void startFormattedString(StringBuilder result);

	protected abstract void endFormattedString(StringBuilder result);

	protected abstract void startValueGroup(StringBuilder result, GroupedValuesInfo groupedValuesInfo);

	protected abstract void endValueGroup(StringBuilder result);

	private String getValueSeparator() {
		return ", ";
	}

	protected String getGroupsSeparator() {
		return getValueSeparator();
	}

	protected abstract void appendSingleValue(StringBuilder result, ValueInfo value);
}
