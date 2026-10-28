package ru.lab1.dealership.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    ru.lab1.dealership.domain.order.CommonOrderStatus status,
    UUID clientId,
    UUID managerId,
    UUID carId,
    String carDisplayName,
    BigDecimal totalPrice,
    Instant createdAt,
    Instant updatedAt,
    boolean removed) {}
