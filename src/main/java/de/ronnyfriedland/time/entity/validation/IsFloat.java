package de.ronnyfriedland.time.entity.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

/**
 * 
 * Validator der nur Zeichenketten zulässt, welche in eine Float Zahl
 * konvertiert werden können.
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ValidIsFloatValidator.class)
@Documented
@ReportAsSingleViolation
public @interface IsFloat {

    /**
     * 
     * Default Message
     * 
     * @return
     */
    String message() default "Value could not be parsed as float.";

    /**
     * 
     * Groups
     * 
     * @return
     */
    Class<?>[] groups() default {

    };

    /**
     * 
     * Payload
     * 
     * @return
     */
    Class<? extends Payload>[] payload() default {

    };
}
