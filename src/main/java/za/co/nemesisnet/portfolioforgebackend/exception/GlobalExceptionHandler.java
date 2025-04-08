package za.co.nemesisnet.portfolioforgebackend.exception;


import io.micrometer.common.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // For 403 Forbidden
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.ErrorDetails;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice // Indicates this class handles exceptions globally across controllers
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler { // Extend for common web exceptions

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // --- Handle Specific Exceptions ---

    /**
     * Handles ResourceNotFoundException (our custom exception).
     * Returns HTTP 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            WebRequest webRequest) {

        log.warn("Resource not found: {}", exception.getMessage()); // Log as warning
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false) // Get request URI without params
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles AccessDeniedException (from Spring Security, e.g., wrong role).
     * Returns HTTP 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(
            AccessDeniedException exception,
            WebRequest webRequest) {

        log.warn("Access denied: {}", exception.getMessage()); // Log as warning
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Access Denied: You do not have permission to access this resource.", // User-friendly message
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles RuntimeException (e.g., from slug conflict check).
     * Consider creating more specific custom exceptions for business logic errors.
     * Returns HTTP 400 Bad Request.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> handleBusinessLogicExceptions(
            RuntimeException exception, // Catching generic Runtime, be careful
            WebRequest webRequest) {

        // Only log as error if it's unexpected. Business errors maybe log as WARN.
        log.error("Business logic exception: {}", exception.getMessage(), exception);
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                exception.getMessage(), // Use message from exception (e.g., "Slug already taken")
                webRequest.getDescription(false)
        );
        // Return 400 Bad Request for general business errors like duplicates
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }


    // --- Handle Global/Unexpected Exceptions ---

    /**
     * Handles generic Exceptions (catch-all for unexpected errors).
     * Returns HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception exception,
            WebRequest webRequest) {

        log.error("An unexpected error occurred: {}", exception.getMessage(), exception); // Log full stack trace
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "An internal server error occurred.", // Generic message for user
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // --- Override method from ResponseEntityExceptionHandler for Validation Errors ---

    /**
     * Handles validation errors (@Valid annotation on @RequestBody).
     * Triggered by MethodArgumentNotValidException.
     * Returns HTTP 400 Bad Request.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        log.warn("Validation error: {}", ex.getMessage());
        // Collect validation errors into a more readable format
        List<String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Validation Failed", // General message
                request.getDescription(false),
                validationErrors // Include specific field errors
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

}
