package com.ecommerce.project.exception;

import com.ecommerce.project.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String, String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            response.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleResourceNotFoundException(ResourceNotFoundException e){
        APIResponse message = new APIResponse();
        message.setMessage(e.getMessage());
        message.setStatus(false);
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(APIException.class)
    public ResponseEntity<?> handleAPIException(APIException e){
        APIResponse message = new APIResponse();
        message.setMessage(e.getMessage());
        message.setStatus(false);
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthException(AuthException e){
        APIResponse message = new APIResponse();
        message.setMessage(e.getMessage());
        message.setStatus(false);
        return new ResponseEntity<>(message,HttpStatus.UNAUTHORIZED);
    }
}
