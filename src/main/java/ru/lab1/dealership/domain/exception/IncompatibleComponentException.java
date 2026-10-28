package ru.lab1.dealership.domain.exception;

public class IncompatibleComponentException extends RuntimeException {
  public IncompatibleComponentException(String message) {
    super(message);
  }
}
