package ru.lab1.dealership.web.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String keycloakId,
    String fullName,
    ru.lab1.dealership.domain.user.UserRole role,
    Instant createdAt,
    Instant updatedAt,
    boolean removed) {
  public UserResponse(
      UUID id,
      String fullName,
      ru.lab1.dealership.domain.user.UserRole role,
      Instant createdAt,
      Instant updatedAt,
      boolean removed) {
    this(id, null, fullName, role, createdAt, updatedAt, removed);
  }
}
