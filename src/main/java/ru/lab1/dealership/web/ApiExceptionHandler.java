package ru.lab1.dealership.web;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
import ru.lab1.dealership.domain.exception.GrpcServiceUnavailableException;
import ru.lab1.dealership.domain.exception.IncompatibleComponentException;
import ru.lab1.dealership.web.dto.ErrorResponse;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException exception) {
    return build(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(GrpcServiceUnavailableException.class)
  public ResponseEntity<ErrorResponse> handleGrpcUnavailable(
      GrpcServiceUnavailableException exception) {
    return build(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
  }

  @ExceptionHandler({
    DomainValidationException.class,
    IncompatibleComponentException.class,
    IllegalStateException.class,
    MethodArgumentNotValidException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception exception) {
    return build(HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message));
  }
}
