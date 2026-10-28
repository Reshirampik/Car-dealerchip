package ru.lab1.dealership.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record CarModelResponse(
    UUID id,
    String brand,
    String modelName,
    ru.lab1.dealership.domain.car.BodyType bodyType,
    ru.lab1.dealership.domain.car.FuelType fuelType,
    int enginePowerHp,
    BigDecimal engineVolumeLiters,
    ru.lab1.dealership.domain.car.GearboxType gearboxType,
    ru.lab1.dealership.domain.car.DriveType driveType,
    BigDecimal basePrice,
    Set<ru.lab1.dealership.domain.config.ComponentType> requiredComponentTypes,
    Instant createdAt,
    Instant updatedAt,
    boolean removed) {}
