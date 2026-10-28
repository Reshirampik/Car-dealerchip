package ru.lab1.dealership.domain.car;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.domain.Identifiable;
import ru.lab1.dealership.domain.exception.DomainValidationException;

public final class CarInStock implements Identifiable {
  private final UUID id;
  private final CarModel model;
  private String color;
  private BigDecimal price;

  public CarInStock(UUID id, CarModel model, String color, BigDecimal price) {
    this.id = Objects.requireNonNull(id, "id");
    this.model = Objects.requireNonNull(model, "model");
    this.color = requireNonBlank(color, "color");
    this.price = validatePrice(price);
  }

  @Override
  public UUID getId() {
    return id;
  }

  public CarModel getModel() {
    return model;
  }

  public String getColor() {
    return color;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void update(String color, BigDecimal price) {
    this.color = requireNonBlank(color, "color");
    this.price = validatePrice(price);
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new DomainValidationException(fieldName + " cannot be blank.");
    }
    return value;
  }

  private static BigDecimal validatePrice(BigDecimal price) {
    Objects.requireNonNull(price, "price");
    if (price.signum() < 0) {
      throw new DomainValidationException("price cannot be negative.");
    }
    return price;
  }
}
