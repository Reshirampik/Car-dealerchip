package ru.lab1.dealership.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CustomOrderResponse(
    UUID id,
    ru.lab1.dealership.domain.order.CustomOrderStatus status,
    UUID clientId,
    UUID managerId,
    UUID modelId,
    String modelDisplayName,
    BigDecimal totalPrice,
    List<ConfigurationSelectionResponse> selections,
    Instant createdAt,
    Instant updatedAt,
    boolean removed) {}
