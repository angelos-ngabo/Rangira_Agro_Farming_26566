package com.raf.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
ResourceNotFoundException ex, HttpServletRequest request) {

log.error("Resource not found: {}", ex.getMessage());

ErrorResponse errorResponse = new ErrorResponse(
HttpStatus.NOT_FOUND.value(),
"Not Found",
ex.getMessage(),
request.getRequestURI()
);

return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
}

@ExceptionHandler(DuplicateResourceException.class)
public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
DuplicateResourceException ex, HttpServletRequest request) {

log.error("Duplicate resource: {}", ex.getMessage());

ErrorResponse errorResponse = new ErrorResponse(
HttpStatus.CONFLICT.value(),
"Conflict",
ex.getMessage(),
request.getRequestURI()
);

return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidationExceptions(
MethodArgumentNotValidException ex) {

Map<String, String> errors = new HashMap<>();
ex.getBindingResult().getAllErrors().forEach(error -> {
String fieldName = ((FieldError) error).getField();
String errorMessage = error.getDefaultMessage();
errors.put(fieldName, errorMessage);
});

log.error("Validation errors: {}", errors);

return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(OperationNotAllowedException.class)
public ResponseEntity<ErrorResponse> handleOperationNotAllowed(
OperationNotAllowedException ex, HttpServletRequest request) {

log.error("Operation not allowed: {}", ex.getMessage());

ErrorResponse errorResponse = new ErrorResponse(
HttpStatus.CONFLICT.value(),
"Conflict",
ex.getMessage(),
request.getRequestURI()
);

return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
}

@ExceptionHandler(UnauthorizedException.class)
public ResponseEntity<ErrorResponse> handleUnauthorizedException(
UnauthorizedException ex, HttpServletRequest request) {

log.error("Unauthorized access: {}", ex.getMessage());

ErrorResponse errorResponse = new ErrorResponse(
HttpStatus.FORBIDDEN.value(),
"Forbidden",
ex.getMessage(),
request.getRequestURI()
);

return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
}

@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
DataIntegrityViolationException ex, HttpServletRequest request) {

log.error("Data integrity violation: ", ex);

ErrorResponse errorResponse = new ErrorResponse(
HttpStatus.CONFLICT.value(),
"Conflict",
"Operation cannot be completed because the resource is referenced by other records.",
request.getRequestURI()
);

return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
}

@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGlobalException(
Exception ex, HttpServletRequest request) {

log.error("Unexpected error occurred: ", ex);

ErrorResponse errorResponse = new ErrorResponse(
HttpStatus.INTERNAL_SERVER_ERROR.value(),
"Internal Server Error",
ex.getMessage(),
request.getRequestURI()
);

return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
}
}

