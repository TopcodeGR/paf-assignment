package com.paf.exercise.exercise.web.exception.error;

import org.springframework.http.HttpStatus;


public class InsufficientAuthoritiesError extends PafError {
    public InsufficientAuthoritiesError() {
        super("INSUFFICIENT_AUTHORITIES", HttpStatus.FORBIDDEN,"Account has insufficient authorities");
    }
}

