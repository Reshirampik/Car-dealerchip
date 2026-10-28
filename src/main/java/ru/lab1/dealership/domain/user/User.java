package ru.lab1.dealership.domain.user;

import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.domain.Identifiable;
import ru.lab1.dealership.domain.exception.DomainValidationException;

public final class User implements Identifiable {
  private final UUID id;
  private String fullName;
  private UserRole role;

  public User(UUID id, String fullName, UserRole role) {
    this.id = Objects.requireNonNull(id, "id");
    this.fullName = requireNonBlank(fullName, "fullName");
    this.role = Objects.requireNonNull(role, "role");
  }

  @Override
  public UUID getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public UserRole getRole() {
    return role;
  }

  public void rename(String fullName) {
    this.fullName = requireNonBlank(fullName, "fullName");
  }

  public void changeRole(UserRole role) {
    this.role = Objects.requireNonNull(role, "role");
  }

  public boolean isClient() {
    return role == UserRole.CLIENT;
  }

  public boolean isManager() {
    return role == UserRole.MANAGER;
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new DomainValidationException(fieldName + ' ' + "cannot be blank.");
    }
    return value;
  }
}
