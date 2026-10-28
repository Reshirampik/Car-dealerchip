package ru.lab1.dealership.web.dto;

public record UserRequest(
    String fullName, ru.lab1.dealership.domain.user.UserRole role, String keycloakId) {
  public UserRequest(String fullName, ru.lab1.dealership.domain.user.UserRole role) {
    this(fullName, role, null);
  }
}
