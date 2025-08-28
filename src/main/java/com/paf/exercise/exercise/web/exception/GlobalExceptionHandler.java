package com.paf.exercise.exercise.web.exception;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = GlobalException.class)
    protected ResponseEntity<ApiError> handleConflict(
            GlobalException ex) {

        ApiError apiError = ApiError
                .builder()
                .message(ex.getMessage())
                .status(ex.getError().getHttpStatus().value())
                .timestamp(new Date())
                .code(ex.getError().getCode())
                .data(ex.getData())
                .build();

        return ResponseEntity.status(ex.getError().getHttpStatus().value()).body(apiError);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<ApiError> handleConflict(
            ConstraintViolationException ex, WebRequest request) {

        ApiError apiError = ApiError
                .builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(new Date())
                .code(request.getDescription(false))
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(apiError);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<ApiError> handleConflict(
            Exception ex, WebRequest request) {

        ApiError apiError = ApiError
                .builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(new Date())
                .code(request.getDescription(false))
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(apiError);
    }


}
