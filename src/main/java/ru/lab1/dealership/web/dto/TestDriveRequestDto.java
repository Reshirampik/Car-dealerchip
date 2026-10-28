package ru.lab1.dealership.web.dto;

import java.time.Instant;
import java.util.UUID;

public record TestDriveRequestDto(UUID clientId, UUID carId, Instant startDateTime) {}
