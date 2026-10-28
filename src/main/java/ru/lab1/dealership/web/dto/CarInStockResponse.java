package ru.lab1.dealership.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CarInStockResponse(
    UUID id,
    UUID modelId,
    String modelDisplayName,
    String color,
    BigDecimal price,
    boolean availableForTestDrive,
    Instant createdAt,
    Instant updatedAt,
    boolean removed) {}
