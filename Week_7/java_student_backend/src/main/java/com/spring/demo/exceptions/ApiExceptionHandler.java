package com.spring.demo.exceptions;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRecordNotFoundException(
            RecordNotFoundException e) {

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // this is a simpler version to handle validation compared to the other
    // it does NOT give the client info about which fields failed validation
    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<ApiErrorResponse> handleValidationException(
    // MethodArgumentNotValidException e
    // ) {

    // ApiErrorResponse error = new ApiErrorResponse(
    // HttpStatus.BAD_REQUEST.value(),
    // "Validation failed. Please check your input.",
    // System.currentTimeMillis());

    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    // }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleArgumentException(
            MethodArgumentNotValidException e) {

        String details = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                details,
                System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            ConstraintViolationException e) {

        String details = e.getConstraintViolations().stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    String field = path.contains(".")
                            ? path.substring(path.lastIndexOf('.') + 1)
                            : path;
                    return field + ": " + violation.getMessage();
                })
                .collect(Collectors.joining("; "));

        if (details.isBlank()) {
            details = "Validation failed. Please check your input.";
        }

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                details,
                System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgsException(
            MethodArgumentTypeMismatchException e) {

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "The argument(s) provided is(are) not valid. Please only supply valid arguments.",
                System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<ApiErrorResponse> handleGenericException(Exception
    // e) {

    // ApiErrorResponse error = new ApiErrorResponse(
    // HttpStatus.INTERNAL_SERVER_ERROR.value(),
    // "An internal error ocurred.",
    // System.currentTimeMillis());

    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    // }
}
