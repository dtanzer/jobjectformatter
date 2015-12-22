package net.davidtanzer.jobjectformatter.formatter;

import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ValueInfo;

public class SimpleFormatter extends AbstractObjectStringFormatter {
	@Override
	protected void startFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		result.append("{ ");
	}

	@Override
	protected void endFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		result.append(" }");
	}

	@Override
	protected void appendSingleValue(final StringBuilder result, final ValueInfo value) {
		result.append(value.propertyName()).append("=").append(value.value());
	}
}
