package ru.lab1.dealership.domain.order;

public enum CommonOrderStatus {
  CREATED,
  APPROVED_BY_MANAGER,
  WAITING_PAYMENT,
  PAID,
  READY_FOR_PICKUP,
  COMPLETED,
  CANCELLED
}
