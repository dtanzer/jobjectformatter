package net.davidtanzer.jobjectformatter.formatter;

import net.davidtanzer.jobjectformatter.valuesinfo.GroupedValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ValueInfo;

public class ConfigurableObjectStringFormatter extends AbstractObjectStringFormatter {
	public static final ConfigurableObjectStringFormatter UNGROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME = new ConfigurableObjectStringFormatter("", "{", "}", ", ", "", "", "=\"", "\"", FormatGrouped.NO, DisplayClassName.ALWAYS);
	public static final ConfigurableObjectStringFormatter GROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME = new ConfigurableObjectStringFormatter("", "", "}", ", ", "{", "}", "=\"", "\"", FormatGrouped.BY_CLASS, DisplayClassName.NEVER);

	private final String beforeClassName;
	private final String afterClassName;
	private final String endOfString;
	private final String valueSeparator;
	private final String startOfGroup;
	private final String endOfGroup;
	private final FormatGrouped formatGrouped;
	private final DisplayClassName displayClassName;
	private final String beforeValue;
	private final String afterValue;

	public ConfigurableObjectStringFormatter(final String beforeClassName, final String afterClassName, final String endOfString, final String valueSeparator, final String startOfGroup, final String endOfGroup, final String beforeValue, final String afterValue, final FormatGrouped formatGrouped, final DisplayClassName displayClassName) {
		super(formatGrouped);

		this.beforeClassName = beforeClassName;
		this.afterClassName = afterClassName;
		this.endOfString = endOfString;
		this.valueSeparator = valueSeparator;
		this.startOfGroup = startOfGroup;
		this.endOfGroup = endOfGroup;
		this.beforeValue = beforeValue;
		this.afterValue = afterValue;
		this.formatGrouped = formatGrouped;
		this.displayClassName = displayClassName;
	}

	@Override
	protected void appendSingleValue(final StringBuilder result, final ValueInfo value) {
		result.append(value.getPropertyName()).append(beforeValue).append(value.getValue()).append(afterValue);
	}

	@Override
	protected void startFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		if(displayClassName == DisplayClassName.ALWAYS ||
				(displayClassName == DisplayClassName.WHEN_NOT_GROUPED_BY_CLASS && formatGrouped != FormatGrouped.BY_CLASS)) {
			result.append(beforeClassName).append(info.getType().getSimpleName());
		}
		result.append(afterClassName);
	}

	@Override
	protected void endFormattedString(final StringBuilder result, final ObjectValuesInfo info) {
		result.append(endOfString);
	}

	@Override
	protected void startValueGroup(final StringBuilder result, final GroupedValuesInfo groupedValuesInfo) {
		result.append(groupedValuesInfo.getGroupName()).append(startOfGroup);
	}

	@Override
	protected void endValueGroup(final StringBuilder result) {
		result.append(endOfGroup);
	}

	@Override
	protected String getValueSeparator() {
		return valueSeparator;
	}
}
