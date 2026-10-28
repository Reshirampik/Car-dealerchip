package ru.lab1.dealership.web.dto;

import java.time.Instant;
import java.util.UUID;

public record TestDriveResponse(
    UUID id,
    UUID clientId,
    UUID carId,
    Instant startDateTime,
    Instant createdAt,
    Instant updatedAt,
    boolean removed) {}
