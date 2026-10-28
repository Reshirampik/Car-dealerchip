package ru.lab1.dealership.domain.order;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.domain.Identifiable;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.user.User;

public final class CommonOrder implements Identifiable {
  private final UUID id;
  private final User client;
  private final User manager;
  private final CarInStock car;
  private final LocalDateTime createdAt;
  private CommonOrderStatus status;

  public CommonOrder(UUID id, User client, User manager, CarInStock car) {
    this.id = Objects.requireNonNull(id, "id");
    this.client = Objects.requireNonNull(client, "client");
    this.manager = Objects.requireNonNull(manager, "manager");
    this.car = Objects.requireNonNull(car, "car");
    this.createdAt = LocalDateTime.now();
    this.status = CommonOrderStatus.CREATED;
  }

  @Override
  public UUID getId() {
    return id;
  }

  public User getClient() {
    return client;
  }

  public User getManager() {
    return manager;
  }

  public CarInStock getCar() {
    return car;
  }

  public CommonOrderStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void approveByManager() {
    ensureStatus(CommonOrderStatus.CREATED);
    status = CommonOrderStatus.APPROVED_BY_MANAGER;
  }

  public void moveToWaitingPayment() {
    ensureStatus(CommonOrderStatus.APPROVED_BY_MANAGER);
    status = CommonOrderStatus.WAITING_PAYMENT;
  }

  public void markPaid() {
    ensureStatus(CommonOrderStatus.WAITING_PAYMENT);
    status = CommonOrderStatus.PAID;
  }

  public void markReadyForPickup() {
    ensureStatus(CommonOrderStatus.PAID);
    status = CommonOrderStatus.READY_FOR_PICKUP;
  }

  public void complete() {
    ensureStatus(CommonOrderStatus.READY_FOR_PICKUP);
    status = CommonOrderStatus.COMPLETED;
  }

  public void cancel() {
    if (status == CommonOrderStatus.COMPLETED || status == CommonOrderStatus.CANCELLED) {
      throw new DomainValidationException("Order cannot be cancelled from status " + status + '.');
    }
    status = CommonOrderStatus.CANCELLED;
  }

  private void ensureStatus(CommonOrderStatus expected) {
    if (status != expected) {
      throw new DomainValidationException(
          "Invalid order transition from " + status + " to " + expected + '.');
    }
  }
}
