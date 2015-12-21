package net.davidtanzer.jobjectformatter;

public class ObjectFormatter {
	private static volatile FormattedStringGenerator generator;

	public static void configureGenerator(final FormattedStringGenerator generator) {
		ObjectFormatter.generator = generator;
	}

	public static String format(final Object object) {
		return generator.format(object);
	}
}
