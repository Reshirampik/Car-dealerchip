package ru.lab1.dealership.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import ru.lab1.dealership.domain.order.CommonOrderStatus;

@Entity
@Table(name = "common_orders")
public class CommonOrderEntity extends BaseEntity {
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CommonOrderStatus status = CommonOrderStatus.CREATED;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  private UserEntity client;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manager_id", nullable = false)
  private UserEntity manager;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "car_id", nullable = false)
  private CarInStockEntity car;

  @Column(name = "total_price", nullable = false)
  private BigDecimal totalPrice;

  public CommonOrderEntity() {}

  public CommonOrderStatus getStatus() {
    return status;
  }

  public void setStatus(CommonOrderStatus status) {
    this.status = status;
  }

  public UserEntity getClient() {
    return client;
  }

  public void setClient(UserEntity client) {
    this.client = client;
  }

  public UserEntity getManager() {
    return manager;
  }

  public void setManager(UserEntity manager) {
    this.manager = manager;
  }

  public CarInStockEntity getCar() {
    return car;
  }

  public void setCar(CarInStockEntity car) {
    this.car = car;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  public void approveByManager() {
    assertStatus(CommonOrderStatus.CREATED);
    status = CommonOrderStatus.APPROVED_BY_MANAGER;
  }

  public void moveToWaitingPayment() {
    assertStatus(CommonOrderStatus.APPROVED_BY_MANAGER);
    status = CommonOrderStatus.WAITING_PAYMENT;
  }

  public void markPaid() {
    assertStatus(CommonOrderStatus.WAITING_PAYMENT);
    status = CommonOrderStatus.PAID;
  }

  public void markReadyForPickup() {
    assertStatus(CommonOrderStatus.PAID);
    status = CommonOrderStatus.READY_FOR_PICKUP;
  }

  public void complete() {
    assertStatus(CommonOrderStatus.READY_FOR_PICKUP);
    status = CommonOrderStatus.COMPLETED;
  }

  public void cancel() {
    if (status == CommonOrderStatus.COMPLETED) {
      throw new IllegalStateException("Completed order cannot be cancelled.");
    }
    status = CommonOrderStatus.CANCELLED;
  }

  private void assertStatus(CommonOrderStatus expected) {
    if (status != expected) {
      throw new IllegalStateException("Unexpected common order status: " + status);
    }
  }
}
