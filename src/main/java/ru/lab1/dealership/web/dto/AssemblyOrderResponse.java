package ru.lab1.dealership.web.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record AssemblyOrderResponse(
    UUID id,
    UUID sourceOrderId,
    String sourceOrderType,
    UUID carId,
    UUID modelId,
    Set<UUID> requiredComponentIds,
    UUID warehouseEmployeeId,
    String status,
    Instant createdAt,
    Instant updatedAt,
    boolean removed) {}
