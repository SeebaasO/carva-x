package com.example.carva_x.controller;

import com.example.carva_x.exception.BadRequestException;
import com.example.carva_x.exception.ErrorResponse;
import com.example.carva_x.exception.NotFoundException;
import com.example.carva_x.model.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNotFound(NotFoundException exception) {
        return build(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadRequest(BadRequestException exception) {
        return build(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Solicitud inválida");
        return build(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiResponse<ErrorResponse>> build(String message, HttpStatus status) {
        ErrorResponse error = ErrorResponse.builder()
                .message(message)
                .statusCode(status.value())
                .build();

        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .result(false)
                .data(error)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
