package ru.lab1.dealership.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import ru.lab1.dealership.domain.order.AssemblyOrderStatus;

@Entity
@Table(name = "assembly_orders")
public class AssemblyOrderEntity extends BaseEntity {
  @Column(name = "source_order_id", nullable = false)
  private UUID sourceOrderId;

  @Column(name = "source_order_type", nullable = false)
  private String sourceOrderType;

  @Column(name = "car_id")
  private UUID carId;

  @Column(name = "model_id")
  private UUID modelId;

  @Column(name = "required_component_ids", columnDefinition = "TEXT")
  private String requiredComponentIdsCsv;

  @Column(name = "warehouse_employee_id")
  private UUID warehouseEmployeeId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AssemblyOrderStatus status = AssemblyOrderStatus.CREATED;

  public UUID getSourceOrderId() {
    return sourceOrderId;
  }

  public void setSourceOrderId(UUID sourceOrderId) {
    this.sourceOrderId = sourceOrderId;
  }

  public String getSourceOrderType() {
    return sourceOrderType;
  }

  public void setSourceOrderType(String sourceOrderType) {
    this.sourceOrderType = sourceOrderType;
  }

  public UUID getCarId() {
    return carId;
  }

  public void setCarId(UUID carId) {
    this.carId = carId;
  }

  public UUID getModelId() {
    return modelId;
  }

  public void setModelId(UUID modelId) {
    this.modelId = modelId;
  }

  public Set<UUID> getRequiredComponentIds() {
    if (requiredComponentIdsCsv == null || requiredComponentIdsCsv.isBlank()) {
      return new LinkedHashSet<>();
    }
    return Arrays.stream(requiredComponentIdsCsv.split(","))
        .filter(part -> !part.isBlank())
        .map(UUID::fromString)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public void setRequiredComponentIds(Set<UUID> requiredComponentIds) {
    if (requiredComponentIds == null || requiredComponentIds.isEmpty()) {
      requiredComponentIdsCsv = null;
      return;
    }
    requiredComponentIdsCsv =
        requiredComponentIds.stream().map(UUID::toString).collect(Collectors.joining(","));
  }

  public UUID getWarehouseEmployeeId() {
    return warehouseEmployeeId;
  }

  public void setWarehouseEmployeeId(UUID warehouseEmployeeId) {
    this.warehouseEmployeeId = warehouseEmployeeId;
  }

  public AssemblyOrderStatus getStatus() {
    return status;
  }

  public void setStatus(AssemblyOrderStatus status) {
    this.status = status;
  }
}
