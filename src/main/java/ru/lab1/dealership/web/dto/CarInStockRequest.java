package ru.lab1.dealership.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CarInStockRequest(
    UUID modelId, String color, BigDecimal price, boolean availableForTestDrive) {}
