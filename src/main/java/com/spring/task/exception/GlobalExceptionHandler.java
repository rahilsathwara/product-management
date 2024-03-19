package com.spring.task.exception;

import com.spring.task.web.ApiError;
import com.spring.task.web.ResponseEntityBuilder;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append("media type is not supported. Supported media types are");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        details.add(builder.toString());

        return ResponseEntityBuilder.build(buildErrorResponse(HttpStatus.BAD_REQUEST, "media type is not supported. Supported media types are", details));
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<String>();
        details.add(ex.getMessage());

        return ResponseEntityBuilder.build(buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", details));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<String>();
        details = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + " : " + error.getDefaultMessage()).collect(Collectors.toList());

        logger.error("Validation error occurred:", ex);

        ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "validation", details);
        return ResponseEntityBuilder.build(apiError);
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<Object> handleCommonException(CommonException ce) {
        List<String> details = new ArrayList<String>();
        details.add(ce.getMessage());

        return ResponseEntityBuilder.build(buildErrorResponse(HttpStatus.NOT_FOUND, "exception", details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(Exception ex, WebRequest request) {
        List<String> details = new ArrayList<String>();
        details.add(ex.getMessage());

        return ResponseEntityBuilder.build(buildErrorResponse(HttpStatus.BAD_REQUEST, "Constraint violation", details));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(Exception ex, WebRequest request) {
        List<String> details = new ArrayList<String>();
        details.add(ex.getMessage());

        return ResponseEntityBuilder.build(buildErrorResponse(HttpStatus.CONFLICT, "Bad Credentials", details));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());

        return ResponseEntityBuilder.build(buildErrorResponse(HttpStatus.BAD_REQUEST, "Already Exist", details));
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<?> handleResourceAlreadyExistException(ResourceAlreadyExistException ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());

        return ResponseEntityBuilder.build(buildErrorResponse(HttpStatus.CONFLICT, "Resource Already Exists", details));
    }

    @ExceptionHandler(JwtTokenExpiredException.class)
    public ResponseEntity<?> handleJwtTokenExpiredException(JwtTokenExpiredException ex) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());

        return ResponseEntityBuilder.build(buildErrorResponse(HttpStatus.UNAUTHORIZED, "JWT token exception", details));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        List<String> details = new ArrayList<String>();
        details.add(ex.getLocalizedMessage());

        return ResponseEntityBuilder.build(buildErrorResponse(HttpStatus.BAD_REQUEST, "Error", details));
    }

    private ApiError buildErrorResponse(HttpStatus status, String message, List<String> details) {
        return  ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .errors(details)
                .build();
    }
}