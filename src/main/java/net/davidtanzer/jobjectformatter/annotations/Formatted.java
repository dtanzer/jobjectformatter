package net.davidtanzer.jobjectformatter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
public @interface Formatted {
	FormattedInclude value() default FormattedInclude.ANNOTATED_FIELDS;
	TransitiveInclude transitive() default TransitiveInclude.ANNOTADED_FIELDS;
}
