package com.lessa.healthmonitoring.controller;

import com.lessa.healthmonitoring.dto.ApiError;
import com.lessa.healthmonitoring.service.exception.UserNotFoundException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ UserNotFoundException.class })
    public ResponseEntity<ApiError> handleUserNotFoundException(Exception ex, WebRequest request) {
        var apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), "user supplied not found");
        logger.error(ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
        var apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
        logger.error(ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
