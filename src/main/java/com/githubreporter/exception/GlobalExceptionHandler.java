package com.githubreporter.exception;

import com.githubreporter.model.ReportModels.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Catches GitHub API errors (e.g., 404 Org Not Found, 401 Bad Token)
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleGitHubError(WebClientResponseException ex) {
        String message = "GitHub API Error: " + ex.getStatusText();
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            message = "Organization not found on GitHub.";
        } else if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            message = "Invalid GitHub Token. Please check your application.properties.";
        }
        
        ErrorResponse error = new ErrorResponse(message, ex.getStatusCode().value(), System.currentTimeMillis());
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    // 2. Catches any other unexpected Java crashes
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "An unexpected error occurred: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}