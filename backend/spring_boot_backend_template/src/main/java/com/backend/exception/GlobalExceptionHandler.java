package com.backend.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Handles wrong email or wrong password during login.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request) {

        Map<String, Object> response = createResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /*
     * Handles custom bad request errors.
     *
     * Example:
     * Email already registered.
     * Current password is incorrect.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            BadRequestException exception,
            HttpServletRequest request) {

        Map<String, Object> response = createResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Handles resource not found errors.
     *
     * Example:
     * User not found with id.
     * User not found with email.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        Map<String, Object> response = createResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    /*
     * Handles validation errors from @Valid @RequestBody.
     *
     * Example:
     * Invalid email.
     * Empty name.
     * Password length error.
     * Invalid phone number.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Please provide valid input");
        response.put("fieldErrors", fieldErrors);
        response.put("path", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Handles validation errors from @RequestParam.
     *
     * This is useful because AuthController uses:
     *
     * @Validated
     * @RequestParam
     * @NotBlank
     * @Pattern
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request) {

        Map<String, String> validationErrors =
                new LinkedHashMap<>();

        exception.getConstraintViolations()
                .forEach(violation -> {

                    String propertyPath =
                            violation.getPropertyPath().toString();

                    String fieldName = propertyPath;

                    if (propertyPath.contains(".")) {
                        fieldName = propertyPath.substring(
                                propertyPath.lastIndexOf('.') + 1
                        );
                    }

                    validationErrors.put(
                            fieldName,
                            violation.getMessage()
                    );
                });

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Please provide valid input");
        response.put("fieldErrors", validationErrors);
        response.put("path", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Handles invalid JSON request values.
     *
     * Example:
     * Invalid role:
     * "role": "abcd"
     *
     * Unknown login field:
     * "role": "abcd"
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        String message = "Invalid request body";

        Throwable cause = exception.getCause();

        if (cause instanceof UnrecognizedPropertyException propertyException) {

            message = "Unknown field '"
                    + propertyException.getPropertyName()
                    + "' is not allowed";
        } else if (cause instanceof InvalidFormatException formatException) {

            if (formatException.getTargetType().isEnum()) {

                Object[] allowedValues =
                        formatException.getTargetType()
                                .getEnumConstants();

                StringBuilder values =
                        new StringBuilder();

                for (int i = 0; i < allowedValues.length; i++) {

                    values.append(allowedValues[i]);

                    if (i < allowedValues.length - 1) {
                        values.append(", ");
                    }
                }

                message = "Invalid value. Allowed values are: "
                        + values;
            } else {

                message = "Invalid value provided for one or more fields";
            }
        }

        Map<String, Object> response = createResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Handles all unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception exception,
            HttpServletRequest request) {

        exception.printStackTrace();

        Map<String, Object> response = createResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /*
     * Common response creation method.
     */
    private Map<String, Object> createResponse(
            HttpStatus status,
            String message,
            String path) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        response.put("path", path);

        return response;
    }
}