package ru.lab1.dealership.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "cars_in_stock")
public class CarInStockEntity extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_id", nullable = false)
  private CarModelEntity model;

  @Column(nullable = false)
  private String color;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(name = "available_for_test_drive", nullable = false)
  private boolean availableForTestDrive;

  public CarInStockEntity() {}

  public CarModelEntity getModel() {
    return model;
  }

  public void setModel(CarModelEntity model) {
    this.model = model;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public boolean isAvailableForTestDrive() {
    return availableForTestDrive;
  }

  public void setAvailableForTestDrive(boolean availableForTestDrive) {
    this.availableForTestDrive = availableForTestDrive;
  }
}
