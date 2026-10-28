package ru.lab1.dealership.domain.config;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import ru.lab1.dealership.domain.Identifiable;
import ru.lab1.dealership.domain.exception.DomainValidationException;

public final class ComponentOption implements Identifiable {
  private final UUID id;
  private final ComponentType type;
  private final String name;
  private final BigDecimal deltaPrice;
  private final Set<UUID> compatibleModelIds;

  public ComponentOption(
      UUID id,
      ComponentType type,
      String name,
      BigDecimal deltaPrice,
      Set<UUID> compatibleModelIds) {
    this.id = Objects.requireNonNull(id, "id");
    this.type = Objects.requireNonNull(type, "type");
    this.name = requireNonBlank(name, "name");
    this.deltaPrice = validatePrice(deltaPrice);
    this.compatibleModelIds =
        Set.copyOf(Objects.requireNonNull(compatibleModelIds, "compatibleModelIds"));
    if (this.compatibleModelIds.isEmpty()) {
      throw new DomainValidationException("compatibleModelIds cannot be empty.");
    }
  }

  @Override
  public UUID getId() {
    return id;
  }

  public ComponentType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getDeltaPrice() {
    return deltaPrice;
  }

  public Set<UUID> getCompatibleModelIds() {
    return compatibleModelIds;
  }

  public boolean isCompatible(UUID modelId) {
    return compatibleModelIds.contains(modelId);
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new DomainValidationException(fieldName + " cannot be blank.");
    }
    return value;
  }

  private static BigDecimal validatePrice(BigDecimal price) {
    Objects.requireNonNull(price, "deltaPrice");
    return price;
  }
}
