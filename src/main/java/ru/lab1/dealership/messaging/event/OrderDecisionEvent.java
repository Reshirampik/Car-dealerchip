package ru.lab1.dealership.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record OrderDecisionEvent(
    UUID eventId,
    UUID orderId,
    OrderType orderType,
    boolean approved,
    String traceId,
    String reason,
    Instant createdAt) {}
