/*
 * Copyright (c) 2010 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, D-01129 Dresden, Germany
 * All rights reserved.
 *
 * $Id$
 */
package de.ronnyfriedland.time.entity.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 
 * Überprüft Zeichenketten ob diese nicht null, nicht leer und mehr als nur
 * Leerzeichen enthalten (NotBlank).
 */
public class ValidIsFloatValidator implements ConstraintValidator<IsFloat, String> {

    /**
     * 
     * {@inheritDoc}
     * 
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final IsFloat isFloat) {
        // initialize
    }

    /**
     * 
     * {@inheritDoc}
     * 
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
     *      javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        boolean isValid = false;
        ;
        if (null != value) {
            String result = value.replaceAll(",", ".").trim();
            try {
                Float floatResult = Float.valueOf(result);
                isValid = (0 < floatResult);
            } catch (NumberFormatException e) {
                isValid = false;
            }
        }
        return isValid;
    }
}