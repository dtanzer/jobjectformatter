package net.davidtanzer.jobjectformatter;

import net.davidtanzer.jobjectformatter.formatter.ObjectStringFormatter;
import net.davidtanzer.jobjectformatter.formatter.SimpleFormatter;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfo;
import net.davidtanzer.jobjectformatter.typeinfo.TypeInfoCache;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesInfo;
import net.davidtanzer.jobjectformatter.valuesinfo.ObjectValuesCompiler;

public class FormattedStringGenerator {
	private final TypeInfoCache typeInfoCache;
	private final ObjectValuesCompiler objectValuesCompiler;
	private final ObjectStringFormatter toStringFormatter;

	public FormattedStringGenerator() {
		this(new SimpleFormatter(), new TypeInfoCache(), new ObjectValuesCompiler());
	}

	public FormattedStringGenerator(final ObjectStringFormatter toStringFormatter) {
		this(toStringFormatter, new TypeInfoCache(), new ObjectValuesCompiler());
	}

	FormattedStringGenerator(final ObjectStringFormatter objectStringFormatter, final TypeInfoCache typeInfoCache, final ObjectValuesCompiler objectValuesCompiler) {
		if(objectStringFormatter == null) {
			throw new IllegalArgumentException("Parameter objectStringFormatter must not be null!");
		}
		assert typeInfoCache != null : "Parameter typeInfoCache must not be null!";
		assert objectValuesCompiler != null : "Parameter objectValuesCompiler must not be null!";

		this.toStringFormatter = objectStringFormatter;
		this.typeInfoCache = typeInfoCache;
		this.objectValuesCompiler = objectValuesCompiler;
	}

	public String format(final Object object) {
		if(object==null) {
			throw new IllegalArgumentException("Parameter object must not be null!");
		}

		final TypeInfo typeInfo = typeInfoCache.typeInfoFor(object.getClass());
		final ObjectValuesInfo objectValuesInfo = objectValuesCompiler.compileToStringInfo(typeInfo, object);

		return toStringFormatter.format(objectValuesInfo);
	}
}
