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

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Validator der nur nicht leere Zeichenketten zulässt. Siehe {@link StringUtils}.isBlank().
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ValidNotBlankValidator.class)
@Documented
@ReportAsSingleViolation
public @interface NotBlank {

    /**
     * 
     * Default Message
     * 
     * @return
     */
    String message() default "Null, empty or whitspace only is an invalid value.";

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
