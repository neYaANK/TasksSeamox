/*
 * CustomResponseExceptionHandler.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CustomResponseExceptionHandler {
    /**
     * Creates ValidationErrorResponse with status 400 and returns all
     * validation violations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onArgumentNotValid(MethodArgumentNotValidException e){
        ValidationErrorResponse response = new ValidationErrorResponse();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            response.getViolations().add(
                    new ValidationError(fieldError.getField(),
                            fieldError.getDefaultMessage()));
        }
        return response;
    }
}
