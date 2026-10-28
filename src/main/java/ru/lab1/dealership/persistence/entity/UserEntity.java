package ru.lab1.dealership.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import ru.lab1.dealership.domain.user.UserRole;

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {
  @Column(name = "keycloak_id")
  private String keycloakId;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  public UserEntity() {}

  public UserEntity(String fullName, UserRole role) {
    this.fullName = fullName;
    this.role = role;
  }

  public UserEntity(String keycloakId, String fullName, UserRole role) {
    this.keycloakId = keycloakId;
    this.fullName = fullName;
    this.role = role;
  }

  public String getKeycloakId() {
    return keycloakId;
  }

  public void setKeycloakId(String keycloakId) {
    this.keycloakId = keycloakId;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }
}
