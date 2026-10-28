package ru.lab1.dealership.messaging.event;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record OrderSentForApprovalEvent(
    UUID eventId,
    UUID orderId,
    OrderType orderType,
    UUID clientId,
    UUID carId,
    UUID modelId,
    Set<UUID> componentIds,
    String traceId,
    Instant createdAt) {}
