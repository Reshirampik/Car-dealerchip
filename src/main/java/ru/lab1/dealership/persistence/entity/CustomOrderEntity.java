package ru.lab1.dealership.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import ru.lab1.dealership.domain.order.CustomOrderStatus;

@Entity
@Table(name = "custom_orders")
public class CustomOrderEntity extends BaseEntity {
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CustomOrderStatus status = CustomOrderStatus.CREATED;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  private UserEntity client;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manager_id", nullable = false)
  private UserEntity manager;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_id", nullable = false)
  private CarModelEntity model;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "custom_order_options",
      joinColumns = @JoinColumn(name = "custom_order_id"),
      inverseJoinColumns = @JoinColumn(name = "component_option_id"))
  private Set<ComponentOptionEntity> selectedOptions = new LinkedHashSet<>();

  @Column(name = "total_price", nullable = false)
  private BigDecimal totalPrice;

  public CustomOrderEntity() {}

  public CustomOrderStatus getStatus() {
    return status;
  }

  public void setStatus(CustomOrderStatus status) {
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

  public CarModelEntity getModel() {
    return model;
  }

  public void setModel(CarModelEntity model) {
    this.model = model;
  }

  public Set<ComponentOptionEntity> getSelectedOptions() {
    return selectedOptions;
  }

  public void setSelectedOptions(Set<ComponentOptionEntity> selectedOptions) {
    this.selectedOptions = selectedOptions;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  public void approveByWarehouse() {
    assertStatus(CustomOrderStatus.CREATED);
    status = CustomOrderStatus.APPROVED_BY_WAREHOUSE;
  }

  public void moveToWaitingPayment() {
    assertStatus(CustomOrderStatus.APPROVED_BY_WAREHOUSE);
    status = CustomOrderStatus.WAITING_PAYMENT;
  }

  public void markPaid() {
    assertStatus(CustomOrderStatus.WAITING_PAYMENT);
    status = CustomOrderStatus.PAID;
  }

  public void markWaitingDelivery() {
    assertStatus(CustomOrderStatus.PAID);
    status = CustomOrderStatus.WAITING_FOR_DELIVERY;
  }

  public void markReadyForPickup() {
    assertStatus(CustomOrderStatus.WAITING_FOR_DELIVERY);
    status = CustomOrderStatus.READY_FOR_PICKUP;
  }

  public void complete() {
    assertStatus(CustomOrderStatus.READY_FOR_PICKUP);
    status = CustomOrderStatus.COMPLETED;
  }

  public void cancel() {
    if (status == CustomOrderStatus.COMPLETED) {
      throw new IllegalStateException("Completed order cannot be cancelled.");
    }
    status = CustomOrderStatus.CANCELLED;
  }

  private void assertStatus(CustomOrderStatus expected) {
    if (status != expected) {
      throw new IllegalStateException("Unexpected custom order status: " + status);
    }
  }
}
