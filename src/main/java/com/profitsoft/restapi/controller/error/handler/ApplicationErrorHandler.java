package com.profitsoft.restapi.controller.error.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.profitsoft.restapi.dto.exception.ExceptionDto;
import com.profitsoft.restapi.exception.ApiException;
import com.profitsoft.restapi.exception.JsonFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApplicationErrorHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleNoSuchElementException(NoSuchElementException exception) {
        return ExceptionDto.of(exception.getMessage());
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class,
            JsonFormatException.class,
            IOException.class,
            JsonParseException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleBadRequest(Exception exception) {
        return ExceptionDto.of(exception.getMessage());
    }

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleInternalError(ApiException exception) {
        return ExceptionDto.of(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleBadRequest(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        StringBuilder errorMessage = new StringBuilder("Validation failed for: ");

        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getField())
                    .append(" - ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        return ExceptionDto.of(errorMessage.toString());
    }
}