package ru.lab1.dealership.domain.order;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.domain.Identifiable;
import ru.lab1.dealership.domain.config.CarConfiguration;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.user.User;

public final class CustomOrder implements Identifiable {
  private final UUID id;
  private final User client;
  private final User manager;
  private final CarConfiguration configuration;
  private final LocalDateTime createdAt;
  private CustomOrderStatus status;

  public CustomOrder(UUID id, User client, User manager, CarConfiguration configuration) {
    this.id = Objects.requireNonNull(id, "id");
    this.client = Objects.requireNonNull(client, "client");
    this.manager = Objects.requireNonNull(manager, "manager");
    this.configuration = Objects.requireNonNull(configuration, "configuration");
    this.createdAt = LocalDateTime.now();
    this.status = CustomOrderStatus.CREATED;
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

  public CarConfiguration getConfiguration() {
    return configuration;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public CustomOrderStatus getStatus() {
    return status;
  }

  public void approveByWarehouse() {
    ensureStatus(CustomOrderStatus.CREATED);
    status = CustomOrderStatus.APPROVED_BY_WAREHOUSE;
  }

  public void moveToWaitingPayment() {
    ensureStatus(CustomOrderStatus.APPROVED_BY_WAREHOUSE);
    status = CustomOrderStatus.WAITING_PAYMENT;
  }

  public void markPaid() {
    ensureStatus(CustomOrderStatus.WAITING_PAYMENT);
    status = CustomOrderStatus.PAID;
  }

  public void moveToWaitingDelivery() {
    ensureStatus(CustomOrderStatus.PAID);
    status = CustomOrderStatus.WAITING_FOR_DELIVERY;
  }

  public void markReadyForPickup() {
    ensureStatus(CustomOrderStatus.WAITING_FOR_DELIVERY);
    status = CustomOrderStatus.READY_FOR_PICKUP;
  }

  public void complete() {
    ensureStatus(CustomOrderStatus.READY_FOR_PICKUP);
    status = CustomOrderStatus.COMPLETED;
  }

  public void cancel() {
    if (status == CustomOrderStatus.COMPLETED || status == CustomOrderStatus.CANCELLED) {
      throw new DomainValidationException("Order cannot be cancelled from status " + status + '.');
    }
    status = CustomOrderStatus.CANCELLED;
  }

  private void ensureStatus(CustomOrderStatus expected) {
    if (status != expected) {
      throw new DomainValidationException(
          "Invalid order transition from " + status + " to " + expected + '.');
    }
  }
}
