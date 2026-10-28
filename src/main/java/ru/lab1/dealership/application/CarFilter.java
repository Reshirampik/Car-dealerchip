package ru.lab1.dealership.application;

import java.math.BigDecimal;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;

public record CarFilter(
    BigDecimal minPrice,
    BigDecimal maxPrice,
    String brand,
    String model,
    BodyType bodyType,
    FuelType fuelType,
    Integer minEnginePowerHp,
    Integer maxEnginePowerHp,
    BigDecimal minEngineVolumeLiters,
    BigDecimal maxEngineVolumeLiters,
    GearboxType gearboxType,
    DriveType driveType,
    String color) {}
