package ru.lab1.dealership.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ComponentOptionResponse(
    UUID id,
    ru.lab1.dealership.domain.config.ComponentType componentType,
    String optionName,
    BigDecimal deltaPrice,
    Set<UUID> compatibleModelIds,
    Instant createdAt,
    Instant updatedAt,
    boolean removed) {}
