package ru.lab1.dealership.domain.exception;

public class GrpcServiceUnavailableException extends RuntimeException {
  public GrpcServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
