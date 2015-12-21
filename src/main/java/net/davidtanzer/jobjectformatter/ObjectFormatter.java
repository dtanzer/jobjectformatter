package net.davidtanzer.jobjectformatter;

import java.util.concurrent.atomic.AtomicReference;

public class ObjectFormatter {
	private static AtomicReference<FormattedStringGenerator> generator = new AtomicReference<>(new FormattedStringGenerator());

	public static void configureGenerator(final FormattedStringGenerator generator) {
		ObjectFormatter.generator.set(generator);
	}

	public static String format(final Object object) {
		return generator.get().format(object);
	}
}
