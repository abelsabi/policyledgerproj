package com.hdfclife.ledger.config;

import com.hdfclife.ledger.dto.ErrorResponse;
import com.hdfclife.ledger.exception.ClaimNotFoundException;
import com.hdfclife.ledger.exception.DuplicatePolicyException;
import com.hdfclife.ledger.exception.InvalidRequestException;
import com.hdfclife.ledger.exception.PolicyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse(
                400,
                "Bad Request",
                "Validation failed",
                fields
        ));
    }

    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePolicyNotFound(PolicyNotFoundException ex) {
        return notFound(ex.getMessage());
    }

    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClaimNotFound(ClaimNotFoundException ex) {
        return notFound(ex.getMessage());
    }

    @ExceptionHandler(DuplicatePolicyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicatePolicyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                409,
                "Conflict",
                ex.getMessage(),
                Map.of()
        ));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(InvalidRequestException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                400,
                "Bad Request",
                ex.getMessage(),
                Map.of()
        ));
    }

    private ResponseEntity<ErrorResponse> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                404,
                "Not Found",
                message,
                Map.of()
        ));
    }
}
