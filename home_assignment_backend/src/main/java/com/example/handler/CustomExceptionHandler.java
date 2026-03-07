package com.example.handler;

import com.example.exception.ValidationException;
import com.example.model.response.SingleValueResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    private static final String GENERAL_EXCEPTION_MESSAGE = "Oops! Something went wrong!";


    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationExceptions(ValidationException validationException) {
        log.error("Validation error during node operation: ", validationException);
        return ResponseEntity.badRequest().body(validationException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleExceptionsGeneral(Exception e){
        log.error("Unexpected error during node operation: ", e);
        return ResponseEntity.internalServerError().body((GENERAL_EXCEPTION_MESSAGE));
    }

}
