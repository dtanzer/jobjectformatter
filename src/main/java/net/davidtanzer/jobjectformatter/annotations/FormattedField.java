package net.davidtanzer.jobjectformatter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormattedField {
	FormattedFieldType value() default FormattedFieldType.ALWAYS;
	FormattedFieldType transitive() default FormattedFieldType.NEVER;
}
