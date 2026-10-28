package ru.lab1.dealership.web.dto;

import java.util.Set;
import java.util.UUID;

public record AssemblyOrderRequest(
    UUID sourceOrderId,
    String sourceOrderType,
    UUID carId,
    UUID modelId,
    Set<UUID> requiredComponentIds,
    UUID warehouseEmployeeId,
    String status) {}
