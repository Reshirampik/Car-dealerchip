package ru.lab1.dealership.domain.order;

public enum CustomOrderStatus {
  CREATED,
  APPROVED_BY_WAREHOUSE,
  WAITING_PAYMENT,
  PAID,
  WAITING_FOR_DELIVERY,
  READY_FOR_PICKUP,
  COMPLETED,
  CANCELLED
}
