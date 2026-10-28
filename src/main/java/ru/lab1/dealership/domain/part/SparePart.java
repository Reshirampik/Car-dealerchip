package ru.lab1.dealership.domain.part;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import ru.lab1.dealership.domain.Identifiable;
import ru.lab1.dealership.domain.exception.DomainValidationException;

public final class SparePart implements Identifiable {
  private final UUID id;
  private String name;
  private BigDecimal price;
  private Set<UUID> compatibleModelIds;

  public SparePart(UUID id, String name, BigDecimal price, Set<UUID> compatibleModelIds) {
    this.id = Objects.requireNonNull(id, "id");
    this.name = requireNonBlank(name, "name");
    this.price = validatePrice(price);
    this.compatibleModelIds = validateCompatibleModels(compatibleModelIds);
  }

  @Override
  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Set<UUID> getCompatibleModelIds() {
    return compatibleModelIds;
  }

  public boolean isCompatible(UUID modelId) {
    return compatibleModelIds.contains(modelId);
  }

  public void update(String name, BigDecimal price, Set<UUID> compatibleModelIds) {
    this.name = requireNonBlank(name, "name");
    this.price = validatePrice(price);
    this.compatibleModelIds = validateCompatibleModels(compatibleModelIds);
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

  private static Set<UUID> validateCompatibleModels(Set<UUID> compatibleModelIds) {
    var copy = Set.copyOf(Objects.requireNonNull(compatibleModelIds, "compatibleModelIds"));
    if (copy.isEmpty()) {
      throw new DomainValidationException("compatibleModelIds cannot be empty.");
    }
    return copy;
  }
}
