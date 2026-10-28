package ru.lab1.dealership.domain.testdrive;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.domain.Identifiable;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.user.User;

public final class TestDriveRequest implements Identifiable {
  private final UUID id;
  private final User client;
  private final CarInStock car;
  private final LocalDateTime startDateTime;
  private final LocalDateTime createdAt;

  public TestDriveRequest(UUID id, User client, CarInStock car, LocalDateTime startDateTime) {
    this.id = Objects.requireNonNull(id, "id");
    this.client = Objects.requireNonNull(client, "client");
    this.car = Objects.requireNonNull(car, "car");
    this.startDateTime = Objects.requireNonNull(startDateTime, "startDateTime");
    this.createdAt = LocalDateTime.now();
  }

  @Override
  public UUID getId() {
    return id;
  }

  public User getClient() {
    return client;
  }

  public CarInStock getCar() {
    return car;
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
