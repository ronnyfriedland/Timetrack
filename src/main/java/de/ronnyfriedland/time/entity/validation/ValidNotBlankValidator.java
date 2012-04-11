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

import org.apache.commons.lang.StringUtils;

/**
 * Überprüft Zeichenketten ob diese nicht null, nicht leer und mehr als nur Leerzeichen enthalten (NotBlank).
 * 
 * @author Ronny Friedland
 */
public class ValidNotBlankValidator implements ConstraintValidator<NotBlank, String> {

    /**
     * 
     * {@inheritDoc}
     * 
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final NotBlank notBlank) {
        // initialize
    }

    /**
     * 
     * {@inheritDoc}
     * 
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return StringUtils.isNotBlank(value);
    }
}
