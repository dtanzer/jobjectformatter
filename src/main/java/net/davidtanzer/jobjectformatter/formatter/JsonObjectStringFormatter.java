package net.davidtanzer.jobjectformatter.formatter;

import net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ValueInfo;

import java.util.HashSet;
import java.util.Set;

public class JsonObjectStringFormatter extends AbstractObjectStringFormatter {
	private static final Set<Class<?>> unescapedTypes = new HashSet<Class<?>>() {{
		add(Integer.class);
		add(Short.class);
		add(Float.class);
		add(Double.class);
		add(Boolean.class);

		add(int.class);
		add(short.class);
		add(float.class);
		add(double.class);
		add(boolean.class);
	}};

	@Override
	protected void appendSingleValue(final StringBuilder result, final ValueInfo value) {
		result.append("\"").append(value.getPropertyName()).append("\": ");
		if(!unescapedTypes.contains(value.getFieldClass())) {
			result.append("\"");
		}
		result.append(value.getValue());
		if(!unescapedTypes.contains(value.getFieldClass())) {
			result.append("\"");
		}
	}

	@Override
	protected void startFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		result.append("{");
	}

	@Override
	protected void endFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		result.append("}");
	}

	@Override
	protected void startValueGroup(final StringBuilder result, final GroupedValuesInfo groupedValuesInfo) {
		result.append("\"").append(groupedValuesInfo.getGroupName()).append("\": {");
	}

	@Override
	protected void endValueGroup(final StringBuilder result) {
		result.append("}");
	}
}
