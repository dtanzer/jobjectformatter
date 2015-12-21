package net.davidtanzer.jobjectformatter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormattedTransitively {
	public static enum TransitiveInclude {
		ALLOWED, DISALLOWED, ALWAYS
	}

	TransitiveInclude value() default TransitiveInclude.ALLOWED;
}
