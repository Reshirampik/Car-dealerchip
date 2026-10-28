package ru.lab1.dealership.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record SparePartResponse(
    UUID id,
    String partName,
    BigDecimal price,
    Set<UUID> compatibleModelIds,
    Instant createdAt,
    Instant updatedAt,
    boolean removed) {}
