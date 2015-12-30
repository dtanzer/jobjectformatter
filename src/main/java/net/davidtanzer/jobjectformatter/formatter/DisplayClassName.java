package net.davidtanzer.jobjectformatter.formatter;

/**
 * Enum to configure when to output the class name to the formatted string.
 */
public enum DisplayClassName {
	/**
	 * Never output the class name.
	 */
	NEVER,
	/**
	 * Only output the class name when not grouped by class.
	 */
	WHEN_NOT_GROUPED_BY_CLASS,
	/**
	 * Always output the class name.
	 */
	ALWAYS
}
